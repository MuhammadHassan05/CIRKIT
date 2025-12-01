# setup-and-run.ps1
# Creates compilation output and runs the Swing app using javac/java.

$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Write-Host "Project root: $root"

# Find JDK
$java = Get-Command java -ErrorAction SilentlyContinue
$javac = Get-Command javac -ErrorAction SilentlyContinue
if (-not $java -or -not $javac) {
    Write-Host "Java JDK (javac) not found in PATH. Please install JDK 17+ and retry." -ForegroundColor Red
    exit 1
}

$src = Join-Path $root 'src\main\java'
$out = Join-Path $root 'out'
Remove-Item -Recurse -Force $out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $out | Out-Null

Write-Host "Compiling sources..."
$files = Get-ChildItem -Path $src -Recurse -Include '*.java' | ForEach-Object { $_.FullName }
$cp = $out
$javacArgs = @('-d',$out) + $files
& javac @javacArgs
if ($LASTEXITCODE -ne 0) { Write-Host "Compilation failed" -ForegroundColor Red; exit 1 }

Write-Host "Running application..."
if (Test-Path -Path (Join-Path $root 'gradlew.bat')) {
    Write-Host "Found Gradle wrapper; running 'gradlew run'..."
    & (Join-Path $root 'gradlew.bat') run
} else {
    Write-Host "No Gradle wrapper found; running local JDK build-run fallback..."
    & powershell -NoProfile -ExecutionPolicy Bypass -File (Join-Path $root 'build-and-run.ps1')
}
