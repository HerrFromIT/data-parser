<?php

abstract class DataParser
{

    abstract public function fetchData($filePath, $isUrl = false);



    public function checkPath($filePath)
    {
        $isOkay = false;
        if (file_exists(dirname($filePath))) {
            $isOkay = true;
        }
        return $isOkay;
    }

    public function createFolder($path)
    {
        if (!file_exists($path)) {
            mkdir($path, 0777, true);
        }
    }

    public function saveCache($data, $folder, $land, $amenity)
    {
        $fullPath = $folder . '/' . $amenity;

        if (!file_exists($fullPath)) {
            $result = mkdir($fullPath, 0777, true);
            if (!$result) {
                echo "FEHLER: Konnte Cache-Verzeichnis nicht erstellen: " . $fullPath . "<br>";
                return false;
            }
            echo "Cache-Verzeichnis erstellt: " . $fullPath . "<br>";
        }

        $filePath = $fullPath . '/cache_' . $land . '.json';
        $writeResult = file_put_contents($filePath, $data);

        if ($writeResult !== false) {
            echo "Cache-Datei erfolgreich gespeichert: " . $filePath . " (" . $writeResult . " Bytes)<br>";
            return true;
        } else {
            echo "FEHLER: Cache-Datei konnte nicht gespeichert werden: " . $filePath . "<br>";
            return false;
        }
    }

    public function matchData($data)
    {
        // $data = json_decode($data, true);
        $elements = $data['elements'];
        $companyData = [];
        $count = 0;

        echo "Verarbeite " . count($elements) . " Elemente<br>";

        for ($i = 0; $i < count($elements); $i++) {
            $element = $elements[$i];

            // Debug: Zeige alle verfügbaren Tags für das erste Element
            if ($i === 0) {
                echo "Erstes Element Tags: ";
                if (isset($element['tags'])) {
                    foreach ($element['tags'] as $key => $value) {
                        echo $key . "=" . $value . ", ";
                    }
                }
                echo "<br>";
            }

            if (!isset($element['tags']['name'])) {
                continue;
            } else {
                $count++;

                // Table company_data
                $name = $element['tags']['name'];
                $lat = isset($element['lat']) ? $element['lat'] : null;
                $lon = isset($element['lon']) ? $element['lon'] : null;
                $createdAt = new DateTime();
                $updatedAt = new DateTime();

                // Table company_address - korrekte Tag-Namen aus OpenStreetMap
                $country = isset($element['tags']['addr:country']) ? $element['tags']['addr:country'] : null;
                $city = isset($element['tags']['addr:city']) ? $element['tags']['addr:city'] : null;
                $state = isset($element['tags']['addr:state']) ? $element['tags']['addr:state'] : null;
                $street = isset($element['tags']['addr:street']) ? $element['tags']['addr:street'] : null;
                $housenumber = isset($element['tags']['addr:housenumber']) ? $element['tags']['addr:housenumber'] : null;
                $postalCode = isset($element['tags']['addr:postcode']) ? $element['tags']['addr:postcode'] : null;

                // Table company_contact
                $phone = isset($element['tags']['phone']) ? $element['tags']['phone'] : null;
                $website = isset($element['tags']['website']) ? $element['tags']['website'] : null;
                $email = isset($element['tags']['email']) ? $element['tags']['email'] : null;

                $companyData[$i] = [
                    'id' => $count,
                    'name' => $name,
                    'lat' => $lat,
                    'lon' => $lon,
                    'createdAt' => $createdAt,
                    'updatedAt' => $updatedAt,
                    'country' => $country,
                    'city' => $city,
                    'state' => $state,
                    'street' => $street,
                    'housenumber' => $housenumber,
                    'postalCode' => $postalCode,
                    'phone' => $phone,
                    'website' => $website,
                    'email' => $email,
                    'count' => $count,
                    'isAddress' => true,
                    'isContact' => true
                ];

                // Debug: Zeige Adressdaten für das erste Element
                if ($i === 0) {
                    echo "Adressdaten für " . $name . ":<br>";
                    echo "- Country: " . ($country ?? 'null') . "<br>";
                    echo "- City: " . ($city ?? 'null') . "<br>";
                    echo "- State: " . ($state ?? 'null') . "<br>";
                    echo "- Street: " . ($street ?? 'null') . "<br>";
                    echo "- Housenumber: " . ($housenumber ?? 'null') . "<br>";
                    echo "- PostalCode: " . ($postalCode ?? 'null') . "<br>";
                }
            }
        }
        return $companyData;
    }

    public function saveMatchedData($data, $folder, $filename, $amenity)
    {
        // Verwende absoluten Pfad basierend auf dem aktuellen Verzeichnis
        $baseDir = dirname(__DIR__);
        $matchFolder = $baseDir . '/match_' . ($folder === 'cache_de' ? 'de' : 'en');

        echo "Versuche Verzeichnis zu erstellen: " . $matchFolder . "<br>";

        if (!file_exists($matchFolder)) {
            $result = mkdir($matchFolder, 0777, true);
            if (!$result) {
                echo "FEHLER: Konnte Verzeichnis nicht erstellen: " . $matchFolder . "<br>";
                echo "Aktuelle Berechtigungen: " . substr(sprintf('%o', fileperms(dirname($matchFolder))), -4) . "<br>";
                return false;
            }
            echo "Match-Verzeichnis erstellt: " . $matchFolder . "<br>";
        }

        $amenityPath = $matchFolder . '/' . $amenity;
        if (!file_exists($amenityPath)) {
            $result = mkdir($amenityPath, 0777, true);
            if (!$result) {
                echo "FEHLER: Konnte Unterverzeichnis nicht erstellen: " . $amenityPath . "<br>";
                return false;
            }
            echo "Amenity-Verzeichnis erstellt: " . $amenityPath . "<br>";
        }

        $filename = 'match_' . $filename . '.json';
        $filePath = $amenityPath . '/' . $filename;

        echo "Speichere Datei: " . $filePath . "<br>";

        $writeResult = file_put_contents($filePath, json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE));

        if ($writeResult !== false) {
            echo "Datei erfolgreich gespeichert: " . $filePath . " (" . $writeResult . " Bytes)<br>";
            return true;
        } else {
            echo "FEHLER: Datei konnte nicht gespeichert werden: " . $filePath . "<br>";
            $error = error_get_last();
            echo "Letzter Fehler: " . ($error['message'] ?? 'Unbekannter Fehler') . "<br>";
            return false;
        }
    }
}