Here is the English translation:

# Build and Deployment Guide

## Local Build

### Prerequisites
- Java 21 (JDK)
- Maven 3.6+
- On Windows: Launch4j is automatically downloaded by Maven

### Create JAR
```bash
mvn clean package
```

This creates:
- `target/simple-activiti-VERSION.jar` - Executable fat JAR with all dependencies
- `target/simple-activiti-VERSION.exe` - Windows executable (only on Windows)

## GitHub Actions Workflow

The workflow runs automatically when:
- A new release (tag) is created
- Manually triggered via "workflow_dispatch"

### Two build jobs:

1. **build-jar** (Ubuntu)
   - Creates the JAR file
   - Publishes to GitHub Packages
   - Uploads as artifact "executable-jar"

2. **build-windows-exe** (Windows)
   - Creates the portable Windows .exe
   - Uploads as artifact "windows-executable"

### Create Release

1. Create and push a tag:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. Create a release on GitHub:
   - Go to "Releases" → "Draft a new release"
   - Select the tag (e.g. v1.0.0)
   - Add release notes
   - Click "Publish release"

3. The workflow will start automatically and create:
   - `simple-activiti-1.0.0.jar`
   - `simple-activiti-1.0.0.exe`

4. The artifacts will be available for download in the Actions overview

### Download Artifacts

The build artifacts can be downloaded from:
- GitHub Actions → Workflow Run → Artifacts section
- Alternatively: Manually add to the release assets

## Windows EXE Details

The created .exe file is:
- **Portable** - No installation required
- **Standalone** - All Java classes are embedded (fat JAR is wrapped)
- **JRE-dependent** - Requires Java 21+ at runtime
- **Console application** - Shows console window with logs

### Launch4j Configuration

The EXE is created with the following settings:
- Header type: Console
- Min JRE version: 21
- File encoding: UTF-8
- Error title: "Simple Activiti Error"
- Version information embedded

## Troubleshooting

### "Invalid target release: 21"
→ Local Java version is < 21. Install JDK 21+

### Launch4j does not work on Linux/Mac
→ This is normal. The .exe is only created on Windows. The GitHub workflow uses a Windows runner.

### Maven cannot find Launch4j
→ The plugin downloads Launch4j automatically. If there are network issues: Run Maven with `-X` for debug output.