$script_dir = Split-Path -Parent $MyInvocation.MyCommand.Path

$config = @{}
Get-Content "$script_dir\config.txt" | ForEach-Object {
    $key, $value = $_.Split('=')
    $config[$key] = $value
}

$ORGANIZATION = $config["ORGANIZATION"]
$PROJECT_KEY = $config["PROJECT_KEY"]
$SONAR_URL = $config["SONAR_URL"]
$REPORTS_PATH = $config["REPORTS_PATH"]
$OUTPUT_FILE = "$REPORTS_PATH\sonarcloud-issues-raw.json"

$SONAR_TOKEN = $env:SONAR_TOKEN
if ([string]::IsNullOrEmpty($SONAR_TOKEN)) {
    Write-Host "ERROR: SONAR_TOKEN no configurado" -ForegroundColor Red
    exit 1
}

$headers = @{
    "Authorization" = "Bearer $SONAR_TOKEN"
    "Content-Type" = "application/json"
}

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "EXPORTAR ISSUES DE SONARCLOUD" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Organizacion: $ORGANIZATION" -ForegroundColor Green
Write-Host "Proyecto: $PROJECT_KEY" -ForegroundColor Green
Write-Host ""

$api_url = "$SONAR_URL/api/issues/search?componentKeys=$PROJECT_KEY&maxResults=500"

try {
    Write-Host "Descargando issues..." -ForegroundColor Yellow
    $response = Invoke-RestMethod -Uri $api_url -Headers $headers -Method Get

    if (-Not (Test-Path $REPORTS_PATH)) {
        New-Item -ItemType Directory -Path $REPORTS_PATH -Force | Out-Null
    }

    $response | ConvertTo-Json -Depth 10 | Out-File -FilePath $OUTPUT_FILE -Encoding UTF8

    Write-Host "OK: Guardado" -ForegroundColor Green
    Write-Host "Total: $($response.issues.Count) issues" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Resumen:" -ForegroundColor Cyan

    $severities = @("BLOCKER", "CRITICAL", "MAJOR", "MINOR", "INFO")
    $total = $response.issues.Count

    foreach ($severity in $severities) {
        $count = ($response.issues | Where-Object { $_.severity -eq $severity } | Measure-Object).Count
        $percentage = if ($total -gt 0) { [math]::Round(($count / $total) * 100, 2) } else { 0 }
        Write-Host "$severity .... : $count ($percentage%)" -ForegroundColor White
    }

} catch {
    Write-Host "ERROR: Conexion fallida" -ForegroundColor Red
    Write-Host "Mensaje: $_" -ForegroundColor Red
    exit 1
}
