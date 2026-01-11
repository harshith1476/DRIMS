# Quick Start Script for Backend
Write-Host "=== DRIMS Backend Startup ===" -ForegroundColor Cyan

# Set JAVA_HOME if not set
if (-not $env:JAVA_HOME) {
    $javaPath = "C:\Program Files\Java\jdk-22"
    if (Test-Path $javaPath) {
        $env:JAVA_HOME = $javaPath
        Write-Host "JAVA_HOME set to: $javaPath" -ForegroundColor Green
    } else {
        Write-Host "JAVA_HOME not set. Please set it manually." -ForegroundColor Red
        Write-Host "Run: `$env:JAVA_HOME = 'C:\Program Files\Java\jdk-22'" -ForegroundColor Yellow
        exit 1
    }
} else {
    Write-Host "JAVA_HOME is already set: $env:JAVA_HOME" -ForegroundColor Green
}

# Check if Maven Wrapper exists
if (-not (Test-Path "mvnw.cmd")) {
    Write-Host "Maven Wrapper not found. Setting up..." -ForegroundColor Yellow
    powershell -ExecutionPolicy Bypass -File download-wrapper.ps1
}

# Check MongoDB connection (optional)
Write-Host "`nNote: Make sure MongoDB is running!" -ForegroundColor Yellow
Write-Host "Press Ctrl+C to stop the backend`n" -ForegroundColor Gray

# Start the backend
Write-Host "Starting backend..." -ForegroundColor Cyan
.\mvnw.cmd spring-boot:run

