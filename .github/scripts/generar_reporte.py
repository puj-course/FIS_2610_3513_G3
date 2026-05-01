import xml.etree.ElementTree as ET
import glob, os, html, datetime

PLAN = {
    "CP01": {
        "descripcion": "Builder con datos validos: crea una Tarea correcta",
        "entrada": "nombre=Sprint 1, fechas validas, dificultad=MEDIA",
        "tipo": "Normal",
        "esperado": "Objeto Tarea creado; nombre e instante de inicio iguales a los provistos",
        "metodo": "TareaBuilderTest.CP01_BuilderDatosValidos",
    },
    "CP02": {
        "descripcion": "Builder con nombre null lanza excepcion",
        "entrada": "nombre = null",
        "tipo": "Negativa",
        "esperado": "IllegalStateException",
        "metodo": "TareaBuilderTest.CP02_BuilderNombreNull",
    },
    "CP03": {
        "descripcion": "Builder con nombre en blanco lanza excepcion",
        "entrada": "nombre = solo espacios",
        "tipo": "Borde",
        "esperado": "IllegalStateException",
        "metodo": "TareaBuilderTest.CP03_BuilderNombreBlanco",
    },
    "CP04": {
        "descripcion": "Builder con cronologia invalida (fin antes de inicio)",
        "entrada": "inicio = 2026-05-10, fin = 2026-05-01",
        "tipo": "Negativa",
        "esperado": "IllegalStateException (mensaje contiene fecha)",
        "metodo": "TareaBuilderTest.CP04_BuilderFechasInvalidas",
    },
    "CP05": {
        "descripcion": "Builder con duracion cero (inicio igual a fin)",
        "entrada": "inicio = fin = 2026-05-01T12:00",
        "tipo": "Borde",
        "esperado": "Tarea creada con fechaInicio igual a fechaFinal",
        "metodo": "TareaBuilderTest.CP05_BuilderDuracionCero",
    },
    "CP06": {
        "descripcion": "Factory resuelve tarea completada a CompletadaDecorator",
        "entrada": "completada = true, fechaFinal = ayer",
        "tipo": "Normal",
        "esperado": "Instancia de CompletadaDecorator, etiqueta = Completada",
        "metodo": "TareaDecoratorFactoryTest.CP06_TareaCompletada",
    },
    "CP07": {
        "descripcion": "Factory resuelve tarea vencida a VencidaDecorator",
        "entrada": "completada = false, fechaFinal = ayer",
        "tipo": "Negativa",
        "esperado": "Instancia de VencidaDecorator, color = ef4444",
        "metodo": "TareaDecoratorFactoryTest.CP07_TareaVencida",
    },
    "CP08": {
        "descripcion": "Factory: limite de urgencia exacto (+2 dias) a UrgenteDecorator",
        "entrada": "completada = false, fechaFinal = hoy +2 dias",
        "tipo": "Borde",
        "esperado": "Instancia de UrgenteDecorator, etiqueta = Urgente",
        "metodo": "TareaDecoratorFactoryTest.CP08_TareaUrgente",
    },
    "CP09": {
        "descripcion": "Factory: limite de proximidad exacto (+7 dias) a ProximaDecorator",
        "entrada": "completada = false, fechaFinal = hoy +7 dias",
        "tipo": "Borde",
        "esperado": "Instancia de ProximaDecorator, etiqueta = Proxima",
        "metodo": "TareaDecoratorFactoryTest.CP09_TareaProxima",
    },
    "CP10": {
        "descripcion": "TareaInfoBase con tarea null lanza excepcion",
        "entrada": "tarea = null",
        "tipo": "Negativa",
        "esperado": "IllegalArgumentException",
        "metodo": "TareaDecoratorFactoryTest.CP10_TareaNull",
    },
    "CP11": {
        "descripcion": "LiderOEditorStrategy con rol LIDER retorna true",
        "entrada": "Estrategia = LiderOEditor, Rol = LIDER",
        "tipo": "Normal",
        "esperado": "true",
        "metodo": "PermisoStrategyTest.CP11_LiderOEditorConLider",
    },
    "CP12": {
        "descripcion": "LiderOEditorStrategy con rol COLABORADOR retorna false",
        "entrada": "Estrategia = LiderOEditor, Rol = COLABORADOR",
        "tipo": "Negativa",
        "esperado": "false",
        "metodo": "PermisoStrategyTest.CP12_LiderOEditorConColaborador",
    },
    "CP13": {
        "descripcion": "SoloLiderStrategy con rol EDITOR retorna false",
        "entrada": "Estrategia = SoloLider, Rol = EDITOR",
        "tipo": "Negativa",
        "esperado": "false",
        "metodo": "PermisoStrategyTest.CP13_SoloLiderConEditor",
    },
    "CP14": {
        "descripcion": "LiderOEditorStrategy con rol null retorna false (fail-safe)",
        "entrada": "Estrategia = LiderOEditor, Rol = null",
        "tipo": "Borde",
        "esperado": "false",
        "metodo": "PermisoStrategyTest.CP14_LiderOEditorConNull",
    },
    "CP15": {
        "descripcion": "SoloLiderStrategy con rol null retorna false (fail-safe)",
        "entrada": "Estrategia = SoloLider, Rol = null",
        "tipo": "Borde",
        "esperado": "false",
        "metodo": "PermisoStrategyTest.CP15_SoloLiderConNull",
    },
}

