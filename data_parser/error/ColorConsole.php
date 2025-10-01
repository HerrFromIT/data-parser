<?php

namespace data_parser\error;

/**
 * Klasse für farbige Konsolenausgaben
 * Unterstützt verschiedene Log-Level: debug, info, error, warning
 */
class ColorConsole
{
    // ANSI Farbcodes
    private const COLORS = [
        'reset' => "\033[0m",
        'black' => "\033[30m",
        'red' => "\033[31m",
        'green' => "\033[32m",
        'yellow' => "\033[33m",
        'blue' => "\033[34m",
        'magenta' => "\033[35m",
        'cyan' => "\033[36m",
        'white' => "\033[37m",
        'bright_red' => "\033[91m",
        'bright_green' => "\033[92m",
        'bright_yellow' => "\033[93m",
        'bright_blue' => "\033[94m",
        'bright_magenta' => "\033[95m",
        'bright_cyan' => "\033[96m",
        'bright_white' => "\033[97m"
    ];

    // Stilcodes
    private const STYLES = [
        'bold' => "\033[1m",
        'dim' => "\033[2m",
        'italic' => "\033[3m",
        'underline' => "\033[4m"
    ];

    // Log-Level Konfiguration
    private const LOG_LEVELS = [
        'debug' => ['color' => 'cyan', 'prefix' => '[DEBUG]'],
        'info' => ['color' => 'green', 'prefix' => '[INFO]'],
        'warning' => ['color' => 'yellow', 'prefix' => '[WARNING]'],
        'error' => ['color' => 'red', 'prefix' => '[ERROR]']
    ];

    /**
     * Gibt eine Nachricht mit dem angegebenen Log-Level aus
     *
     * @param string $level Log-Level (debug, info, warning, error)
     * @param string $message Die auszugebende Nachricht
     * @param bool $newline Ob ein Zeilenumbruch hinzugefügt werden soll
     */
    public static function log(string $level, string $message, bool $newline = true): void
    {
        if (!isset(self::LOG_LEVELS[$level])) {
            throw new \InvalidArgumentException("Ungültiges Log-Level: {$level}");
        }

        $config = self::LOG_LEVELS[$level];
        $color = self::COLORS[$config['color']];
        $prefix = $config['prefix'];
        $reset = self::COLORS['reset'];

        $output = $color . $prefix . ' ' . $message . $reset;

        if ($newline) {
            $output .= PHP_EOL;
        }

        echo $output;
    }

    /**
     * Debug-Ausgabe (cyan)
     */
    public static function debug(string $message, bool $newline = true): void
    {
        self::log('debug', $message, $newline);
    }

    /**
     * Info-Ausgabe (grün)
     */
    public static function info(string $message, bool $newline = true): void
    {
        self::log('info', $message, $newline);
    }

    /**
     * Warning-Ausgabe (gelb)
     */
    public static function warning(string $message, bool $newline = true): void
    {
        self::log('warning', $message, $newline);
    }

    /**
     * Error-Ausgabe (rot)
     */
    public static function error(string $message, bool $newline = true): void
    {
        self::log('error', $message, $newline);
    }

    /**
     * Gibt eine Nachricht in einer bestimmten Farbe aus
     *
     * @param string $message Die Nachricht
     * @param string $color Die Farbe
     * @param string|null $style Optionaler Stil (bold, dim, italic, underline)
     * @param bool $newline Ob ein Zeilenumbruch hinzugefügt werden soll
     */
    public static function color(string $message, string $color, ?string $style = null, bool $newline = true): void
    {
        if (!isset(self::COLORS[$color])) {
            throw new \InvalidArgumentException("Ungültige Farbe: {$color}");
        }

        if ($style && !isset(self::STYLES[$style])) {
            throw new \InvalidArgumentException("Ungültiger Stil: {$style}");
        }

        $colorCode = self::COLORS[$color];
        $styleCode = $style ? self::STYLES[$style] : '';
        $reset = self::COLORS['reset'];

        $output = $styleCode . $colorCode . $message . $reset;

        if ($newline) {
            $output .= PHP_EOL;
        }

        echo $output;
    }

    /**
     * Gibt eine Nachricht mit mehreren Farben aus
     *
     * @param array $segments Array mit ['text' => 'Text', 'color' => 'Farbe', 'style' => 'Stil']
     * @param bool $newline Ob ein Zeilenumbruch hinzugefügt werden soll
     */
    public static function multiColor(array $segments, bool $newline = true): void
    {
        $output = '';

        foreach ($segments as $segment) {
            $text = $segment['text'] ?? '';
            $color = $segment['color'] ?? 'white';
            $style = $segment['style'] ?? null;

            if (!isset(self::COLORS[$color])) {
                throw new \InvalidArgumentException("Ungültige Farbe: {$color}");
            }

            if ($style && !isset(self::STYLES[$style])) {
                throw new \InvalidArgumentException("Ungültiger Stil: {$style}");
            }

            $colorCode = self::COLORS[$color];
            $styleCode = $style ? self::STYLES[$style] : '';
            $output .= $styleCode . $colorCode . $text . self::COLORS['reset'];
        }

        if ($newline) {
            $output .= PHP_EOL;
        }

        echo $output;
    }

    /**
     * Prüft ob die Konsole Farben unterstützt
     */
    public static function supportsColors(): bool
    {
        // Prüfe ob wir in einer echten Konsole sind und nicht in einem Browser
        return php_sapi_name() === 'cli' &&
            (getenv('TERM') !== false || getenv('COLORTERM') !== false);
    }

    /**
     * Gibt eine farbige Tabelle aus
     *
     * @param array $headers Array mit Spaltenüberschriften
     * @param array $rows Array mit Zeilen (jede Zeile ist ein Array)
     * @param string $headerColor Farbe für die Überschriften
     * @param string $rowColor Farbe für die Zeilen
     */
    public static function table(array $headers, array $rows, string $headerColor = 'bright_white', string $rowColor = 'white'): void
    {
        if (empty($headers) || empty($rows)) {
            return;
        }

        // Berechne Spaltenbreiten
        $widths = [];
        foreach ($headers as $index => $header) {
            $widths[$index] = strlen($header);
        }

        foreach ($rows as $row) {
            foreach ($row as $index => $cell) {
                if (isset($widths[$index])) {
                    $widths[$index] = max($widths[$index], strlen($cell));
                }
            }
        }

        // Ausgabe der Überschriften
        $headerLine = '';
        foreach ($headers as $index => $header) {
            $headerLine .= str_pad($header, $widths[$index] + 2);
        }
        self::color($headerLine, $headerColor, 'bold');

        // Ausgabe der Zeilen
        foreach ($rows as $row) {
            $rowLine = '';
            foreach ($row as $index => $cell) {
                $rowLine .= str_pad($cell, $widths[$index] + 2);
            }
            self::color($rowLine, $rowColor);
        }
    }
}
