import xml.etree.ElementTree as ET
import os, html, datetime, glob

now     = datetime.datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S UTC")
rama    = os.environ.get("GITHUB_REF_NAME", "local")
run_num = os.environ.get("GITHUB_RUN_NUMBER", "-")

# ── Buscar el XML de Checkstyle ──────────────────────────────────────────────
xml_candidates = [
    "target/checkstyle-result.xml",
    "target/checkstyle-results.xml",
    *glob.glob("target/*checkstyle*.xml"),
]
xml_path = next((p for p in xml_candidates if os.path.exists(p)), None)

# ── Parsear ──────────────────────────────────────────────────────────────────
violations = []   # {file, line, col, severity, message, rule}
total_errors   = 0
total_warnings = 0
total_info     = 0

if xml_path:
    tree = ET.parse(xml_path)
    root = tree.getroot()
    for file_el in root.findall(".//file"):
        filepath = file_el.get("name", "")
        # Mostrar solo la parte relativa desde src/
        short = filepath
        if "src/" in filepath:
            short = "src/" + filepath.split("src/", 1)[1]
        for err in file_el.findall("error"):
            sev = err.get("severity", "error").lower()
            rule_raw = err.get("source", "")
            rule = rule_raw.split(".")[-1] if rule_raw else ""
            violations.append({
                "file":     short,
                "line":     err.get("line", "-"),
                "col":      err.get("column", "-"),
                "severity": sev,
                "message":  err.get("message", ""),
                "rule":     rule,
            })
            if sev == "error":
                total_errors += 1
            elif sev == "warning":
                total_warnings += 1
            else:
                total_info += 1

total_violations = len(violations)

# ── Colores por severidad ────────────────────────────────────────────────────
SEV_STYLE = {
    "error":   ("#fce8e6", "#c62828", "❌"),
    "warning": ("#fff8e1", "#e65100", "⚠️"),
    "info":    ("#e3f2fd", "#1565c0", "ℹ️"),
}

# ── Agrupar por archivo ──────────────────────────────────────────────────────
files_map: dict[str, list] = {}
for v in violations:
    files_map.setdefault(v["file"], []).append(v)

# ── Construir filas agrupadas ────────────────────────────────────────────────
rows = ""
if not violations:
    rows = "<tr><td colspan='5' style='text-align:center;padding:24px;color:#2e7d32;font-weight:600;'>✅ Sin violaciones — Checkstyle OK</td></tr>\n"
else:
    for fname, vlist in files_map.items():
        # Cabecera de archivo
        rows += (
            f"<tr style='background:#e0f2f1;'>"
            f"<td colspan='5' style='padding:8px 12px;font-weight:700;font-size:.83em;"
            f"color:#00695c;font-family:monospace;'>"
            f"📄 {html.escape(fname)} "
            f"<span style='font-weight:400;color:#555;'>({len(vlist)} violación{'es' if len(vlist)!=1 else ''})</span>"
            f"</td></tr>\n"
        )
        for v in vlist:
            bg, fg, icon = SEV_STYLE.get(v["severity"], ("#fff", "#000", "•"))
            rows += (
                f"<tr>"
                f"<td style='padding-left:28px;font-family:monospace;font-size:.82em;color:#555;'>"
                f"L{html.escape(v['line'])}:{html.escape(v['col'])}</td>"
                f"<td style='background:{bg};text-align:center;font-size:.82em;'>"
                f"<span style='color:{fg};font-weight:700;'>{icon} {html.escape(v['severity'].capitalize())}</span></td>"
                f"<td style='font-size:.84em;'>{html.escape(v['message'])}</td>"
                f"<td style='font-family:monospace;font-size:.78em;color:#7986cb;'>{html.escape(v['rule'])}</td>"
                f"</tr>\n"
            )

# ── Estado general ────────────────────────────────────────────────────────────
if not xml_path:
    status_label = "SIN DATOS"
    status_bg    = "#ede7f6"
    status_fg    = "#4527a0"
    status_msg   = "No se encontró el XML de Checkstyle"
elif total_errors > 0:
    status_label = "FALLIDO"
    status_bg    = "#fce8e6"
    status_fg    = "#c62828"
    status_msg   = f"{total_errors} error(es) encontrado(s)"
elif total_warnings > 0:
    status_label = "ADVERTENCIAS"
    status_bg    = "#fff8e1"
    status_fg    = "#e65100"
    status_msg   = f"{total_warnings} advertencia(s) encontrada(s)"
else:
    status_label = "OK"
    status_bg    = "#e6f4ea"
    status_fg    = "#1e7e34"
    status_msg   = "Sin errores ni advertencias"

