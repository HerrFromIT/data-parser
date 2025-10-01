# ⚡ GitHub Schnellstart - Data Parser

## 🚀 In 10 Minuten auf GitHub

### Schritt 1: Git installieren (5 Minuten)
```bash
# Windows: https://git-scm.com/download/win
# Linux: sudo apt install git
# macOS: brew install git

# Testen
git --version
```

### Schritt 2: GitHub Account (2 Minuten)
1. Gehen Sie zu: https://github.com
2. Klicken Sie "Sign up"
3. Füllen Sie das Formular aus
4. Verifizieren Sie Ihre E-Mail

### Schritt 3: Repository erstellen (2 Minuten)
1. Klicken Sie "+" → "New repository"
2. Name: `data-parser`
3. Description: `OpenStreetMap Healthcare Data Extraction System`
4. Public ✅
5. "Create repository"

### Schritt 4: Projekt hochladen (1 Minute)
```bash
# In Ihr Projektverzeichnis
cd /var/www/html/data_parser

# Git initialisieren
git init
git add .
git commit -m "Initial commit: Data Parser project"

# Auf GitHub hochladen
git remote add origin https://github.com/ihrusername/data-parser.git
git branch -M main
git push -u origin main
```

## ✅ Fertig!

Ihr Projekt ist jetzt auf GitHub verfügbar unter:
`https://github.com/ihrusername/data-parser`

## 📋 Checkliste

- [ ] Git installiert
- [ ] GitHub Account erstellt
- [ ] Repository erstellt
- [ ] Projekt hochgeladen
- [ ] README.md mit Screenshots
- [ ] .gitignore erstellt
- [ ] LICENSE hinzugefügt

## 🔧 Bei Problemen

### "Repository not found"
```bash
git remote -v
git remote set-url origin https://github.com/ihrusername/data-parser.git
```

### "Authentication failed"
- Verwenden Sie Personal Access Token als Passwort
- GitHub → Settings → Developer settings → Personal access tokens

### "Permission denied"
```bash
git config --global user.name "Ihr Name"
git config --global user.email "ihre.email@example.com"
```

## 📚 Nächste Schritte

1. **Screenshots hinzufügen** (siehe SCREENSHOTS_GUIDE.md)
2. **README.md aktualisieren**
3. **Issues erstellen** für geplante Features
4. **Profil optimieren** für Portfolio

## 🎯 Für HR-Präsentation

- **Repository URL** bereithalten
- **Screenshots** vorbereiten
- **Live-Demo** üben
- **Code-Qualität** demonstrieren

---

**💡 Tipp**: Lesen Sie die vollständige Anleitung in `GITHUB_SETUP_GUIDE.md` für detaillierte Erklärungen.
