# 🤝 Contributing Guidelines

## Code of Conduct

Dieses Projekt folgt einem respektvollen und inklusiven Verhalten. Alle Beiträge sind willkommen, unabhängig von Erfahrungslevel oder Hintergrund.

## Development Setup

### Lokale Entwicklungsumgebung

```bash
# 1. Repository forken und klonen
git clone https://github.com/yourusername/data-parser.git
cd data-parser

# 2. Development Dependencies installieren
composer install --dev

# 3. Environment Setup
cp .env.example .env
# Bearbeiten Sie .env mit Ihren lokalen Einstellungen

# 4. Datenbank Setup
php database/DatabaseCreator.php test_database
```

### Testing

```bash
# Unit Tests ausführen
./vendor/bin/phpunit tests/

# Code Coverage
./vendor/bin/phpunit --coverage-html coverage/

# Code Style Check
./vendor/bin/phpcs --standard=PSR12 php/
```

## Pull Request Process

### 1. Feature Branch erstellen
```bash
git checkout -b feature/your-feature-name
```

### 2. Änderungen committen
```bash
git add .
git commit -m "feat: add new feature description"
```

### 3. Push und Pull Request erstellen
```bash
git push origin feature/your-feature-name
# Erstellen Sie einen Pull Request auf GitHub
```

## Code Standards

### PHP Coding Standards
- **PSR-12**: PHP Standard Recommendations
- **PSR-4**: Autoloading Standard
- **PSR-1**: Basic Coding Standard

### Beispiel für korrekten Code:
```php
<?php

declare(strict_types=1);

namespace DataParser\Utility;

use DataParser\Error\ColorConsole;

class DataParser
{
    private array $config;
    private string $amenity;

    public function __construct(array $config)
    {
        $this->config = $config;
        $this->amenity = $config['amenity'] ?? 'hospital';
    }

    public function fetchData(string $filePath, bool $isUrl = false): array
    {
        if ($isUrl) {
            return $this->fetchDataFromUrl($filePath);
        }

        return $this->fetchDataFromCache($filePath);
    }

    private function fetchDataFromUrl(string $url): array
    {
        // Implementation
    }
}
```

## Commit Message Guidelines

### Format
```
type(scope): description

[optional body]

[optional footer]
```

### Types
- **feat**: Neue Funktionalität
- **fix**: Bug-Fix
- **docs**: Dokumentation
- **style**: Code-Formatierung
- **refactor**: Code-Refactoring
- **test**: Tests hinzufügen/ändern
- **chore**: Build-Prozess, Dependencies

### Beispiele
```
feat(api): add rate limiting to Overpass API calls
fix(cache): resolve cache invalidation issue
docs(readme): update installation instructions
style(php): apply PSR-12 formatting
```

## Issue Guidelines

### Bug Reports
Verwenden Sie die Bug Report Vorlage:
```markdown
## Bug Description
Kurze Beschreibung des Bugs

## Steps to Reproduce
1. Gehen Sie zu...
2. Klicken Sie auf...
3. Fehler tritt auf

## Expected Behavior
Was sollte passieren

## Actual Behavior
Was tatsächlich passiert

## Environment
- PHP Version: 8.1
- OS: Ubuntu 20.04
- Browser: Chrome 91
```

### Feature Requests
```markdown
## Feature Description
Beschreibung der gewünschten Funktionalität

## Use Case
Warum ist diese Funktion nützlich?

## Implementation Ideas
Mögliche Lösungsansätze
```

## Code Review Guidelines

### Als Reviewer
- **Konstruktives Feedback**: Fokus auf Verbesserungen
- **Code-Qualität**: Prüfen Sie auf Best Practices
- **Tests**: Stellen Sie sicher, dass Tests vorhanden sind
- **Dokumentation**: Überprüfen Sie Code-Kommentare

