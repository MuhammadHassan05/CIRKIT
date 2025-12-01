# create-release.ps1
# Create a ZIP release containing the runnable JAR and helper scripts.
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$dist = Join-Path $root 'dist'
$jar = Join-Path $dist 'logisim-swing-project.jar'
if (-not (Test-Path $jar)) {
    Write-Host "JAR not found, build first with build-and-run.ps1 or gradle." -ForegroundColor Red
    exit 1
}
$releaseDir = Join-Path $root 'release'
Remove-Item -Recurse -Force $releaseDir -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Path $releaseDir | Out-Null
Copy-Item -Path $jar -Destination $releaseDir
Copy-Item -Path (Join-Path $root 'setup-and-run.ps1') -Destination $releaseDir -Force
Copy-Item -Path (Join-Path $root 'build-and-run.ps1') -Destination $releaseDir -Force
Copy-Item -Path (Join-Path $root 'gradlew.bat') -Destination $releaseDir -Force
Copy-Item -Path (Join-Path $root 'gradlew') -Destination $releaseDir -Force
$zip = Join-Path $root ('release-' + (Get-Date -Format yyyyMMddHHmmss) + '.zip')
Compress-Archive -Path (Join-Path $releaseDir '*') -DestinationPath $zip -Force
Write-Host "Created release: $zip"