# Leer resultados XML de Surefire
results = {}
for xml_file in glob.glob("target/surefire-reports/TEST-*.xml"):
    tree = ET.parse(xml_file)
    root = tree.getroot()
    for tc in root.findall(".//testcase"):
        name = tc.get("name", "")
        cp_id = None
        for key in PLAN:
            if name.upper().startswith(key):
                cp_id = key
                break
        if cp_id is None:
            continue
        failure = tc.find("failure")
        error   = tc.find("error")
        skipped = tc.find("skipped")
        time_s  = tc.get("time", "")
        if skipped is not None:
            results[cp_id] = {"status": "OMITIDO", "message": "", "time": time_s}
        elif failure is not None:
            msg = (failure.get("message") or failure.text or "").strip().split("\n")[0][:400]
            results[cp_id] = {"status": "FALLIDO", "message": html.escape(msg), "time": time_s}
        elif error is not None:
            msg = (error.get("message") or error.text or "").strip().split("\n")[0][:400]
            results[cp_id] = {"status": "ERROR", "message": html.escape(msg), "time": time_s}
        else:
            results[cp_id] = {"status": "PASADO", "message": "", "time": time_s}

for cp_id in PLAN:
    if cp_id not in results:
        results[cp_id] = {"status": "NO EJECUTADO", "message": "No se encontro en reportes Surefire", "time": "-"}

# Estadisticas
total      = len(PLAN)
pasados    = sum(1 for v in results.values() if v["status"] == "PASADO")
fallidos   = sum(1 for v in results.values() if v["status"] in ("FALLIDO", "ERROR"))
omitidos   = total - pasados - fallidos
porcentaje = round((pasados / total) * 100, 1) if total > 0 else 0
now        = datetime.datetime.utcnow().strftime("%Y-%m-%d %H:%M:%S UTC")
rama       = os.environ.get("GITHUB_REF_NAME", "local")
run_num    = os.environ.get("GITHUB_RUN_NUMBER", "-")

STATUS_INFO = {
    "PASADO":       ("Pasado",       "#e6f4ea", "#1e7e34", "&#10003;"),
    "FALLIDO":      ("Fallido",      "#fce8e6", "#c62828", "&#10007;"),
    "ERROR":        ("Error",        "#fff3e0", "#e65100", "&#9888;"),
    "OMITIDO":      ("Omitido",      "#f5f5f5", "#757575", "&#8212;"),
    "NO EJECUTADO": ("No ejecutado", "#ede7f6", "#4527a0", "?"),
}
TIPO_COLOR = {
    "Normal":   "#e3f2fd",
    "Negativa": "#fce8e6",
    "Borde":    "#fff8e1",
}

