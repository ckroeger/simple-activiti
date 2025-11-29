# Build und Deployment Anleitung

## Lokaler Build

### Voraussetzungen
- Java 21 (JDK)
- Maven 3.6+
- Unter Windows: Launch4j wird automatisch von Maven heruntergeladen

### JAR erstellen
```bash
mvn clean package
```

Dies erstellt:
- `target/simple-activiti-VERSION.jar` - Ausführbares Fat JAR mit allen Dependencies
- `target/simple-activiti-VERSION.exe` - Windows Executable (nur unter Windows)

## GitHub Actions Workflow

Der Workflow wird automatisch ausgeführt bei:
- Erstellung eines neuen Releases (Tag)
- Manueller Ausführung über "workflow_dispatch"

### Zwei Build-Jobs:

1. **build-jar** (Ubuntu)
   - Erstellt das JAR File
   - Published zu GitHub Packages
   - Upload als Artifact "executable-jar"

2. **build-windows-exe** (Windows)
   - Erstellt die portable Windows .exe
   - Upload als Artifact "windows-executable"

### Release erstellen

1. Tag erstellen und pushen:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. Auf GitHub ein Release erstellen:
   - Gehe zu "Releases" → "Draft a new release"
   - Wähle den Tag (z.B. v1.0.0)
   - Füge Release Notes hinzu
   - Klicke "Publish release"

3. Der Workflow wird automatisch gestartet und erstellt:
   - `simple-activiti-1.0.0.jar`
   - `simple-activiti-1.0.0.exe`

4. Die Artifacts werden als Download in der Actions-Übersicht verfügbar sein

### Artifacts herunterladen

Die Build-Artifacts können heruntergeladen werden von:
- GitHub Actions → Workflow Run → Artifacts Sektion
- Alternativ: Manuell zu den Release-Assets hinzufügen

## Windows EXE Details

Die erstellte .exe-Datei ist:
- **Portable** - Keine Installation erforderlich
- **Eigenständig** - Alle Java-Klassen sind eingebettet (Fat JAR wird gewrapped)
- **JRE-abhängig** - Benötigt Java 21+ zur Laufzeit
- **Konsolen-Anwendung** - Zeigt Konsolenfenster mit Logs

### Launch4j Konfiguration

Die EXE wird mit folgenden Einstellungen erstellt:
- Header-Type: Console
- Min JRE Version: 21
- Datei-Encoding: UTF-8
- Fehler-Titel: "Simple Activiti Error"
- Version-Informationen eingebettet

## Troubleshooting

### "Invalid target release: 21"
→ Lokale Java-Version ist < 21. Installiere JDK 21+

### Launch4j funktioniert nicht unter Linux/Mac
→ Das ist normal. Die .exe wird nur unter Windows erstellt. Der GitHub Workflow nutzt einen Windows-Runner.

### Maven kann Launch4j nicht finden
→ Das Plugin lädt Launch4j automatisch herunter. Bei Netzwerkproblemen: Maven mit `-X` ausführen für Debug-Output.

