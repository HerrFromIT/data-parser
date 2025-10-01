# 💻 GitHub Setup - Komplett über Terminal

## 🎯 Übersicht
Sie haben Git bereits installiert (Version 2.51.0) - perfekt! Jetzt machen wir alles über das Terminal.

## 📋 Schritt-für-Schritt Anleitung

### Schritt 1: Git konfigurieren
```bash
# Git Benutzerdaten setzen (wichtig für Commits)
git config --global user.name "Ihr Name"
git config --global user.email "ihre.email@example.com"

# Konfiguration prüfen
git config --list
```

### Schritt 2: Zum Projektverzeichnis wechseln
```bash
# Zu Ihrem Data Parser Projekt wechseln
cd /var/www/html/data_parser

# Aktuelles Verzeichnis prüfen
pwd

# Dateien anzeigen
ls -la
```

### Schritt 3: Git Repository initialisieren
```bash
# Git Repository in Ihrem Projekt initialisieren
git init

# Status prüfen
git status
```

### Schritt 4: Alle Dateien hinzufügen
```bash
# Alle Dateien zum Staging-Bereich hinzufügen
git add .

# Status prüfen (sollte alle Dateien als "staged" zeigen)
git status
```

### Schritt 5: Ersten Commit erstellen
```bash
# Commit mit aussagekräftiger Nachricht
git commit -m "Initial commit: Data Parser project with full documentation

- Added complete PHP application for OpenStreetMap data extraction
- Implemented intelligent caching system
- Added responsive Vue.js frontend
- Created comprehensive documentation
- Added database management system
- Implemented error handling and logging"
```

### Schritt 6: GitHub Repository erstellen (Terminal)
```bash
# GitHub CLI installieren (falls nicht vorhanden)
# Windows: winget install GitHub.cli
# Oder: https://cli.github.com/

# GitHub CLI verwenden
gh auth login

# Repository erstellen
gh repo create data-parser --public --description "OpenStreetMap Healthcare Data Extraction System"

# Repository URL anzeigen
gh repo view
```

### Schritt 7: Remote Repository hinzufügen
```bash
# Remote Repository hinzufügen
git remote add origin https://github.com/ichgeht/data-parser.git



# Remote prüfen
git remote -v
```

### Schritt 8: Hauptbranch benennen
```bash
# Hauptbranch auf 'main' umbenennen
git branch -M main

# Branches anzeigen
git branch
```

### Schritt 9: Auf GitHub hochladen
```bash
# Erste Upload (nur einmal nötig)
git push -u origin main

# Bei nachfolgenden Änderungen
git push
```

## 🔧 Alternative: Ohne GitHub CLI

### Falls GitHub CLI nicht verfügbar ist:

#### Schritt 6a: Repository manuell erstellen
1. **Gehen Sie zu**: https://github.com/new
2. **Repository Name**: `data-parser`
3. **Description**: `OpenStreetMap Healthcare Data Extraction System`
4. **Public** auswählen
5. **"Create repository"** klicken

#### Schritt 6b: Repository URL kopieren
- Kopieren Sie die URL: `https://github.com/ihrusername/data-parser.git`

#### Dann weiter mit Schritt 7-9

## 📝 Tägliche Git-Workflow

### Änderungen machen und hochladen
```bash
# 1. Änderungen an Dateien machen
# (z.B. README.md bearbeiten)

# 2. Status prüfen
git status

# 3. Änderungen hinzufügen
git add .

# 4. Commit erstellen
git commit -m "Update documentation with new features"

# 5. Auf GitHub hochladen
git push
```

### Änderungen von GitHub herunterladen
```bash
# Neueste Änderungen herunterladen
git pull
```

## 🔍 Nützliche Git-Befehle

### Status und Logs
```bash
# Aktuellen Status anzeigen
git status

# Änderungen anzeigen
git diff

# Commit-Historie anzeigen
git log --oneline

# Detaillierte Commit-Info
git log --graph --pretty=format:'%h -%d %s (%cr) <%an>'
```

### Dateien verwalten
```bash
# Einzelne Datei hinzufügen
git add README.md

# Bestimmte Dateitypen hinzufügen
git add *.php

# Datei aus Staging entfernen
git reset HEAD README.md

# Datei aus Repository entfernen
git rm README.md
```

