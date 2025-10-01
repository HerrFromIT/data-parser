<?php


/*
 * 1) Fetch Data from Overpass API wurde gemacht, falls die Daten bereits in der Datei sind, dann werden die Daten aus der Datei gelesen, falls nicht, dann werden die Daten aus der URL gelesen - erledigt
 * 2) Nun muss man die Methode Implementieren, um Cache zu speichern. - [erledigt]
 * 3) Nachher muss man die Methode Implementieren, um die Daten zu matchen. Alle Daten spiegeln die Struktur der Klasse CompanyData.
 * Es soll so gespeichert werden, dass die Daten in das Verzeichnis match_de oder match_en gespeichert werden. 
 * Und dann soll das Verzeichnis mit dem Namen von $amenity gespeichert werden. und drin sollen die Dateien mit den namen match_$land.json gespeichert werden.
 * 4) Nachher muss man die Methode Implementieren, um die Daten in die Datenbank zu speichern.Dafür benötige ich eine Datei .sql, um die Datenbank zu erstellen. 
 * 
 * Man muss checkFilePath machen und wenn checkFilePath false ist oder isUrl true ist, dann wird die Daten aus der URL gelesen. Stattdessen muss man die Daten aus cache lesen.
 * Um die Daten aus Cach oder URL zu lesen, muss man die Methode fetchDataFromCache oder fetchDataFromUrl in der Klasse DataParser implementieren.
 * Dann kann amn die Daten bekommen und das Ergebnis in die Variable $fetchedData speichern.
 * Danach kann man die Daten matchen mit der Methode matchData in der Klasse DataOverpassParser implementieren. Und die Daten in das Ergebnis speichern.    
 * 
 */

require_once __DIR__ . '/utility/CompanyDataParser.php';
require_once __DIR__ . '/utility/DataOverpassParser.php';
require_once __DIR__ . '/Vue/Vue.php';
$configDataParser = require_once __DIR__ . '/utility/configDataParser.php';


class App
{
    private array $configDataParser;
    private string $amenity;
    private bool $isLandInGerman;
    private array $lands;
    private string $folder;
    private array $results;
    private int $totalDataCount;
    public function __construct($configDataParser)
    {
        $this->configDataParser = $configDataParser;
        $this->amenity = $configDataParser['amenity'];
        $this->isLandInGerman = $configDataParser['isLandInGerman'];
        $this->lands = $this->extractLand();
        $this->folder = $this->extractFolder();

        $this->run();

    }

    private function extractLand()
    {
        if ($this->isLandInGerman) {
            $lands = json_decode(file_get_contents(__DIR__ . "/utility/" . $this->configDataParser['landsInGerman']), true)['landsInGerman'];
        } else {
            $lands = json_decode(file_get_contents(__DIR__ . "/utility/" . $this->configDataParser['landsInEnglish']), true)['landsInEnglish'];
        }
        return $lands;
    }

    private function extractFolder()
    {
        if ($this->isLandInGerman) {
            $folder = $this->configDataParser['folder_de'];
        } else {
            $folder = $this->configDataParser['folder_en'];
        }
        return $folder;
    }

