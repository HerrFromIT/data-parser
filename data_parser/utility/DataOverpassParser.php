<?php

require_once 'DataParser.php';

class DataOverpassParser extends DataParser
{

    public function fetchData($filePath, $isUrl = false)
    {
        $dataResult = [
            'data' => null,
            'dataSource' => null,
        ];

        if ($isUrl) {
            $urlResult = $this->fetchDataFromUrl($filePath);
            $dataResult['data'] = $urlResult['data'];
            $dataResult['dataSource'] = 'Data from URL';
        } else {
            $checkFilePath = $this->checkPath($filePath);

            if (!$checkFilePath) {
                $urlResult = $this->fetchDataFromUrl($filePath);
                $dataResult['data'] = $urlResult['data'];
                $dataResult['dataSource'] = 'Data from URL';
            } else if (
                !empty($filePath)
                && $filePath != ''
                && $filePath != null
                && $checkFilePath
            ) {
                $dataResult['data'] = $this->fetchDataFromCache($filePath);
                $dataResult['dataSource'] = 'Data from cache';
            }
        }


        return $dataResult;
    }

    public function fetchDataFromCache(string $path)
    {
        if (!file_exists($path)) {
            return [
                'dataSource' => 'Data from cache',
                'data' => null
            ];
        } else {
            $data = json_decode(file_get_contents($path), true);
            if ($data !== null && isset($data['elements']) && is_array($data['elements'])) {
                return [
                    'dataSource' => 'Data from cache',
                    'data' => $data
                ];
            } else {
                return [
                    'dataSource' => 'Data from cache',
                    'data' => null
                ];
            }
        }
    }

    public function fetchDataFromUrl(string $url)
    {
        if (!is_null($url) && !empty($url) && $url != '') {
            // Konfiguriere Context für bessere Fehlerbehandlung
            $context = stream_context_create([
                'http' => [
                    'timeout' => 60, // 60 Sekunden Timeout
                    'user_agent' => 'DataParser/1.0 (https://your-domain.com)',
                    'method' => 'GET',
                    'header' => [
                        'Accept: application/json',
                        'Accept-Language: de-DE,de;q=0.9,en;q=0.8'
                    ]
                ],
                'ssl' => [
                    'verify_peer' => false,
                    'verify_peer_name' => false,
                    'timeout' => 60
                ]
            ]);

            // echo "Sende Anfrage an: " . $url . "<br>";

            $data = file_get_contents($url, false, $context);

            if ($data !== false && $data !== null) {
                // echo "Daten erfolgreich von URL erhalten (" . strlen($data) . " Bytes)<br>";
                // Parse JSON data
                $jsonData = json_decode($data, true);
                return [
                    'dataSource' => 'Data from URL',
                    'data' => $jsonData
                ];
            } else {
                $error = error_get_last();
                // echo "FEHLER beim Abrufen der Daten: " . ($error['message'] ?? 'Unbekannter Fehler') . "<br>";

                // Prüfe auf spezifische HTTP-Fehler
                if (strpos($error['message'] ?? '', '429') !== false) {
                    // echo "Rate Limit erreicht - warte länger...<br>";
                    return [
                        'dataSource' => 'Data from URL',
                        'data' => null,
                        'error' => 'rate_limit'
                    ];
                } elseif (strpos($error['message'] ?? '', 'SSL') !== false) {
                    // echo "SSL-Fehler - versuche erneut...<br>";
                    return [
                        'dataSource' => 'Data from URL',
                        'data' => null,
                        'error' => 'ssl_error'
                    ];
                }

                return [
                    'dataSource' => 'Data from URL',
                    'data' => null,
                    'error' => 'general_error'
                ];
            }
        } else {
            return [
                'dataSource' => 'Data from URL',
                'data' => null,
                'error' => 'invalid_url'
            ];
        }
    }

}