# ── HTML ─────────────────────────────────────────────────────────────────────
page = f"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Reporte Checkstyle - EntregaYA</title>
<style>
* {{ box-sizing: border-box; margin: 0; padding: 0; }}
body {{ font-family: 'Segoe UI', Arial, sans-serif; font-size: 14px; background: #f4f6f8; color: #212121; }}

header {{ background: #37474f; color: #fff; padding: 20px 32px; }}
header h1 {{ font-size: 1.3em; margin-bottom: 6px; }}
header .meta {{ font-size: .82em; opacity: .85; display: flex; gap: 20px; flex-wrap: wrap; }}

.summary {{ display: flex; gap: 12px; padding: 20px 32px; flex-wrap: wrap; }}
.card {{ flex: 1; min-width: 120px; background: #fff; border-radius: 6px; padding: 14px 16px;
         text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,.1); border-top: 3px solid #ccc; }}
.card .num {{ font-size: 2em; font-weight: 800; line-height: 1; }}
.card .lbl {{ font-size: .72em; text-transform: uppercase; letter-spacing: .5px; color: #555; margin-top: 5px; }}
.card.total   {{ border-color: #546e7a; }} .card.total   .num {{ color: #37474f; }}
.card.errors  {{ border-color: #e53935; }} .card.errors  .num {{ color: #c62828; }}
.card.warns   {{ border-color: #fb8c00; }} .card.warns   .num {{ color: #e65100; }}
.card.info    {{ border-color: #1e88e5; }} .card.info    .num {{ color: #1565c0; }}
.card.status  {{ border-color: {status_fg}; }}
.card.status  .num {{ color: {status_fg}; font-size: 1.1em; }}

.section {{ margin: 0 32px 28px; background: #fff; border-radius: 6px;
            box-shadow: 0 1px 3px rgba(0,0,0,.1); overflow: hidden; }}
.section-title {{ background: #546e7a; color: #fff; padding: 10px 18px;
                  font-size: .88em; font-weight: 700; text-transform: uppercase; letter-spacing: .5px; }}

table {{ width: 100%; border-collapse: collapse; }}
thead tr {{ background: #eceff1; border-bottom: 2px solid #b0bec5; }}
thead th {{ padding: 10px 12px; text-align: left; font-size: .78em; font-weight: 700;
            text-transform: uppercase; color: #37474f; white-space: nowrap; }}
tbody tr {{ border-bottom: 1px solid #eceff1; }}
tbody tr:hover {{ background: #fafafa; }}
tbody td {{ padding: 8px 12px; vertical-align: top; font-size: .86em; line-height: 1.5; }}

footer {{ text-align: center; padding: 16px; font-size: .72em;
          color: #9e9e9e; border-top: 1px solid #e0e0e0; }}
</style>
</head>
<body>
<header>
  <h1>📋 Reporte Checkstyle - EntregaYA</h1>
  <div class="meta">
    <span>Generado: {now}</span>
    <span>Rama: {rama}</span>
    <span>Ejecución: #{run_num}</span>
    {'<span>Fuente: ' + html.escape(xml_path) + '</span>' if xml_path else '<span style="color:#ffcdd2;">⚠️ XML no encontrado</span>'}
  </div>
</header>

<div class="summary">
  <div class="card total">
    <div class="num">{total_violations}</div>
    <div class="lbl">Total violaciones</div>
  </div>
  <div class="card errors">
    <div class="num">{total_errors}</div>
    <div class="lbl">Errores</div>
  </div>
  <div class="card warns">
    <div class="num">{total_warnings}</div>
    <div class="lbl">Advertencias</div>
  </div>
  <div class="card info">
    <div class="num">{total_info}</div>
    <div class="lbl">Informativos</div>
  </div>
  <div class="card status">
    <div class="num" style="color:{status_fg};">{status_label}</div>
    <div class="lbl">{html.escape(status_msg)}</div>
  </div>
</div>

<div class="section">
  <div class="section-title">Violaciones por Archivo</div>
  <table>
    <thead>
      <tr>
        <th>Línea:Col</th>
        <th>Severidad</th>
        <th>Mensaje</th>
        <th>Regla</th>
      </tr>
    </thead>
    <tbody>
{rows}    </tbody>
  </table>
</div>

<footer>Generado automáticamente por el pipeline CI/CD - Proyecto EntregaYA 2026</footer>
</body>
</html>"""

os.makedirs("target/checkstyle-report", exist_ok=True)
out = "target/checkstyle-report/informe-checkstyle.html"
with open(out, "w", encoding="utf-8") as f:
    f.write(page)

print(f"Reporte Checkstyle generado: {out}")
print(f"Errores: {total_errors} | Advertencias: {total_warnings} | Info: {total_info} | Total: {total_violations}")