    private function run()
    {
        $startTime = microtime(true);

        $results = [];
        $totalDataCount = 0;
        $isUrl = false;

        foreach ($this->lands as $land) {
            $startTimeForLand = microtime(true);

            $filePath = $this->folder . '/' . $this->amenity . '/' . 'cache_' . $land . '.json';

            $data = null;
            $dataSource = null;
            $dataCount = 0;
            $executionTime = 0;
            $status = 'success';

            $landResult = [
                'land' => $land,
                'dataSource' => $dataSource,
                'dataCount' => $dataCount,
                'data' => $data,
                'executionTime' => $executionTime,
                'status' => $status
            ];

            $parser = new DataOverpassParser();
            $fetchedData = null;

            // Prüfe zuerst, ob Cache-Datei existiert
            if (file_exists($filePath) && !empty($filePath)) {
                echo "Cache-Datei gefunden: " . $filePath . "<br>";
                $cacheResult = $parser->fetchData($filePath, false);
                $fetchedData = $cacheResult['data'];
                $landResult['dataSource'] = $cacheResult['dataSource'];
                $landResult['data'] = $fetchedData;
                echo "Daten aus Cache geladen<br>";
            } else {
                echo "Cache-Datei nicht gefunden, lade von URL: " . $filePath . "<br>";
                $urlToFetch = $this->queryBuilder($this->configDataParser['url'], $this->amenity, $land);
                $urlResult = $parser->fetchDataFromUrl($urlToFetch);
                $fetchedData = $urlResult['data'];
                $landResult['dataSource'] = 'Data from URL';
                $landResult['data'] = $fetchedData;

                // Speichere in Cache, wenn Daten erfolgreich geladen wurden
                if (
                    $fetchedData !== null
                    && $fetchedData != 'null'
                    && !empty($fetchedData)
                    && isset($fetchedData['elements'])
                    && is_array($fetchedData['elements'])
                ) {
                    echo "Speichere Daten in Cache...<br>";
                    $parser->saveCache(json_encode($fetchedData), $this->folder, $land, $this->amenity);
                }
            }

            if (
                $fetchedData !== null
                && $fetchedData != 'null'
                && !empty($fetchedData)
                && is_array($fetchedData)
                && isset($fetchedData['elements'])
                && is_array($fetchedData['elements'])
            ) {
                $countData = count($fetchedData['elements']);
                $landResult['dataCount'] = $countData;
                $totalDataCount += $countData;

                $matchedData = $parser->matchData($fetchedData);

                $parser->saveMatchedData($matchedData, $this->folder, $land, $this->amenity);

                $landResult['status'] = 'success';
            } else {
                $landResult['dataCount'] = 0;
                $landResult['status'] = 'no-data';
            }

            $endTimeForLand = microtime(true);
            $timeForLand = $endTimeForLand - $startTimeForLand;
            $landResult['executionTime'] = $timeForLand;

            // Rate Limiting für URL-Anfragen
            if ($landResult['dataSource'] === 'Data from URL') {
                // Mindestens 5 Sekunden zwischen Anfragen (statt 3)
                $minWaitTime = 5;

                if ($timeForLand < $minWaitTime) {
                    $timeToSleep = $minWaitTime - $timeForLand;
                    echo "Warte " . $timeToSleep . " Sekunden vor der nächsten Anfrage...<br>";
                    sleep($timeToSleep);
                    $landResult['sleepTime'] = $timeToSleep;
                }

                // Zusätzliche Wartezeit bei Fehlern
                if (isset($urlResult['error'])) {
                    if ($urlResult['error'] === 'rate_limit') {
                        echo "Rate Limit erreicht - warte 30 Sekunden...<br>";
                        sleep(30);
                        $landResult['sleepTime'] = ($landResult['sleepTime'] ?? 0) + 30;
                    } elseif ($urlResult['error'] === 'ssl_error') {
                        echo "SSL-Fehler - warte 10 Sekunden...<br>";
                        sleep(10);
                        $landResult['sleepTime'] = ($landResult['sleepTime'] ?? 0) + 10;
                    }
                }
            }

            $results[] = $landResult;
        }

        $endTime = microtime(true);
        $totalExecutionTime = $endTime - $startTime;


        $this->results = ['results' => $results, 'totalDataCount' => $totalDataCount, 'totalExecutionTime' => $totalExecutionTime];

        $vue = new Vue($this->results);
        $vue->displayData();
    }

    private function queryBuilder($url, $amenity, $land)
    {
        $query = '[out:json][timeout:25];';
        $query .= 'area["name:de"="' . $land . '"]["admin_level"="2"]->.land;';
        $query .= '(node["amenity"="' . $amenity . '"](area.land);';
        $query .= 'way["amenity"="' . $amenity . '"](area.land);';
        $query .= 'relation["amenity"="' . $amenity . '"](area.land););';
        $query .= 'out;';

        return $url . "?data=" . urlencode($query);
    }




}

new App($configDataParser);

?>