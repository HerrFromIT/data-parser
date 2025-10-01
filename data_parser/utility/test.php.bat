<?php

require_once 'CompanyDataParser.php';
require_once 'DBconnect.php';
require_once 'StringBuilder.php';

$startTime = microtime(true);

$landsInGerman = json_decode(file_get_contents('landsInGerman.json'), true)['landsInGerman'];
$landsInEnglish = json_decode(file_get_contents('landsInEnglish.json'), true)['landsInEnglish'];

$isLandInGerman = true;

$lands = $isLandInGerman ? $landsInGerman : $landsInEnglish;
$folder = $isLandInGerman ? 'cache_de' : 'cache_en';

$executeTimeOfLands = [];

$url = "https://overpass-api.de/api/interpreter";

$parser = new CompanyDataParser();
$fetchedData = null;

// Array für alle Ausgabe-Strings
$outputStrings = [];

$count = 0;

// Funktion zum Verarbeiten der Länder
function processLands($lands, $url, $folder, $parser, $startTime)
{
    global $executeTimeOfLands, $outputStrings;

    $results = [];
    $totalDataCount = 0;

    foreach ($lands as $land) {
        $startTimeForLand = microtime(true);

        $landResult = [
            'land' => $land,
            'dataSource' => '',
            'dataCount' => 0,
            'executionTime' => 0,
            'status' => 'success'
        ];

        $query = '[out:json];area["name:de"="' . $land . '"]["admin_level"="2"]->.land;(node["amenity"="hospital"](area.land);way["amenity"="hospital"](area.land);relation["amenity"="hospital"](area.land););out;';
        $urlToFetch = $url . "?data=" . urlencode($query);

        $filename = 'cache_' . $land . '.json';
        $path = $folder . '/' . $filename;

        if (file_exists($path) && !empty($path)) {
            $fetchedData = $parser->fetchDataFromCache($path);
            if ($fetchedData !== null) {
                $fetchedData = json_decode($fetchedData, true);
            }
            $landResult['dataSource'] = 'Data from cache';
        } else {
            $fetchedData = $parser->fetchDataFromUrl($urlToFetch);
            if ($fetchedData !== null) {
                $fetchedData = json_decode($fetchedData, true);
                $parser->saveCache(json_encode($fetchedData), $folder, $filename);
            }
            $landResult['dataSource'] = 'Data from URL';
        }

        if ($fetchedData !== null && !empty($fetchedData) && isset($fetchedData['elements']) && is_array($fetchedData['elements'])) {
            $countData = count($fetchedData['elements']);
            $landResult['dataCount'] = $countData;
            $totalDataCount += $countData;
        } else {
            $landResult['dataCount'] = 0;
            $landResult['status'] = 'no-data';
        }

        $endTimeForLand = microtime(true);
        $timeForLand = $endTimeForLand - $startTimeForLand;
        $landResult['executionTime'] = $timeForLand;

        // Rate Limiting für URL-Anfragen
        if ($landResult['dataSource'] === 'Data from URL' && $timeForLand < 3) {
            $timeToSleep = max(0, 3 - $timeForLand);
            if ($timeToSleep > 0) {
                sleep($timeToSleep);
                $landResult['sleepTime'] = $timeToSleep;
            }
        }

        $results[] = $landResult;
    }

    return ['results' => $results, 'totalDataCount' => $totalDataCount];
}

// Verarbeite die Länder
$processedData = processLands($lands, $url, $folder, $parser, $startTime);
$results = $processedData['results'];
$totalDataCount = $processedData['totalDataCount'];

$endTime = microtime(true);
$totalExecutionTime = $endTime - $startTime;

?>

<!DOCTYPE html>
<html lang="de">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Länder-Datenübersicht - Krankenhäuser</title>
    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Bootstrap Icons -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">
    <!-- Custom CSS -->
    <link href="styles.css" rel="stylesheet">
</head>

