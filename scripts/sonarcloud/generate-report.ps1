
# Obtener ruta del script
$script_dir = Split-Path -Parent $MyInvocation.MyCommand.Path

# Leer configuracion
$config = @{}
Get-Content "$script_dir\config.txt" | ForEach-Object {
    $key, $value = $_.Split('=')
    $config[$key] = $value
}

$REPORTS_PATH = $config["REPORTS_PATH"]
$INPUT_FILE = "$REPORTS_PATH\sonarcloud-issues-classified.json"
$OUTPUT_FILE = "$REPORTS_PATH\issues-report.md"

Write-Host ""
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host "GENERAR REPORTE MARKDOWN" -ForegroundColor Cyan
Write-Host "============================================================" -ForegroundColor Cyan
Write-Host ""

# Validar archivo
if (-Not (Test-Path $INPUT_FILE)) {
    Write-Host "ERROR: Archivo no encontrado: $INPUT_FILE" -ForegroundColor Red
    Write-Host "Primero debes ejecutar process-issues.ps1" -ForegroundColor Yellow
    exit 1
}

try {
    Write-Host "Leyendo: $INPUT_FILE" -ForegroundColor Green
    $data = Get-Content $INPUT_FILE | ConvertFrom-Json

    Write-Host "Generando reporte..." -ForegroundColor Yellow
    Write-Host ""

    # Funcion para calcular porcentaje
    function Get-Percentage {
        param([int]$value, [int]$total)
        if ($total -eq 0) { return "0" }
        return [math]::Round(($value / $total) * 100, 2)
    }

    $percent_blocker = Get-Percentage $data.summary.BLOCKER $data.total_issues
    $percent_critical = Get-Percentage $data.summary.CRITICAL $data.total_issues
    $percent_major = Get-Percentage $data.summary.MAJOR $data.total_issues
    $percent_minor = Get-Percentage $data.summary.MINOR $data.total_issues
    $percent_info = Get-Percentage $data.summary.INFO $data.total_issues

    # Crear contenido markdown
    $markdown = @"
# Reporte de Issues - SonarCloud

**Proyecto:** EntregaYa
**Organizacion:** puj-course
**Fecha de Generacion:** $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
**Total de Issues:** $($data.total_issues)

---

## RESUMEN EJECUTIVO

| Severidad | Cantidad | Porcentaje | Prioridad |
|-----------|----------|-----------|-----------|
| BLOCKER | $($data.summary.BLOCKER) | $percent_blocker% | URGENTE |
| CRITICAL | $($data.summary.CRITICAL) | $percent_critical% | URGENTE |
| MAJOR | $($data.summary.MAJOR) | $percent_major% | PROXIMO SPRINT |
| MINOR | $($data.summary.MINOR) | $percent_minor% | BACKLOG |
| INFO | $($data.summary.INFO) | $percent_info% | DOCUMENTAR |

---

## ISSUES BLOCKER (RESOLVER INMEDIATAMENTE)

Total: $($data.summary.BLOCKER)

"@

    if ($data.summary.BLOCKER -gt 0) {
        $markdown += "`n| # | Clave | Tipo | Linea | Componente | Descripcion |`n"
        $markdown += "|---|-------|------|-------|-----------|-------------|`n"

        $counter = 1
        foreach ($issue in $data.issues_by_severity.BLOCKER) {
            $desc = if ($issue.message.Length -gt 60) {
                $issue.message.Substring(0, 60) + "..."
            } else {
                $issue.message
            }
            $component = if ($issue.component) { $issue.component } else { "N/A" }
            $markdown += "| $counter | $($issue.key) | $($issue.type) | $($issue.line) | $component | $desc |`n"
            $counter++
        }
    } else {
        $markdown += "`nNo hay issues BLOCKER. Excelente!`n"
    }

    $markdown += @"

---

## ISSUES CRITICAL (RESOLVER ANTES DEL RELEASE)

Total: $($data.summary.CRITICAL)

"@

    if ($data.summary.CRITICAL -gt 0) {
        $markdown += "`n| # | Clave | Tipo | Linea | Componente | Descripcion |`n"
        $markdown += "|---|-------|------|-------|-----------|-------------|`n"

        $counter = 1
        foreach ($issue in $data.issues_by_severity.CRITICAL) {
            $desc = if ($issue.message.Length -gt 60) {
                $issue.message.Substring(0, 60) + "..."
            } else {
                $issue.message
            }
            $component = if ($issue.component) { $issue.component } else { "N/A" }
            $markdown += "| $counter | $($issue.key) | $($issue.type) | $($issue.line) | $component | $desc |`n"
            $counter++
        }
    } else {
        $markdown += "`nNo hay issues CRITICAL. Muy bien!`n"
    }

    $markdown += @"

---

## ISSUES MAJOR (PROXIMO SPRINT)

Total: $($data.summary.MAJOR)

"@

    if ($data.summary.MAJOR -gt 0) {
        $markdown += "`n| # | Clave | Tipo | Linea | Componente | Descripcion |`n"
        $markdown += "|---|-------|------|-------|-----------|-------------|`n"

        $counter = 1
        $shown = 0
        foreach ($issue in $data.issues_by_severity.MAJOR) {
            if ($shown -lt 20) {
                $desc = if ($issue.message.Length -gt 60) {
                    $issue.message.Substring(0, 60) + "..."
                } else {
                    $issue.message
                }
                $component = if ($issue.component) { $issue.component } else { "N/A" }
                $markdown += "| $counter | $($issue.key) | $($issue.type) | $($issue.line) | $component | $desc |`n"
                $counter++
                $shown++
            }
        }

        if ($data.summary.MAJOR -gt 20) {
            $markdown += "| ... | ... | ... | ... | ... | (+$($data.summary.MAJOR - 20) mas) |`n"
        }
    } else {
        $markdown += "`nNo hay issues MAJOR.`n"
    }

    $markdown += @"

---

## ISSUES MINOR (BACKLOG)

Total: $($data.summary.MINOR)

"@

    if ($data.summary.MINOR -gt 0) {
        $markdown += "`n| # | Clave | Tipo | Linea | Componente | Descripcion |`n"
        $markdown += "|---|-------|------|-------|-----------|-------------|`n"

        $counter = 1
        $shown = 0
        foreach ($issue in $data.issues_by_severity.MINOR) {
            if ($shown -lt 15) {
                $desc = if ($issue.message.Length -gt 60) {
                    $issue.message.Substring(0, 60) + "..."
                } else {
                    $issue.message
                }
                $component = if ($issue.component) { $issue.component } else { "N/A" }
                $markdown += "| $counter | $($issue.key) | $($issue.type) | $($issue.line) | $component | $desc |`n"
                $counter++
                $shown++
            }
        }

        if ($data.summary.MINOR -gt 15) {
            $markdown += "| ... | ... | ... | ... | ... | (+$($data.summary.MINOR - 15) mas) |`n"
        }
    }

    $markdown += @"

---

## PLAN DE ACCION

### FASE 1: BLOCKER (Resolver inmediatamente - 48 horas)
- Asignar a desarrolladores senior
- Crear rama de hotfix
- Code review riguroso
- Testing de regresion completo
- Merge a main

### FASE 2: CRITICAL (Resolver en 1-2 semanas)
- Integrar en sprint actual
- Pair programming
- Testing exhaustivo
- Deadline: 2 semanas

### FASE 3: MAJOR (Resolver en 1-2 meses)
- Distribuir en sprints futuros
- Priorizar con Product Owner
- Integrar con testing

### FASE 4: MINOR e INFO (Resolver en 3-6 meses)
- Asignar a developers junior
- Ejecutar entre tareas principales
- Documentar hallazgos

---

## METRICAS

| Metrica | Actual | Objetivo | Estado |
|---------|--------|----------|--------|
| Issues Totales | $($data.total_issues) | < 50 | Requiere mejora |
| BLOCKER | $($data.summary.BLOCKER) | 0 | Critico |
| CRITICAL | $($data.summary.CRITICAL) | < 5 | Requiere mejora |
| MAJOR | $($data.summary.MAJOR) | < 30 | Requiere mejora |
| MINOR | $($data.summary.MINOR) | < 20 | Requiere mejora |

---

## ENLACES SONARCLOUD

- Dashboard: https://sonarcloud.io/project/overview?id=puj-course_entregaya
- Issues: https://sonarcloud.io/project/issues?id=puj-course_entregaya
- Hotspots: https://sonarcloud.io/project/security_hotspots?id=puj-course_entregaya

---

Reporte generado: $(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')
"@

    # Guardar archivo
    $markdown | Out-File -FilePath $OUTPUT_FILE -Encoding UTF8

    Write-Host "Reporte generado exitosamente" -ForegroundColor Green
    Write-Host "Ubicacion: $OUTPUT_FILE" -ForegroundColor Green
    Write-Host ""
    Write-Host "PASO 3 COMPLETADO" -ForegroundColor Green

} catch {
    Write-Host "ERROR: Fallo en generacion de reporte" -ForegroundColor Red
    Write-Host "Mensaje: $_" -ForegroundColor Red
    exit 1
}
