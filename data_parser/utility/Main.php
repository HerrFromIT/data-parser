<?php

require_once 'CompanyDataParser.php';
require_once 'DBconnect.php';

$filename = 'cache.json';

$url = "https://overpass-api.de/api/interpreter";
$query = '[out:json];area["name:de"="Berlin"]["admin_level"="4"]->.berlin;(node["amenity"="hospital"](area.berlin);way["amenity"="hospital"](area.berlin);relation["amenity"="hospital"](area.berlin););out;';
$urlToFetch = $url . "?data=" . urlencode($query);
// echo $urlToFetch . "<br>";

$parser = new CompanyDataParser();
$fetchedData = $parser->fetchData($urlToFetch);
// var_dump($fetchedData);
$parser->saveCache($fetchedData, $filename);

// $filenameToSave = 'data.json';
// $matchedData = $parser->matchData($fetchedData);
// $count = count($matchedData);
// echo 'Daten gesammelt und in die Datei ' . $filenameToSave . ' gespeichert. ' . $count . ' Datensätze gefunden.' . "<br>";
// $parser->saveData($matchedData, $filenameToSave);

// $db = new DBconnect();
// $conn = $db->getConnection();

// $sql = "SELECT * FROM companies WHERE id = 8";
// $stmt = $conn->prepare($sql);
// $stmt->execute();
// $result = $stmt->fetchAll(PDO::FETCH_ASSOC);
// // var_dump($result);
// $db->closeConnection();

// $data = $parser->loadCache($filename);
// echo $data;
?>

<!DOCTYPE html>
<html lang="de">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Berliner Krankenhäuser - Datenübersicht</title>
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
                    <div class="stats-number"><?php echo count($matchedData); ?></div>
                    <div class="stats-text">
                        <i class="bi bi-hospital"></i> Krankenhäuser in Berlin gefunden
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
                                <i class="bi bi-table"></i> Krankenhaus-Datenbank
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
                        <table class="table table-hover" id="hospitalTable">
                            <thead>
                                <tr>
                                    <th><i class="bi bi-hash"></i> ID</th>
                                    <th><i class="bi bi-building"></i> Name</th>
                                    <th><i class="bi bi-geo-alt"></i> Koordinaten</th>
                                    <th><i class="bi bi-geo"></i> Adresse</th>
                                    <th><i class="bi bi-telephone"></i> Kontakt</th>
                                    <th><i class="bi bi-globe"></i> Web</th>
                                </tr>
                            </thead>
                            <tbody id="tableBody">
                                <?php
                                $displayCount = 0;
                                $maxInitial = 20;
                                foreach ($matchedData as $index => $item):
                                    $data = $item['data'];
                                    $displayCount++;
                                    $rowClass = $displayCount > $maxInitial ? 'hidden-row' : 'fade-in';
                                    ?>
                                    <tr class="<?php echo $rowClass; ?>" data-index="<?php echo $index; ?>">
                                        <td>
                                            <span class="badge bg-secondary"><?php echo $item['id']; ?></span>
                                        </td>
                                        <td>
                                            <strong><?php echo htmlspecialchars($data->name ?? 'N/A'); ?></strong>
                                        </td>
                                        <td>
                                            <?php if (!empty($data->lat) && !empty($data->lon)): ?>
                                                <small class="text-muted">
                                                    <i class="bi bi-geo-alt-fill text-primary"></i>
                                                    <?php echo number_format($data->lat, 6); ?>,
                                                    <?php echo number_format($data->lon, 6); ?>
                                                </small>
                                            <?php else: ?>
                                                <span class="empty-cell">Keine Koordinaten</span>
                                            <?php endif; ?>
                                        </td>
                                        <td>
                                            <?php
                                            $address = [];
                                            if (!empty($data->street))
                                                $address[] = $data->street;
                                            if (!empty($data->housenumber))
                                                $address[] = $data->housenumber;
                                            if (!empty($data->postalCode))
                                                $address[] = $data->postalCode;
                                            if (!empty($data->city))
                                                $address[] = $data->city;
                                            echo !empty($address) ? htmlspecialchars(implode(' ', $address)) : '<span class="empty-cell">Keine Adresse</span>';
                                            ?>
                                        </td>
                                        <td>
                                            <?php if (!empty($data->phone)): ?>
                                                <a href="tel:<?php echo $data->phone; ?>"
                                                    class="btn btn-sm btn-outline-primary">
                                                    <i class="bi bi-telephone"></i>
                                                    <?php echo htmlspecialchars($data->phone); ?>
                                                </a>
                                            <?php else: ?>
                                                <span class="empty-cell">Kein Telefon</span>
                                            <?php endif; ?>
                                        </td>
                                        <td>
                                            <?php if (!empty($data->website)): ?>
                                                <a href="<?php echo htmlspecialchars($data->website); ?>" target="_blank"
                                                    class="btn btn-sm btn-outline-success">
                                                    <i class="bi bi-box-arrow-up-right"></i> Website
                                                </a>
                                            <?php else: ?>
                                                <span class="empty-cell">Keine Website</span>
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
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

    <!-- Custom JavaScript -->
    <script src="script.js"></script>
</body>

</html>