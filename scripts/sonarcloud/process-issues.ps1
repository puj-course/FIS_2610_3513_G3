$script_dir = Split-Path -Parent $MyInvocation.MyCommand.Path

$config = @{}
Get-Content "$script_dir\config.txt" | ForEach-Object {
    $key, $value = $_.Split('=')
    $config[$key] = $value
}

$REPORTS_PATH = $config["REPORTS_PATH"]
$INPUT_FILE = "$REPORTS_PATH\sonarcloud-issues-raw.json"
$OUTPUT_FILE = "$REPORTS_PATH\sonarcloud-issues-classified.json"

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "PROCESAR ISSUES" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Leyendo archivo: $INPUT_FILE" -ForegroundColor Green
Write-Host ""

if (-Not (Test-Path $INPUT_FILE)) {
    Write-Host "ERROR: Archivo no encontrado: $INPUT_FILE" -ForegroundColor Red
    Write-Host "Primero debes ejecutar export-issues.ps1" -ForegroundColor Yellow
    exit 1
}

try {
    $issues = Get-Content $INPUT_FILE | ConvertFrom-Json

    $classified = @{
        BLOCKER = @()
        CRITICAL = @()
        MAJOR = @()
        MINOR = @()
        INFO = @()
    }

    foreach ($issue in $issues.issues) {
        $classified[$issue.severity] += $issue
    }

    $report = @{
        timestamp = Get-Date -Format "yyyy-MM-dd HH:mm:ss"
        total_issues = $issues.issues.Count
        summary = @{
            BLOCKER = $classified["BLOCKER"].Count
            CRITICAL = $classified["CRITICAL"].Count
            MAJOR = $classified["MAJOR"].Count
            MINOR = $classified["MINOR"].Count
            INFO = $classified["INFO"].Count
        }
        issues_by_severity = $classified
    }

    $report | ConvertTo-Json -Depth 10 | Out-File -FilePath $OUTPUT_FILE -Encoding UTF8

    Write-Host "Procesamiento completado" -ForegroundColor Green
    Write-Host ""
    Write-Host "Archivo guardado: $OUTPUT_FILE" -ForegroundColor Green
    Write-Host ""
    Write-Host "RESULTADO:" -ForegroundColor Cyan
    Write-Host "BLOCKER ... : $($report.summary.BLOCKER) issues" -ForegroundColor Red
    Write-Host "CRITICAL .. : $($report.summary.CRITICAL) issues" -ForegroundColor Red
    Write-Host "MAJOR ..... : $($report.summary.MAJOR) issues" -ForegroundColor Yellow
    Write-Host "MINOR ..... : $($report.summary.MINOR) issues" -ForegroundColor Cyan
    Write-Host "INFO ...... : $($report.summary.INFO) issues" -ForegroundColor Blue
    Write-Host ""
    Write-Host "TOTAL: $($report.total_issues) issues" -ForegroundColor Green
    Write-Host ""

} catch {
    Write-Host "ERROR: Fallo en procesamiento" -ForegroundColor Red
    Write-Host "Mensaje: $_" -ForegroundColor Red
    exit 1
}
