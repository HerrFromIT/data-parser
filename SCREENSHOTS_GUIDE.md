# 📸 Screenshot-Anleitung für Data Parser Projekt

## Benötigte Screenshots

### 1. Hauptdashboard (01-main-dashboard.png)
**Dateiname**: `screenshots/01-main-dashboard.png`
**Beschreibung**: Vollbildschirm-Dashboard mit Bootstrap-Design

**Was zu zeigen ist**:
- Bootstrap-Tabelle mit Ländern und Statistiken
- Header mit Gesamtstatistiken (Länder verarbeitet, Gesamte Datensätze)
- Status-Badges (Cache/URL, Erfolgreich/Keine Daten)
- Sortier- und Export-Buttons
- Responsive Design auf Desktop-Größe

**Wie erstellen**:
1. Öffnen Sie `http://localhost/data_parser/php/data_parser/App.php` im Browser
2. Warten Sie bis die Datenverarbeitung abgeschlossen ist
3. Machen Sie einen Vollbildschirm-Screenshot
4. Stellen Sie sicher, dass alle Spalten sichtbar sind

### 2. Datenverarbeitung in Aktion (02-data-processing.png)
**Dateiname**: `screenshots/02-data-processing.png`
**Beschreibung**: Konsolen-ähnliche Ausgabe mit Status-Meldungen

**Was zu zeigen ist**:
- Farbige Konsolen-Ausgaben
- "Cache-Datei gefunden" / "Cache-Datei nicht gefunden" Meldungen
- "Daten aus Cache geladen" / "Speichere Daten in Cache" Meldungen
- Ausführungszeiten und Datensätze-Zählung
- Rate Limiting Meldungen ("Warte X Sekunden...")

**Wie erstellen**:
1. Öffnen Sie die Browser-Entwicklertools (F12)
2. Gehen Sie zum Console-Tab
3. Starten Sie die Anwendung neu
4. Screenshot der Konsolen-Ausgabe machen

### 3. Export-Funktionalität (03-export-functionality.png)
**Dateiname**: `screenshots/03-export-functionality.png`
**Beschreibung**: CSV-Export-Dialog mit Download-Option

**Was zu zeigen ist**:
- Klick auf "Export CSV" Button
- Browser-Download-Dialog
- CSV-Datei mit Daten (falls möglich)
- Oder: Export-Button in Aktion

**Wie erstellen**:
1. Klicken Sie auf den "Export CSV" Button
2. Screenshot des Download-Dialogs
3. Oder: Screenshot der CSV-Datei nach dem Download

### 4. Datenbank-Schema (04-database-schema.png)
**Dateiname**: `screenshots/04-database-schema.png`
**Beschreibung**: ER-Diagramm der Datenbankstruktur

**Was zu zeigen ist**:
- Tabellen: company, address, contact, company_address, company_contacts
- Foreign Key Beziehungen
- Primärschlüssel
- Spalten mit Datentypen

**Wie erstellen**:
1. Verwenden Sie phpMyAdmin oder MySQL Workbench
2. Erstellen Sie ein ER-Diagramm der Tabellen
3. Exportieren Sie als Bild
4. Oder: Verwenden Sie ein Online-Tool wie dbdiagram.io

### 5. Code-Architektur (05-code-architecture.png)
**Dateiname**: `screenshots/05-code-architecture.png`
**Beschreibung**: UML-Klassendiagramm oder Code-Struktur

**Was zu zeigen ist**:
- Hauptklassen: App, DataParser, DataOverpassParser, Vue
- Beziehungen zwischen Klassen
- Methoden und Eigenschaften
- Vererbungshierarchie

**Wie erstellen**:
1. Verwenden Sie ein UML-Tool (z.B. draw.io, Lucidchart)
2. Oder: Erstellen Sie ein einfaches Diagramm mit PowerPoint/Keynote
3. Zeigen Sie die Hauptklassen und deren Beziehungen

## Zusätzliche Screenshots (Optional)

### 6. Mobile Ansicht (06-mobile-view.png)
**Dateiname**: `screenshots/06-mobile-view.png`
**Beschreibung**: Responsive Design auf Mobile-Gerät

### 7. Fehlerbehandlung (07-error-handling.png)
**Dateiname**: `screenshots/07-error-handling.png`
**Beschreibung**: Fehlermeldungen und Error Handling

### 8. Cache-Verwaltung (08-cache-management.png)
**Dateiname**: `screenshots/08-cache-management.png`
**Beschreibung**: Cache-Dateien im Dateisystem

## Screenshot-Qualitätsrichtlinien

### Technische Anforderungen
- **Auflösung**: Mindestens 1920x1080 für Desktop-Screenshots
- **Format**: PNG für bessere Qualität
- **Dateigröße**: Unter 2MB pro Screenshot
- **Schriftgröße**: Lesbar bei 100% Zoom

### Design-Richtlinien
- **Saubere Oberfläche**: Keine persönlichen Daten oder Passwörter
- **Konsistente Farben**: Verwenden Sie das Bootstrap-Theme
- **Vollständige Ansicht**: Zeigen Sie alle wichtigen Elemente
- **Professioneller Look**: Saubere, organisierte Darstellung

### Screenshot-Tools
- **Windows**: Snipping Tool, Greenshot, Lightshot
- **macOS**: Screenshot (Cmd+Shift+4), Skitch
- **Linux**: Shutter, Flameshot
- **Browser**: Browser-Entwicklertools für Web-Screenshots

## Ordnerstruktur für Screenshots

```
data_parser/
├── screenshots/
│   ├── 01-main-dashboard.png
│   ├── 02-data-processing.png
│   ├── 03-export-functionality.png
│   ├── 04-database-schema.png
│   ├── 05-code-architecture.png
│   ├── 06-mobile-view.png (optional)
│   ├── 07-error-handling.png (optional)
│   └── 08-cache-management.png (optional)
└── README.md
```

## Anmerkungen für HR-Präsentation

### Was HR-Manager sehen sollten:
1. **Professionelle Oberfläche**: Sauberes, modernes Design
2. **Technische Komplexität**: Zeigen Sie Code-Struktur und Architektur
3. **Real-World Anwendung**: Praktische Verwendung des Systems
4. **Datenverarbeitung**: Live-Demonstration der Funktionalität
5. **Dokumentation**: Umfassende README und Code-Kommentare

### Für technische Interviews:
- Zeigen Sie die Code-Qualität
- Demonstrieren Sie Error Handling
- Erklären Sie die Architektur-Entscheidungen
- Zeigen Sie Performance-Optimierungen