# Construir filas
rows = ""
for cp_id, meta in PLAN.items():
    r = results[cp_id]
    label, bg, fg, icon = STATUS_INFO.get(r["status"], ("?", "#fff", "#000", "?"))
    tipo_bg = TIPO_COLOR.get(meta["tipo"], "#fff")
    if r["status"] == "PASADO":
        resultado_real = '<span style="color:#1e7e34;font-weight:600;">' + html.escape(meta["esperado"]) + "</span>"
    elif r["message"]:
        resultado_real = '<span style="color:' + fg + ';">' + r["message"] + "</span>"
    else:
        resultado_real = '<span style="color:' + fg + ';">' + label + "</span>"

    rows += (
        "<tr>"
        + "<td class='cp-id'>" + cp_id + "</td>"
        + "<td>" + html.escape(meta["descripcion"]) + "<br><small style='color:#78909c;'>" + html.escape(meta["metodo"]) + "</small></td>"
        + "<td style='font-family:monospace;font-size:0.85em;'>" + html.escape(meta["entrada"]) + "</td>"
        + "<td style='background:" + tipo_bg + ";text-align:center;'>" + meta["tipo"] + "</td>"
        + "<td>" + html.escape(meta["esperado"]) + "</td>"
        + "<td style='background:" + bg + ";'><span style='color:" + fg + ";font-weight:700;'>" + icon + " " + label + "</span>"
        + ("<br><small>" + resultado_real + "</small>" if r["status"] != "PASADO" else "")
        + "</td>"
        + "</tr>\n"
    )

