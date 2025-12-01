# build-and-run.ps1
# Compile sources and produce a runnable JAR using the JDK (no Gradle required).

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "Project root: $root"

# Check javac/java/jar
$java = Get-Command java -ErrorAction SilentlyContinue
$javac = Get-Command javac -ErrorAction SilentlyContinue
$jar = Get-Command jar -ErrorAction SilentlyContinue
if (-not $java -or -not $javac -or -not $jar) {
    Write-Host "JDK tools (javac, java, jar) not found in PATH. Please install JDK 17+ and retry." -ForegroundColor Red
    exit 1
}

$src = Join-Path $root 'src\main\java'
$resources = Join-Path $root 'src\main\resources'
$out = Join-Path $root 'out'
$classes = Join-Path $out 'classes'
$dist = Join-Path $root 'dist'

Remove-Item -Recurse -Force $out -ErrorAction SilentlyContinue
Remove-Item -Recurse -Force $dist -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $classes -Force | Out-Null
New-Item -ItemType Directory -Path $dist -Force | Out-Null

Write-Host "Compiling Java sources..."
$files = Get-ChildItem -Path $src -Recurse -Include '*.java' | ForEach-Object { $_.FullName }
$javacArgs = @('-d',$classes) + $files
& javac @javacArgs
if ($LASTEXITCODE -ne 0) { Write-Host "Compilation failed" -ForegroundColor Red; exit 1 }

# Copy resources if present
if (Test-Path $resources) {
    Write-Host "Copying resources..."
    Copy-Item -Path (Join-Path $resources '*') -Destination $classes -Recurse -Force -ErrorAction SilentlyContinue
}

# Create manifest
$manifest = Join-Path $out 'manifest.txt'
"Main-Class: com.example.logisim.App" | Out-File -FilePath $manifest -Encoding ASCII

$jarFile = Join-Path $dist 'logisim-swing-project.jar'
Write-Host "Creating JAR: $jarFile"
Push-Location $classes
& jar cfm $jarFile $manifest -C . .
$exitCode = $LASTEXITCODE
Pop-Location
if ($exitCode -ne 0) { Write-Host "JAR creation failed" -ForegroundColor Red; exit 1 }

Write-Host "Running JAR..."
& java -jar $jarFile
