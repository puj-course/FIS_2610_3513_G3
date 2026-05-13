Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "EXPORTADOR DE ISSUES SONARCLOUD" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

$script_dir = Split-Path -Parent $MyInvocation.MyCommand.Path
$start_time = Get-Date

Write-Host "Ubicacion: $script_dir" -ForegroundColor Yellow
Write-Host ""

Write-Host "Validando archivos..." -ForegroundColor Yellow
$required_files = @(
    "config.txt",
    "export-issues.ps1",
    "process-issues.ps1",
    "generate-report.ps1"
)

foreach ($file in $required_files) {
    if (-Not (Test-Path "$script_dir\$file")) {
        Write-Host "ERROR: $file no encontrado" -ForegroundColor Red
        exit 1
    }
}

Write-Host "OK" -ForegroundColor Green
Write-Host ""

if ([string]::IsNullOrEmpty($env:SONAR_TOKEN)) {
    Write-Host "ADVERTENCIA: SONAR_TOKEN no configurado en environment" -ForegroundColor Yellow
    Write-Host "Se ejecuta mejor en GitHub Actions" -ForegroundColor Yellow
    Write-Host ""
}

Write-Host "[1/3] EXPORTANDO..." -ForegroundColor Cyan
& "$script_dir\export-issues.ps1"
if ($LASTEXITCODE -ne 0) { exit 1 }
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "[2/3] PROCESANDO..." -ForegroundColor Cyan
& "$script_dir\process-issues.ps1"
if ($LASTEXITCODE -ne 0) { exit 1 }
Start-Sleep -Seconds 2

Write-Host ""
Write-Host "[3/3] GENERANDO REPORTE..." -ForegroundColor Cyan
& "$script_dir\generate-report.ps1"
if ($LASTEXITCODE -ne 0) { exit 1 }

$end_time = Get-Date
$duration = ($end_time - $start_time).TotalSeconds

Write-Host ""
Write-Host "============================================================" -ForegroundColor Green
Write-Host "COMPLETADO" -ForegroundColor Green
Write-Host "============================================================" -ForegroundColor Green
Write-Host ""
Write-Host "Tiempo: $([math]::Round($duration, 2))s" -ForegroundColor Yellow
Write-Host ""

$config = @{}
Get-Content "$script_dir\config.txt" | ForEach-Object {
    $key, $value = $_.Split('=')
    $config[$key] = $value
}

$REPORTS_PATH = $config["REPORTS_PATH"]
Write-Host "Archivos en: $REPORTS_PATH" -ForegroundColor Green
Write-Host ""
Write-Host "1. sonarcloud-issues-raw.json" -ForegroundColor White
Write-Host "2. sonarcloud-issues-classified.json" -ForegroundColor White
Write-Host "3. issues-report.md" -ForegroundColor White
Write-Host ""

if ([System.Environment]::OSVersion.Platform -eq "Win32NT") {
    try {
        Invoke-Item "$REPORTS_PATH\issues-report.md"
    } catch {
        Write-Host "No se pudo abrir el archivo" -ForegroundColor Yellow
    }
}
