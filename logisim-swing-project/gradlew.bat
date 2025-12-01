@echo off
REM Lightweight Gradle shim: use system Gradle if available, otherwise fallback to build-and-run.ps1
where gradle >nul 2>&1
if %errorlevel%==0 (
  gradle %*
) else (
  powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0build-and-run.ps1" %*
)
