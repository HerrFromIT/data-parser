# 🚀 Installation Guide - Data Parser

## Systemanforderungen

### Mindestanforderungen
- **PHP**: 8.0 oder höher
- **MySQL**: 5.7 oder höher (oder MariaDB 10.3+)
- **Web Server**: Apache 2.4+ oder Nginx 1.18+
- **Composer**: 2.0 oder höher
- **RAM**: 512MB (empfohlen: 1GB)
- **Disk Space**: 100MB

### Empfohlene Umgebung
- **PHP**: 8.1 oder 8.2
- **MySQL**: 8.0
- **OS**: Ubuntu 20.04+ / CentOS 8+ / Windows 10+
- **RAM**: 2GB+
- **Disk Space**: 500MB+

## Installation Methoden

### Methode 1: Standard Installation

#### Schritt 1: Repository klonen
```bash
git clone https://github.com/yourusername/data-parser.git
cd data-parser
```

#### Schritt 2: Composer Dependencies
```bash
# Composer installieren (falls nicht vorhanden)
curl -sS https://getcomposer.org/installer | php
sudo mv composer.phar /usr/local/bin/composer

# Dependencies installieren
composer install
```

#### Schritt 3: Umgebungsvariablen
```bash
# .env Datei erstellen
cp .env.example .env

# .env bearbeiten
nano .env
```

**Beispiel .env Konfiguration:**
```env
# Database Configuration
DB_HOST=localhost
DB_USER=root
DB_PASS=your_password
DB_NAME=data_parser

# Application Settings
APP_ENV=production
APP_DEBUG=false
CACHE_TTL=3600
RATE_LIMIT_DELAY=5
```

#### Schritt 4: Datenbank Setup
```bash
# Datenbank erstellen
php database/DatabaseCreator.php data_parser

# Tabellen erstellen (automatisch durch DatabaseCreator)
# Überprüfen Sie die Datenbank
mysql -u root -p -e "SHOW TABLES;" data_parser
```

#### Schritt 5: Web Server Konfiguration

**Apache (.htaccess)**
```apache
RewriteEngine On
RewriteCond %{REQUEST_FILENAME} !-f
RewriteCond %{REQUEST_FILENAME} !-d
RewriteRule ^(.*)$ index.php [QSA,L]

# Security Headers
Header always set X-Content-Type-Options nosniff
Header always set X-Frame-Options DENY
Header always set X-XSS-Protection "1; mode=block"
```

**Nginx Konfiguration**
```nginx
server {
    listen 80;
    server_name your-domain.com;
    root /var/www/html/data_parser/php/data_parser;
    index App.php;

    location / {
        try_files $uri $uri/ /App.php?$query_string;
    }

    location ~ \.php$ {
        fastcgi_pass unix:/var/run/php/php8.1-fpm.sock;
        fastcgi_index App.php;
        fastcgi_param SCRIPT_FILENAME $document_root$fastcgi_script_name;
        include fastcgi_params;
    }
}
```

### Methode 2: Docker Installation

#### Docker Compose Setup
```yaml
# docker-compose.yml
version: '3.8'

services:
  web:
    build: .
    ports:
      - "8080:80"
    volumes:
      - ./:/var/www/html
    depends_on:
      - mysql
    environment:
      - DB_HOST=mysql
      - DB_USER=root
      - DB_PASS=rootpassword
      - DB_NAME=data_parser

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: data_parser
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"

  phpmyadmin:
    image: phpmyadmin/phpmyadmin
    ports:
      - "8081:80"
    environment:
      PMA_HOST: mysql
    depends_on:
      - mysql

volumes:
  mysql_data:
```

#### Dockerfile
```dockerfile
FROM php:8.1-apache

# Install dependencies
RUN apt-get update && apt-get install -y \
    libzip-dev \
    zip \
    unzip \
    && docker-php-ext-install pdo pdo_mysql zip

# Install Composer
COPY --from=composer:latest /usr/bin/composer /usr/bin/composer

# Set working directory
WORKDIR /var/www/html

# Copy application files
COPY . .

# Install PHP dependencies
RUN composer install --no-dev --optimize-autoloader

# Set permissions
RUN chown -R www-data:www-data /var/www/html
RUN chmod -R 755 /var/www/html

# Apache configuration
RUN a2enmod rewrite
COPY .docker/apache.conf /etc/apache2/sites-available/000-default.conf
```

#### Docker Commands
```bash
# Container starten
docker-compose up -d

# Logs anzeigen
docker-compose logs -f

# Container stoppen
docker-compose down
```

### Methode 3: XAMPP/WAMP Installation

#### XAMPP Setup
1. **XAMPP herunterladen**: https://www.apachefriends.org/
2. **Installieren**: Standard XAMPP Installation
3. **Projekt kopieren**: Nach `C:\xampp\htdocs\data-parser`
4. **Apache starten**: XAMPP Control Panel
5. **MySQL starten**: XAMPP Control Panel
6. **phpMyAdmin öffnen**: http://localhost/phpmyadmin

#### Datenbank erstellen
```sql
-- In phpMyAdmin
CREATE DATABASE data_parser;
USE data_parser;

-- SQL-Script ausführen
-- Kopieren Sie den Inhalt von create_tables.sql
```

#### URL-Zugriff
```
http://localhost/data-parser/php/data_parser/App.php
```

## Konfiguration

### Anwendungs-Konfiguration