# HTML final
page = """<!DOCTYPE html>
<html lang="es">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Informe de Pruebas Unitarias - EntregaYA</title>
<style>
*{box-sizing:border-box;margin:0;padding:0}
body{font-family:'Segoe UI',Arial,sans-serif;font-size:14px;background:#f4f6f8;color:#212121}
header{background:linear-gradient(135deg,#00695c,#00897b);color:#fff;padding:24px 40px 20px;border-bottom:4px solid #004d40}
header h1{font-size:1.5em;margin-bottom:8px}
header .meta{font-size:0.84em;opacity:.9;display:flex;gap:24px;flex-wrap:wrap}
.summary{display:flex;gap:16px;padding:24px 40px;flex-wrap:wrap}
.card{flex:1;min-width:130px;background:#fff;border-radius:8px;padding:18px 20px;text-align:center;box-shadow:0 1px 4px rgba(0,0,0,.1);border-top:4px solid #ccc}
.card .num{font-size:2.4em;font-weight:800;line-height:1}
.card .lbl{font-size:0.75em;text-transform:uppercase;letter-spacing:.5px;color:#555;margin-top:6px}
.card.total{border-color:#00897b}.card.total .num{color:#00695c}
.card.pass{border-color:#43a047}.card.pass .num{color:#1e7e34}
.card.fail{border-color:#e53935}.card.fail .num{color:#c62828}
.card.other{border-color:#7e57c2}.card.other .num{color:#4527a0}
.card.pct{border-color:#fb8c00}.card.pct .num{color:#e65100;font-size:2em}
.progress-wrap{margin:0 40px 24px}
.progress-label{font-size:.82em;color:#555;margin-bottom:6px}
.progress-bar{height:10px;background:#e0e0e0;border-radius:5px;overflow:hidden}
.progress-fill{height:100%;background:linear-gradient(90deg,#43a047,#00897b);border-radius:5px}
.section{margin:0 40px 32px;background:#fff;border-radius:8px;box-shadow:0 1px 4px rgba(0,0,0,.1);overflow:hidden}
.section-title{background:#00897b;color:#fff;padding:12px 20px;font-size:.95em;font-weight:700;text-transform:uppercase;letter-spacing:.5px}
table{width:100%;border-collapse:collapse}
thead tr{background:#e0f2f1;border-bottom:2px solid #80cbc4}
thead th{padding:12px 14px;text-align:left;font-size:.82em;font-weight:700;text-transform:uppercase;color:#00695c;white-space:nowrap}
tbody tr{border-bottom:1px solid #eceff1}
tbody tr:hover{background:#f9fbe7}
tbody td{padding:11px 14px;vertical-align:top;font-size:.88em;line-height:1.5}
.cp-id{font-weight:800;color:#00695c;font-size:.95em;white-space:nowrap}
.legend{margin:0 40px 32px;display:flex;gap:20px;flex-wrap:wrap;font-size:.80em;color:#555}
.legend-item{display:flex;align-items:center;gap:6px}
.dot{width:12px;height:12px;border-radius:3px;display:inline-block}
footer{text-align:center;padding:20px;font-size:.75em;color:#9e9e9e;border-top:1px solid #e0e0e0;margin-top:16px}
</style>
</head>
<body>
<header>
  <h1>Informe de Ejecucion de Pruebas Unitarias - EntregaYA</h1>
  <div class="meta">
    <span>Generado: """ + now + """</span>
    <span>Rama: """ + rama + """</span>
    <span>Ejecucion: #""" + run_num + """</span>
  </div>
</header>
<div class="summary">
  <div class="card total"><div class="num">""" + str(total) + """</div><div class="lbl">Total de casos</div></div>
  <div class="card pass"><div class="num">""" + str(pasados) + """</div><div class="lbl">Exitosos</div></div>
  <div class="card fail"><div class="num">""" + str(fallidos) + """</div><div class="lbl">Fallidos</div></div>
  <div class="card other"><div class="num">""" + str(omitidos) + """</div><div class="lbl">Otros</div></div>
  <div class="card pct"><div class="num">""" + str(porcentaje) + """%</div><div class="lbl">Tasa de exito</div></div>
</div>
<div class="progress-wrap">
  <div class="progress-label">Progreso: <strong>""" + str(pasados) + "/" + str(total) + """ casos exitosos</strong></div>
  <div class="progress-bar"><div class="progress-fill" style="width:""" + str(porcentaje) + """%;"></div></div>
</div>
<div class="section">
  <div class="section-title">Plan de Pruebas - Casos de Prueba Ejecutados</div>
  <table>
    <thead>
      <tr>
        <th>Caso de Prueba</th>
        <th>Descripcion</th>
        <th>Entrada de Datos</th>
        <th>Tipo de Prueba</th>
        <th>Resultados Esperados</th>
        <th>Resultados Reales</th>
      </tr>
    </thead>
    <tbody>
""" + rows + """    </tbody>
  </table>
</div>
<div class="legend">
  <strong>Leyenda:</strong>
  <div class="legend-item"><div class="dot" style="background:#e6f4ea;border:1px solid #43a047"></div> Pasado</div>
  <div class="legend-item"><div class="dot" style="background:#fce8e6;border:1px solid #e53935"></div> Fallido</div>
  <div class="legend-item"><div class="dot" style="background:#fff3e0;border:1px solid #fb8c00"></div> Error</div>
  <div class="legend-item"><div class="dot" style="background:#ede7f6;border:1px solid #7e57c2"></div> No ejecutado</div>
  <div class="legend-item"><div class="dot" style="background:#e3f2fd"></div> Normal</div>
  <div class="legend-item"><div class="dot" style="background:#fce8e6"></div> Negativa</div>
  <div class="legend-item"><div class="dot" style="background:#fff8e1"></div> Borde</div>
</div>
<footer>Generado automaticamente por el pipeline CI/CD - Proyecto EntregaYA 2026</footer>
</body>
</html>"""

os.makedirs("target/test-report", exist_ok=True)
with open("target/test-report/informe-pruebas.html", "w", encoding="utf-8") as f:
    f.write(page)

print("Reporte generado: target/test-report/informe-pruebas.html")
print("Total: " + str(total) + " | Pasados: " + str(pasados) + " | Fallidos: " + str(fallidos) + " | Tasa: " + str(porcentaje) + "%")
