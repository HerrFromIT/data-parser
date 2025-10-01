<?php

require_once 'ColorConsole.php';

use data_parser\error\ColorConsole;

/**
 * Beispiel für die Verwendung der ColorConsole Klasse
 */
class ColorConsoleExample
{
    public static function runExample(): void
    {
        echo "=== ColorConsole Beispiel ===\n\n";

        // Log-Level Beispiele
        ColorConsole::debug("Dies ist eine Debug-Nachricht");
        ColorConsole::info("Dies ist eine Info-Nachricht");
        ColorConsole::warning("Dies ist eine Warnung");
        ColorConsole::error("Dies ist ein Fehler");

        echo "\n";

        // Einzelne Farben
        ColorConsole::color("Roter Text", "red");
        ColorConsole::color("Grüner Text", "green");
        ColorConsole::color("Blauer Text", "blue");
        ColorConsole::color("Gelber Text", "yellow");

        echo "\n";

        // Text mit Stilen
        ColorConsole::color("Fetter Text", "white", "bold");
        ColorConsole::color("Unterstrichener Text", "cyan", "underline");
        ColorConsole::color("Kursiver Text", "magenta", "italic");

        echo "\n";

        // Mehrfarbiger Text
        ColorConsole::multiColor([
            ['text' => 'Mehrfarbiger ', 'color' => 'red'],
            ['text' => 'Text mit ', 'color' => 'green'],
            ['text' => 'verschiedenen ', 'color' => 'blue'],
            ['text' => 'Farben', 'color' => 'yellow', 'style' => 'bold']
        ]);

        echo "\n";

        // Tabelle
        $headers = ['Name', 'Alter', 'Stadt'];
        $rows = [
            ['Max', '25', 'Berlin'],
            ['Anna', '30', 'München'],
            ['Peter', '35', 'Hamburg']
        ];

        ColorConsole::table($headers, $rows);

        echo "\n";

        // Prüfung der Farbunterstützung
        if (ColorConsole::supportsColors()) {
            ColorConsole::info("Farben werden unterstützt!");
        } else {
            ColorConsole::warning("Farben werden nicht unterstützt!");
        }
    }
}

// Beispiel ausführen, wenn die Datei direkt aufgerufen wird
if (basename(__FILE__) === basename($_SERVER['SCRIPT_NAME'])) {
    ColorConsoleExample::runExample();
}