#### configDataParser.php anpassen
```php
<?php

return [
    "amenity" => "hospital",                    // Art der Einrichtung
    "isLandInGerman" => true,                   // Spracheinstellung
    "landsInGerman" => 'landsInGerman.json',   // Deutsche Länderliste
    "landsInEnglish" => 'landsInEnglish.json',  // Englische Länderliste
    "folder_de" => 'cache_de',                  // Cache-Ordner (Deutsch)
    "folder_en" => 'cache_en',                  // Cache-Ordner (Englisch)
    "url" => "https://overpass-api.de/api/interpreter"  // Overpass API URL
];
```

#### Länderlisten anpassen
```json
// utility/landsInGerman.json
{
    "landsInGerman": [
        "Deutschland",
        "Österreich", 
        "Schweiz",
        "Frankreich",
        "Italien"
    ]
}
```

### Performance-Optimierung

#### PHP Konfiguration
```ini
; php.ini Anpassungen
memory_limit = 512M
max_execution_time = 300
upload_max_filesize = 100M
post_max_size = 100M

; OPcache aktivieren
opcache.enable=1
opcache.memory_consumption=128
opcache.max_accelerated_files=4000
```

#### MySQL Optimierung
```sql
-- MySQL Konfiguration
SET GLOBAL innodb_buffer_pool_size = 256M;
SET GLOBAL max_connections = 200;
SET GLOBAL query_cache_size = 32M;
```

## Verifikation der Installation

### System-Check Script
```php
<?php
// system_check.php
echo "=== Data Parser System Check ===\n";

// PHP Version
echo "PHP Version: " . PHP_VERSION . "\n";
if (version_compare(PHP_VERSION, '8.0.0') < 0) {
    echo "❌ PHP 8.0+ erforderlich\n";
} else {
    echo "✅ PHP Version OK\n";
}

// Extensions
$required_extensions = ['pdo', 'pdo_mysql', 'json', 'curl'];
foreach ($required_extensions as $ext) {
    if (extension_loaded($ext)) {
        echo "✅ $ext Extension OK\n";
    } else {
        echo "❌ $ext Extension fehlt\n";
    }
}

// Composer
if (file_exists('vendor/autoload.php')) {
    echo "✅ Composer Dependencies OK\n";
} else {
    echo "❌ Composer Dependencies fehlen\n";
}

// Database Connection
try {
    $pdo = new PDO("mysql:host=localhost", 'root', 'password');
    echo "✅ Database Connection OK\n";
} catch (PDOException $e) {
    echo "❌ Database Connection Failed: " . $e->getMessage() . "\n";
}

echo "=== System Check Complete ===\n";
?>
```

### Test-Ausführung
```bash
# System Check ausführen
php system_check.php

# Test-Anwendung starten
php App.php

# Web-Interface testen
curl http://localhost/data-parser/php/data_parser/App.php
```

## Troubleshooting

### Häufige Probleme

#### Problem: "Class not found" Fehler
```bash
# Lösung: Composer Dependencies neu installieren
composer install --no-cache
```

#### Problem: Database Connection Failed
```bash
# Lösung: MySQL Service prüfen
sudo systemctl status mysql
sudo systemctl start mysql

# Lösung: Datenbankzugriff testen
mysql -u root -p -e "SHOW DATABASES;"
```

#### Problem: Permission Denied
```bash
# Lösung: Dateiberechtigungen setzen
sudo chown -R www-data:www-data /var/www/html/data_parser
sudo chmod -R 755 /var/www/html/data_parser
```

#### Problem: Memory Limit Exceeded
```php
// Lösung: Memory Limit erhöhen
ini_set('memory_limit', '1024M');
```

### Log-Dateien prüfen
```bash
# Apache Error Log
tail -f /var/log/apache2/error.log

# PHP Error Log
tail -f /var/log/php/error.log

# MySQL Error Log
tail -f /var/log/mysql/error.log
```

### Debug-Modus aktivieren
```php
// In App.php
error_reporting(E_ALL);
ini_set('display_errors', 1);
ini_set('log_errors', 1);
ini_set('error_log', '/var/log/php_errors.log');
```

## Sicherheit

### Sicherheits-Checkliste
- [ ] **Database Passwords**: Starke Passwörter verwenden
- [ ] **File Permissions**: Richtige Berechtigungen setzen
- [ ] **Input Validation**: Alle Eingaben validieren
- [ ] **SQL Injection**: Prepared Statements verwenden
- [ ] **XSS Protection**: Output escaping implementieren
- [ ] **HTTPS**: SSL-Zertifikat für Produktion
- [ ] **Firewall**: Ports richtig konfigurieren

### Produktions-Setup
```bash
# .env für Produktion
APP_ENV=production
APP_DEBUG=false
DB_HOST=your-production-db-host
DB_USER=your-production-db-user
DB_PASS=your-strong-password

# File Permissions
chmod 600 .env
chmod 755 php/data_parser/
chmod 644 php/data_parser/*.php
```

## Support

### Hilfe erhalten
- **GitHub Issues**: Für Bugs und Feature Requests
- **Documentation**: Vollständige README.md
- **Community**: GitHub Discussions

### Logs sammeln
```bash
# System Information sammeln
php -v > system_info.txt
mysql --version >> system_info.txt
apache2 -v >> system_info.txt

# Error Logs sammeln
tail -n 100 /var/log/apache2/error.log > error_logs.txt
tail -n 100 /var/log/php/error.log >> error_logs.txt
```

Bei Problemen senden Sie bitte die Log-Dateien mit Ihrem Issue.
