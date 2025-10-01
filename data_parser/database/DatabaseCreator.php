<?php
require_once __DIR__ . '/DBconnect.php';
require_once __DIR__ . '/../error/FautDecoder.php';
require_once __DIR__ . '/DatabaseNameValidator.php';
require_once __DIR__ . '/../error/ColorConsole.php';

use data_parser\error\ColorConsole;

class DatabaseCreator
{
    private $dbconnect;

    public function __construct($dbconnect)
    {
        $this->dbconnect = $dbconnect;
    }

    public function createDatabase($databaseName)
    {
        $isDBCreateSuccess = 0;


        try {
            // check Database Name mit Builder Pattern
            $validator = new DatabaseNameValidator($databaseName);
            $validationResult = $validator->validate();


            if ($validationResult != 200) {
                $isDBCreateSuccess = 1001;
            }

            // check Connection
            if (!$this->dbconnect) {
                $isDBCreateSuccess = 1002;
            }

            // check if Database exists
            if ($isDBCreateSuccess == 0 && $this->dbExists($databaseName)) {
                echo ColorConsole::color("Die Datenbank '{$databaseName}' existiert bereits.", "yellow", "bold");
                echo ColorConsole::color("Möchten Sie sie löschen? (j/N): ", "yellow", "bold");
                $handle = fopen("php://stdin", "r");
                $answer = trim(fgets($handle));
                fclose($handle);

                // Default zu 'n' wenn Enter gedrückt wird (leere Eingabe)
                if (empty($answer)) {
                    $answer = 'n';
                }

                if (strtolower($answer) === 'j' || strtolower($answer) === 'y') {
                    // drop Database
                    $sql = <<<SQL
                    DROP DATABASE IF EXISTS {$databaseName}
                    SQL;
                    $this->dbconnect->executeQuery($sql);
                    echo ColorConsole::color("Datenbank '{$databaseName}' wurde gelöscht.", "green");
                    $isDBCreateSuccess = 2001;
                } else {
                    echo ColorConsole::color("Datenbank wird nicht gelöscht.", "yellow");
                    $isDBCreateSuccess = 2002;
                }
            } else if ($isDBCreateSuccess == 0 && !$this->dbExists($databaseName)) {
                $isDBCreateSuccess = 2003;
            }

            if ($isDBCreateSuccess < 2000) {
                return $isDBCreateSuccess;
            }

            if ($isDBCreateSuccess == 2000 || $isDBCreateSuccess == 2001 || $isDBCreateSuccess == 2003) {
                // create Database
                $sql = "CREATE DATABASE IF NOT EXISTS {$databaseName}";
                $this->dbconnect->executeQuery($sql);

                // use Database
                $sql = "USE {$databaseName}";
                $this->dbconnect->executeQuery($sql);

                // create Tables
                $sql = file_get_contents('/var/www/html/data_parser/data_parser/database/create_tables.sql');
                $this->dbconnect->executeQuery($sql);

                echo ColorConsole::info("Datenbank erfolgreich erstellt! " . "  " . "Code des Ergebnisses: " . $isDBCreateSuccess);
            }

            return $isDBCreateSuccess;

        } catch (PDOException $e) {
            echo ColorConsole::error("Fehler: " . $e->getMessage());
            return $e->getMessage();
        }
    }


    private function dbExists($databaseName): bool
    {
        $query = <<<'SQL'
            SELECT SCHEMA_NAME 
            FROM INFORMATION_SCHEMA.SCHEMATA 
            WHERE SCHEMA_NAME = :database_name
        SQL;
        $stmt = $this->dbconnect->executeQuery($query, [':database_name'], [':database_name' => $databaseName]);
        return $stmt->fetchColumn();
    }
}

$databaseName = null;
$dbConnect = new DBconnect();
$dbCreator = new DatabaseCreator($dbConnect);
$config = json_decode(file_get_contents(__DIR__ . '/configDatabaseCreator.json'), true);

if (isset($argv[1])) {
    $databaseName = $argv[1];
} else {
    $databaseName = $config['defaultDatabaseName'];
}

echo ColorConsole::info("Database Name: " . $databaseName);

// Builder Pattern für Validierung
$validator = new DatabaseNameValidator($databaseName);
$validationResult = $validator->validate();

if ($validationResult == 200) {
    echo ColorConsole::info("✅ Datenbankname ist gültig!");
    $dbCreator->createDatabase($databaseName);
} else {
    echo ColorConsole::error("❌ Datenbankname ist ungültig!");
    echo ColorConsole::error("Fehlercode: " . $validationResult);
    echo ColorConsole::error(FautDecoder::decode($validationResult));
}
