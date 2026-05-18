"""
Reporte de Latencia - HU-50
Lee los XMLs de Surefire y extrae el tiempo de ejecución de cada testcase.
Marca en rojo los que superen UMBRAL_MS ms.
"""
import xml.etree.ElementTree as ET
import glob, os, html, datetime

UMBRAL_MS = 3000  # milisegundos — umbral definido en HU-50

now     = datetime.datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S UTC")
rama    = os.environ.get("GITHUB_REF_NAME", "local")
run_num = os.environ.get("GITHUB_RUN_NUMBER", "-")

# ── Recolectar todos los testcases con su tiempo ─────────────────────────────
tests = []   # {class, name, time_ms, status}

for xml_file in sorted(glob.glob("target/surefire-reports/TEST-*.xml")):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    classname = root.get("name", os.path.basename(xml_file))

    for tc in root.findall(".//testcase"):
        name     = tc.get("name", "")
        time_s   = tc.get("time", "0") or "0"
        try:
            time_ms = round(float(time_s) * 1000, 1)
        except ValueError:
            time_ms = 0.0

        if tc.find("skipped") is not None:
            status = "OMITIDO"
        elif tc.find("failure") is not None or tc.find("error") is not None:
            status = "FALLIDO"
        else:
            status = "PASADO"

        tests.append({
            "class":   classname.split(".")[-1],    # solo nombre corto
            "name":    name,
            "time_ms": time_ms,
            "status":  status,
            "slow":    time_ms > UMBRAL_MS and status != "OMITIDO",
        })

# ── Estadísticas ─────────────────────────────────────────────────────────────
total      = len(tests)
lentos     = sum(1 for t in tests if t["slow"])
max_ms     = max((t["time_ms"] for t in tests), default=0)
avg_ms     = round(sum(t["time_ms"] for t in tests) / total, 1) if total else 0
umbral_ok  = lentos == 0

# Ordenar: lentos primero, luego por tiempo desc
tests.sort(key=lambda t: (-t["slow"], -t["time_ms"]))

# ── Filas ─────────────────────────────────────────────────────────────────────
STATUS_STYLE = {
    "PASADO":  ("#e6f4ea", "#1e7e34"),
    "FALLIDO": ("#fce8e6", "#c62828"),
    "OMITIDO": ("#f5f5f5", "#757575"),
}

rows = ""
if not tests:
    rows = "<tr><td colspan='4' style='text-align:center;padding:24px;color:#7986cb;'>No se encontraron XMLs de Surefire en target/surefire-reports/</td></tr>\n"
else:
    for t in tests:
        # Color de latencia: rojo si supera umbral, amarillo si >250ms, verde si OK
        if t["slow"]:
            lat_bg, lat_fg = "#fce8e6", "#c62828"
            lat_icon = "🔴"
        elif t["time_ms"] > UMBRAL_MS * 0.5:
            lat_bg, lat_fg = "#fff8e1", "#e65100"
            lat_icon = "🟡"
        else:
            lat_bg, lat_fg = "#e6f4ea", "#1e7e34"
            lat_icon = "🟢"

        st_bg, st_fg = STATUS_STYLE.get(t["status"], ("#fff", "#000"))
        slow_note = " ⚠️ SUPERA UMBRAL" if t["slow"] else ""

        # Barra visual de latencia (máx 200px)
        bar_w = min(int((t["time_ms"] / max(max_ms, 1)) * 200), 200)
        bar_color = lat_fg

        rows += (
            f"<tr>"
            f"<td style='font-size:.82em;color:#555;font-family:monospace;'>{html.escape(t['class'])}</td>"
            f"<td style='font-size:.84em;'>{html.escape(t['name'])}</td>"
            f"<td style='background:{st_bg};text-align:center;'>"
            f"<span style='color:{st_fg};font-weight:600;font-size:.82em;'>{t['status']}</span></td>"
            f"<td style='background:{lat_bg};white-space:nowrap;'>"
            f"<span style='color:{lat_fg};font-weight:700;'>{lat_icon} {t['time_ms']} ms{html.escape(slow_note)}</span><br>"
            f"<div style='height:4px;background:#e0e0e0;border-radius:2px;margin-top:4px;width:200px;'>"
            f"<div style='height:4px;width:{bar_w}px;background:{bar_color};border-radius:2px;'></div></div>"
            f"</td>"
            f"</tr>\n"
        )

# ── Estado global ─────────────────────────────────────────────────────────────
if not tests:
    gstatus_label, gstatus_fg = "SIN DATOS", "#4527a0"
elif umbral_ok:
    gstatus_label, gstatus_fg = "OK ✅",     "#1e7e34"
else:
    gstatus_label, gstatus_fg = "LENTO ❌",  "#c62828"