### Branches verwalten
```bash
# Alle Branches anzeigen
git branch -a

# Neuen Branch erstellen
git branch feature/new-feature

# Branch wechseln
git checkout feature/new-feature

# Branch zusammenführen
git merge feature/new-feature
```

## 🚨 Troubleshooting

### Häufige Probleme

#### Problem: "Repository not found"
```bash
# Remote URL prüfen
git remote -v

# URL korrigieren
git remote set-url origin https://github.com/ihrusername/data-parser.git
```

#### Problem: "Authentication failed"
```bash
# Personal Access Token verwenden
# GitHub → Settings → Developer settings → Personal access tokens
# Token als Passwort verwenden
```

#### Problem: "Permission denied"
```bash
# Git Benutzerdaten prüfen
git config --global user.name
git config --global user.email

# Falls leer, neu setzen
git config --global user.name "Ihr Name"
git config --global user.email "ihre.email@example.com"
```

#### Problem: "Merge conflicts"
```bash
# Konflikte anzeigen
git status

# Konflikte in Dateien auflösen
# Dann:
git add .
git commit -m "Resolve merge conflicts"
```

### Git Reset (Notfall)

#### Letzten Commit rückgängig machen
```bash
# Soft reset (Änderungen bleiben)
git reset --soft HEAD~1

# Hard reset (Änderungen verloren)
git reset --hard HEAD~1
```

#### Zu einem bestimmten Commit zurückkehren
```bash
# Commit-Hash finden
git log --oneline

# Zu diesem Commit zurückkehren
git reset --hard COMMIT_HASH
```

## 📊 Repository-Status prüfen

### Vollständiger Status-Check
```bash
# Repository-Status
echo "=== Repository Status ==="
git status

# Remote-Verbindung
echo "=== Remote Repositories ==="
git remote -v

# Letzte Commits
echo "=== Recent Commits ==="
git log --oneline -5

# Branches
echo "=== Branches ==="
git branch -a
```

## 🎯 Schnellstart-Script

### Alles in einem Script
```bash
#!/bin/bash
# GitHub Setup Script

echo "=== Data Parser GitHub Setup ==="

# Git konfigurieren
echo "Configuring Git..."
git config --global user.name "Ihr Name"
git config --global user.email "ihre.email@example.com"

# Zum Projektverzeichnis wechseln
cd /var/www/html/data_parser

# Git initialisieren
echo "Initializing Git repository..."
git init

# Alle Dateien hinzufügen
echo "Adding all files..."
git add .

# Ersten Commit erstellen
echo "Creating initial commit..."
git commit -m "Initial commit: Data Parser project with full documentation"

# Remote hinzufügen (URL anpassen!)
echo "Adding remote repository..."
git remote add origin https://github.com/ihrusername/data-parser.git

# Branch benennen
echo "Renaming branch to main..."
git branch -M main

# Auf GitHub hochladen
echo "Pushing to GitHub..."
git push -u origin main

echo "=== Setup Complete! ==="
echo "Repository URL: https://github.com/ihrusername/data-parser"
```

## 📋 Checkliste

### Vor dem Start
- [ ] Git installiert ✅ (Version 2.51.0)
- [ ] GitHub Account erstellt
- [ ] Projektverzeichnis gefunden
- [ ] Alle Dokumentationsdateien vorhanden

### Während des Setups
- [ ] Git konfiguriert
- [ ] Repository initialisiert
- [ ] Dateien hinzugefügt
- [ ] Commit erstellt
- [ ] Remote hinzugefügt
- [ ] Auf GitHub hochgeladen

### Nach dem Setup
- [ ] Repository URL funktioniert
- [ ] Alle Dateien sichtbar
- [ ] README.md angezeigt
- [ ] Screenshots hinzugefügt

## 🚀 Nächste Schritte

### Sofort nach Repository-Erstellung
1. **Screenshots hinzufügen** (siehe SCREENSHOTS_GUIDE.md)
2. **README.md aktualisieren**
3. **Issues erstellen** für geplante Features

### Für Portfolio-Optimierung
1. **Profilbild setzen** auf GitHub
2. **Bio schreiben**
3. **Pinned Repositories** setzen
4. **Contributions Graph** aktivieren

---

**💡 Tipp**: Speichern Sie diese Anleitung als Referenz für zukünftige Git-Operationen!