### Checkliste für Pull Requests
- [ ] Code folgt PSR-12 Standards
- [ ] Tests sind hinzugefügt/aktualisiert
- [ ] Dokumentation ist aktualisiert
- [ ] Keine Breaking Changes (oder dokumentiert)
- [ ] Performance-Impact berücksichtigt

## Testing Guidelines

### Unit Tests
```php
<?php

namespace Tests\Unit;

use PHPUnit\Framework\TestCase;
use DataParser\Utility\DataParser;

class DataParserTest extends TestCase
{
    public function testFetchDataFromCache(): void
    {
        $parser = new DataParser(['amenity' => 'hospital']);
        $result = $parser->fetchData('/path/to/cache.json', false);
        
        $this->assertIsArray($result);
        $this->assertArrayHasKey('data', $result);
    }
}
```

### Integration Tests
```php
<?php

namespace Tests\Integration;

use PHPUnit\Framework\TestCase;
use DataParser\App;

class AppTest extends TestCase
{
    public function testAppInitialization(): void
    {
        $config = [
            'amenity' => 'hospital',
            'isLandInGerman' => true
        ];
        
        $app = new App($config);
        $this->assertInstanceOf(App::class, $app);
    }
}
```

## Performance Guidelines

### Memory Management
```php
// Große Arrays nach Verwendung freigeben
unset($largeDataArray);
gc_collect_cycles();

// Streaming für große Dateien
$handle = fopen($file, 'r');
while (($line = fgets($handle)) !== false) {
    // Process line
}
fclose($handle);
```

### Database Optimization
```php
// Prepared Statements verwenden
$stmt = $pdo->prepare('SELECT * FROM company WHERE country = ?');
$stmt->execute([$country]);

// Indexes für häufige Abfragen
CREATE INDEX idx_company_country ON company(country);
```

## Security Guidelines

### Input Validation
```php
public function validateInput(string $input): bool
{
    // Sanitize input
    $sanitized = filter_var($input, FILTER_SANITIZE_STRING);
    
    // Validate length
    if (strlen($sanitized) > 255) {
        return false;
    }
    
    return true;
}
```

### SQL Injection Prevention
```php
// Verwenden Sie Prepared Statements
$stmt = $pdo->prepare('INSERT INTO company (name) VALUES (?)');
$stmt->execute([$companyName]);
```

## Documentation Standards

### Code Comments
```php
/**
 * Fetches data from Overpass API or cache
 * 
 * @param string $filePath Path to cache file or URL
 * @param bool $isUrl Whether the path is a URL
 * @return array Result with 'data' and 'dataSource' keys
 * @throws InvalidArgumentException If filePath is empty
 */
public function fetchData(string $filePath, bool $isUrl = false): array
{
    // Implementation
}
```

### README Updates
- Aktualisieren Sie die Installation-Anleitung
- Fügen Sie neue Features zur Feature-Liste hinzu
- Dokumentieren Sie Breaking Changes

## Release Process

### Version Numbering
- **Major**: Breaking Changes (1.0.0 → 2.0.0)
- **Minor**: Neue Features (1.0.0 → 1.1.0)
- **Patch**: Bug Fixes (1.0.0 → 1.0.1)

### Changelog
```markdown
## [1.1.0] - 2024-01-15

### Added
- New caching mechanism
- Export functionality

### Changed
- Improved error handling
- Updated dependencies

### Fixed
- Memory leak in data processing
- Cache invalidation issue
```

## Community Guidelines

### Kommunikation
- **Respektvoll**: Behandeln Sie alle mit Respekt
- **Konstruktiv**: Fokus auf Lösungen, nicht Probleme
- **Inklusiv**: Willkommen für alle Erfahrungslevel

### Getting Help
- **GitHub Issues**: Für Bugs und Feature Requests
- **Discussions**: Für Fragen und Diskussionen
- **Code Review**: Für Code-Feedback

## License

Durch das Beitragen zu diesem Projekt stimmen Sie zu, dass Ihre Beiträge unter der MIT-Lizenz lizenziert werden.