# ── HTML ──────────────────────────────────────────────────────────────────────
page = f"""<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Reporte de Latencia - EntregaYA</title>
<style>
* {{ box-sizing: border-box; margin: 0; padding: 0; }}
body {{ font-family: 'Segoe UI', Arial, sans-serif; font-size: 14px; background: #f4f6f8; color: #212121; }}

header {{ background: #1a237e; color: #fff; padding: 20px 32px; }}
header h1 {{ font-size: 1.3em; margin-bottom: 6px; }}
header .meta {{ font-size: .82em; opacity: .85; display: flex; gap: 20px; flex-wrap: wrap; }}

.summary {{ display: flex; gap: 12px; padding: 20px 32px; flex-wrap: wrap; }}
.card {{ flex: 1; min-width: 120px; background: #fff; border-radius: 6px; padding: 14px 16px;
         text-align: center; box-shadow: 0 1px 3px rgba(0,0,0,.1); border-top: 3px solid #ccc; }}
.card .num {{ font-size: 2em; font-weight: 800; line-height: 1; }}
.card .lbl {{ font-size: .72em; text-transform: uppercase; letter-spacing: .5px; color: #555; margin-top: 5px; }}
.card.total  {{ border-color: #3949ab; }} .card.total  .num {{ color: #1a237e; }}
.card.slow   {{ border-color: #e53935; }} .card.slow   .num {{ color: #c62828; }}
.card.max    {{ border-color: #fb8c00; }} .card.max    .num {{ color: #e65100; font-size:1.4em; }}
.card.avg    {{ border-color: #00897b; }} .card.avg    .num {{ color: #00695c; font-size:1.4em; }}
.card.status {{ border-color: {gstatus_fg}; }}
.card.status .num {{ color: {gstatus_fg}; font-size: 1.1em; }}
.umbral-badge {{ display:inline-block; background:#e8eaf6; color:#1a237e;
                 border-radius:4px; padding:2px 8px; font-size:.78em; font-weight:700; }}

.section {{ margin: 0 32px 28px; background: #fff; border-radius: 6px;
            box-shadow: 0 1px 3px rgba(0,0,0,.1); overflow: hidden; }}
.section-title {{ background: #283593; color: #fff; padding: 10px 18px;
                  font-size: .88em; font-weight: 700; text-transform: uppercase; letter-spacing: .5px; }}

table {{ width: 100%; border-collapse: collapse; }}
thead tr {{ background: #e8eaf6; border-bottom: 2px solid #9fa8da; }}
thead th {{ padding: 10px 12px; text-align: left; font-size: .78em; font-weight: 700;
            text-transform: uppercase; color: #1a237e; white-space: nowrap; }}
tbody tr {{ border-bottom: 1px solid #eceff1; }}
tbody tr:hover {{ background: #fafafa; }}
tbody td {{ padding: 9px 12px; vertical-align: middle; font-size: .86em; line-height: 1.5; }}

.legend {{ margin: 0 32px 28px; display: flex; gap: 16px; flex-wrap: wrap;
           font-size: .78em; color: #555; align-items: center; }}
.dot {{ width: 11px; height: 11px; border-radius: 50%; display: inline-block; }}
.legend-item {{ display: flex; align-items: center; gap: 5px; }}

footer {{ text-align: center; padding: 16px; font-size: .72em;
          color: #9e9e9e; border-top: 1px solid #e0e0e0; }}
</style>
</head>
<body>
<header>
  <h1>⏱️ Reporte de Latencia - EntregaYA (HU-50)</h1>
  <div class="meta">
    <span>Generado: {now}</span>
    <span>Rama: {rama}</span>
    <span>Ejecución: #{run_num}</span>
    <span>Umbral: {UMBRAL_MS} ms</span>
  </div>
</header>

<div class="summary">
  <div class="card total">
    <div class="num">{total}</div>
    <div class="lbl">Tests analizados</div>
  </div>
  <div class="card slow">
    <div class="num">{lentos}</div>
    <div class="lbl">Superan umbral</div>
  </div>
  <div class="card max">
    <div class="num">{max_ms} ms</div>
    <div class="lbl">Tiempo máximo</div>
  </div>
  <div class="card avg">
    <div class="num">{avg_ms} ms</div>
    <div class="lbl">Tiempo promedio</div>
  </div>
  <div class="card status">
    <div class="num">{gstatus_label}</div>
    <div class="lbl">Estado HU-50 <span class="umbral-badge">≤{UMBRAL_MS}ms</span></div>
  </div>
</div>

<div class="section">
  <div class="section-title">Tiempos de Ejecución por Test (ordenado: lentos primero)</div>
  <table>
    <thead>
      <tr>
        <th>Clase</th>
        <th>Método</th>
        <th>Estado</th>
        <th>Latencia</th>
      </tr>
    </thead>
    <tbody>
{rows}    </tbody>
  </table>
</div>

<div class="legend">
  <strong>Latencia:</strong>
  <div class="legend-item"><div class="dot" style="background:#c62828;"></div> Supera {UMBRAL_MS} ms (falla HU-50)</div>
  <div class="legend-item"><div class="dot" style="background:#e65100;"></div> Supera {UMBRAL_MS // 2} ms (advertencia)</div>
  <div class="legend-item"><div class="dot" style="background:#1e7e34;"></div> OK</div>
</div>

<footer>Generado automáticamente por el pipeline CI/CD - Proyecto EntregaYA 2026</footer>
</body>
</html>"""

os.makedirs("target/latency-report", exist_ok=True)
out = "target/latency-report/informe-latencia.html"
with open(out, "w", encoding="utf-8") as f:
    f.write(page)

print(f"Reporte de latencia generado: {out}")
print(f"Tests: {total} | Lentos (>{UMBRAL_MS}ms): {lentos} | Máx: {max_ms}ms | Prom: {avg_ms}ms")

# Salir con error si hay tests lentos (para bloquear el pipeline si se desea)
# Comentar la siguiente línea si solo quieres el reporte sin bloquear
if lentos > 0:
    print(f"\n⚠️  {lentos} test(s) superan el umbral de {UMBRAL_MS} ms definido en HU-50")
    # exit(1)  # Descomentar para hacer el stage bloqueante