<body>
    <div class="container">
        <!-- Header -->
        <div class="row mb-4">
            <div class="col-12">
                <div class="stats-card">
                    <div class="stats-number"><?php echo count($results); ?></div>
                    <div class="stats-text">
                        <i class="bi bi-globe"></i> Länder verarbeitet
                    </div>
                </div>
            </div>
        </div>

        <!-- Data Table -->
        <div class="row">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <div class="d-flex justify-content-between align-items-center">
                            <h4 class="mb-0">
                                <i class="bi bi-table"></i> Länder-Datenbank
                            </h4>
                            <div class="d-flex gap-2">
                                <button class="btn btn-light btn-sm" onclick="sortByName()" id="sortBtn">
                                    <i class="bi bi-sort-alpha-down"></i> Nach Name sortieren
                                </button>
                                <button class="btn btn-light btn-sm" onclick="exportToCSV()">
                                    <i class="bi bi-download"></i> Export CSV
                                </button>
                            </div>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover" id="landsTable">
                            <thead>
                                <tr>
                                    <th><i class="bi bi-hash"></i> #</th>
                                    <th><i class="bi bi-flag"></i> Land</th>
                                    <th><i class="bi bi-database"></i> Datenquelle</th>
                                    <th><i class="bi bi-hospital"></i> Datensätze</th>
                                    <th><i class="bi bi-clock"></i> Ausführungszeit</th>
                                    <th><i class="bi bi-pause-circle"></i> Wartezeit</th>
                                    <th><i class="bi bi-info-circle"></i> Status</th>
                                </tr>
                            </thead>
                            <tbody id="tableBody">
                                <?php
                                $displayCount = 0;
                                $maxInitial = 20;
                                foreach ($results as $index => $item):
                                    $displayCount++;
                                    $rowClass = $displayCount > $maxInitial ? 'hidden-row' : 'fade-in';
                                    ?>
                                    <tr class="<?php echo $rowClass; ?>" data-index="<?php echo $index; ?>">
                                        <td>
                                            <span class="badge bg-secondary"><?php echo $index + 1; ?></span>
                                        </td>
                                        <td>
                                            <strong><?php echo htmlspecialchars($item['land']); ?></strong>
                                        </td>
                                        <td>
                                            <?php if ($item['dataSource'] === 'Data from cache'): ?>
                                                <span class="badge bg-success">
                                                    <i class="bi bi-database"></i> Cache
                                                </span>
                                            <?php else: ?>
                                                <span class="badge bg-warning">
                                                    <i class="bi bi-globe"></i> URL
                                                </span>
                                            <?php endif; ?>
                                        </td>
                                        <td>
                                            <?php if ($item['dataCount'] > 0): ?>
                                                <span class="badge bg-primary">
                                                    <i class="bi bi-hospital"></i> <?php echo $item['dataCount']; ?> Datensätze
                                                </span>
                                            <?php else: ?>
                                                <span class="badge bg-danger">
                                                    <i class="bi bi-exclamation-triangle"></i> Keine Daten
                                                </span>
                                            <?php endif; ?>
                                        </td>
                                        <td>
                                            <small class="text-muted">
                                                <i class="bi bi-clock"></i>
                                                <?php echo number_format($item['executionTime'], 4); ?> Sekunden
                                            </small>
                                        </td>
                                        <td>
                                            <?php if (isset($item['sleepTime']) && $item['sleepTime'] > 0): ?>
                                                <span class="badge bg-info">
                                                    <i class="bi bi-pause-circle"></i>
                                                    <?php echo number_format($item['sleepTime'], 2); ?>s
                                                </span>
                                            <?php else: ?>
                                                <span class="text-muted">-</span>
                                            <?php endif; ?>
                                        </td>
                                        <td>
                                            <?php if ($item['status'] === 'success'): ?>
                                                <span class="badge bg-success">
                                                    <i class="bi bi-check-circle"></i> Erfolgreich
                                                </span>
                                            <?php else: ?>
                                                <span class="badge bg-warning">
                                                    <i class="bi bi-exclamation-triangle"></i> Keine Daten
                                                </span>
                                            <?php endif; ?>
                                        </td>
                                    </tr>
                                <?php endforeach; ?>
                            </tbody>
                        </table>
                    </div>
                    <div class="loading-spinner" id="loadingSpinner">
                        <div class="spinner-border" role="status">
                            <span class="visually-hidden">Lade...</span>
                        </div>
                        <p class="mt-2">Lade weitere Daten...</p>
                    </div>
                    <div class="text-center p-3" id="loadMoreContainer">
                        <button class="load-more-btn" id="loadMoreBtn" onclick="loadMoreData()">
                            <i class="bi bi-arrow-down"></i> Weitere 10 laden
                        </button>
                    </div>
                </div>
            </div>
        </div>

        <!-- Summary Card -->
        <div class="row mt-4">
            <div class="col-12">
                <div class="card">
                    <div class="card-header">
                        <h5 class="mb-0">
                            <i class="bi bi-graph-up"></i> Zusammenfassung
                        </h5>
                    </div>
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-3">
                                <div class="text-center">
                                    <h4 class="text-primary"><?php echo count($results); ?></h4>
                                    <p class="text-muted">Länder verarbeitet</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="text-center">
                                    <h4 class="text-success"><?php echo $totalDataCount; ?></h4>
                                    <p class="text-muted">Gesamte Datensätze</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="text-center">
                                    <h4 class="text-info">
                                        <?php echo count(array_filter($results, function ($item) {
                                            return $item['dataSource'] === 'Data from cache';
                                        })); ?>
                                    </h4>
                                    <p class="text-muted">Aus Cache geladen</p>
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="text-center">
                                    <h4 class="text-warning">
                                        <?php echo count(array_filter($results, function ($item) {
                                            return $item['dataSource'] === 'Data from URL';
                                        })); ?>
                                    </h4>
                                    <p class="text-muted">Von URL geladen</p>
                                </div>
                            </div>
                        </div>
                        <hr>
                        <div class="text-center">
                            <h3 class="text-primary">
                                <i class="bi bi-clock-history"></i>
                                Gesamtausführungszeit: <?php echo number_format($totalExecutionTime, 2); ?> Sekunden
                            </h3>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Custom JavaScript -->
    <script src="script.js"></script>
</body>

</html>