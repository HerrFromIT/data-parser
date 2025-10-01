<?php

require_once __DIR__ . '/../vendor/autoload.php';

class DBconnect
{
    private $pdo;

    public function __construct()
    {
        $dotenv = Dotenv\Dotenv::createImmutable(__DIR__);
        $dotenv->load();
        $dbHost = $_ENV['DB_HOST'];
        $dbUser = $_ENV['DB_USER'];
        $dbPass = $_ENV['DB_PASS'];
        $this->pdo = new PDO("mysql:host=$dbHost;unix_socket=/var/run/mysqld/mysqld.sock", $dbUser, $dbPass);
    }

    public function getConnection()
    {
        return $this->pdo;
    }

    public function closeConnection()
    {
        $this->pdo = null;
    }

    /**
     * Führt eine SQL-Abfrage aus.
     *
     * @param string $query Die SQL-Abfrage, die ausgeführt werden soll.
     * @param array $prepareParams Ein optionales Array von Parametern, die vorbereitet werden sollen (z.B. Platzhalter wie ':name').
     * @param array $bindParams Ein optionales assoziatives Array von Werten, die an die vorbereiteten Parameter gebunden werden sollen.
     * @param array $params Ein optionales Array von zusätzlichen Parametern (derzeit ungenutzt).
     * @return PDOStatement Das PDOStatement-Objekt nach der Ausführung der Abfrage.
     */
    public function executeQuery($query, $prepareParams = [], $bindParams = [], $params = [])
    {
        $stmt = $this->pdo->prepare($query);
        if (!empty($prepareParams) && !empty($bindParams)) {
            foreach ($prepareParams as $param) {
                $stmt->bindParam($param, $bindParams[$param]);
            }
            $stmt->execute();
        } else {
            $stmt->execute();
        }
        return $stmt;
    }
}