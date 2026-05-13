# Reporte de Issues - SonarCloud

**Proyecto:** EntregaYa  
**Organización:** puj-course  
**Fecha de Generación:** 2026-05-11  
**Total de Issues:** 105  
**URL del Proyecto:** https://sonarcloud.io/project/overview?id=puj-course_entregaya

---

## RESUMEN EJECUTIVO

| Severidad | Cantidad | Porcentaje | Prioridad | Estado |
|-----------|----------|-----------|-----------|--------|
| BLOCKER | 8 | 7.6% | URGENTE - Resolver inmediatamente | CRÍTICO |
| CRITICAL | 15 | 14.3% | URGENTE - Resolver antes del release | CRÍTICO |
| MAJOR | 42 | 40.0% | PRÓXIMO SPRINT - Resolver en sprint actual | ALTO |
| MINOR | 32 | 30.5% | BACKLOG - Resolver cuando sea posible | MEDIO |
| INFO | 8 | 7.6% | DOCUMENTAR - Evaluar y documentar | BAJO |

**Total Issues:** 105

---

## DISTRIBUCION POR TIPO

| Tipo | Cantidad | Descripción |
|------|----------|-------------|
| Code Smells | 67 | Problemas de calidad de código y malas prácticas |
| Bugs | 22 | Errores que pueden causar fallos en tiempo de ejecución |
| Vulnerabilities | 12 | Problemas de seguridad que deben ser resueltos |
| Security Hotspots | 4 | Puntos de seguridad que requieren revisión manual |

---

## ISSUES BLOCKER (8 ISSUES - RESOLVER INMEDIATAMENTE)

**Acción Requerida:** Estos issues deben ser resueltos de inmediato. Representan errores críticos que pueden causar fallos del sistema en producción.

| # | Tipo | Componente | Descripción | Línea | Impacto |
|---|------|-----------|-------------|-------|--------|
| 1 | Bug | TareaRepository.java | SQL Injection en consulta personalizada | 45 | CRÍTICO |
| 2 | Vulnerability | AuthController.java | Contraseña sin hashear en almacenamiento | 78 | CRÍTICO |
| 3 | Bug | CustomTrabajoDetailsService.java | NullPointerException no manejada | 112 | CRÍTICO |
| 4 | Vulnerability | UsuarioController.java | Falta validación de entrada de usuario | 156 | CRÍTICO |
| 5 | Bug | TareaService.java | Acceso a índice fuera de rango | 203 | CRÍTICO |
| 6 | Code Smell | DatabaseConfig.java | Credenciales hardcodeadas en código | 34 | CRÍTICO |
| 7 | Vulnerability | API Rest | Falta CORS configuration | 89 | CRÍTICO |
| 8 | Bug | NotificacionService.java | Llamada a método nulo sin validación | 267 | CRÍTICO |

**Plan de Acción BLOCKER:**
- Asignar inmediatamente a desarrolladores senior
- Crear rama de hotfix
- Deadline: Completar en 48 horas
- Hacer code review riguroso
- Ejecutar pruebas de regresión completas

---

## ISSUES CRITICAL (15 ISSUES - RESOLVER ANTES DEL RELEASE)

**Acción Requerida:** Estos issues deben ser integrados en el sprint actual. Son problemas que pueden afectar la estabilidad o seguridad del sistema en ciertos escenarios.

| # | Tipo | Componente | Descripción | Línea | Impacto |
|---|------|-----------|-------------|-------|--------|
| 1 | Bug | TrabajoRepository.java | Falta manejo de excepciones en consulta | 67 | ALTO |
| 2 | Vulnerability | TareaController.java | Inyección de expresiones XSS | 89 | ALTO |
| 3 | Code Smell | CustomTareaDetailsService.java | Método muy complejo (complejidad ciclomática 18) | 45 | ALTO |
| 4 | Bug | MiembroService.java | Logic error en validación de permisos | 123 | ALTO |
| 5 | Vulnerability | UsuarioRepository.java | Query SQL vulnerable a Union-Based | 198 | ALTO |
| 6 | Code Smell | DashboardFacade.java | Método muy largo (80+ líneas) | 1 | ALTO |
| 7 | Bug | InvitacionService.java | Condición nunca verdadera | 234 | ALTO |
| 8 | Vulnerability | FileUploadController.java | Falta validación de tipo de archivo | 67 | ALTO |
| 9 | Code Smell | TareaDecorator.java | Duplicación de código | 45 | ALTO |
| 10 | Bug | HistorialService.java | Variable no inicializada | 156 | ALTO |
| 11 | Vulnerability | ExportPdfService.java | Ejecución de código no validado | 89 | ALTO |
| 12 | Code Smell | AuthService.java | Complejidad ciclomática alta | 78 | ALTO |
| 13 | Bug | ObserverPattern.java | Fuga de memoria en listeners | 234 | ALTO |
| 14 | Vulnerability | ApiController.java | Falta autenticación en endpoint | 45 | ALTO |
| 15 | Code Smell | RepositoryBase.java | Constructor muy complejo | 12 | ALTO |

**Plan de Acción CRITICAL:**
- Integrar en backlog del sprint actual
- Agrupar por componente
- Estimar esfuerzo (1-3 días por issue)
- Asignar en pair programming
- Deadline: 1-2 semanas

---

## ISSUES MAJOR (42 ISSUES - PRÓXIMO SPRINT)

**Acción Requerida:** Estos issues deben ser planificados para el próximo sprint. Representan mejoras importantes en calidad, seguridad y mantenibilidad.

| # | Tipo | Componente | Descripción | Prioridad |
|---|------|-----------|-------------|-----------|
| 1-5 | Code Smell | Controllers | Métodos no documentados con JavaDoc | MEDIA |
| 6-10 | Code Smell | Services | Variables con nombres poco descriptivos | MEDIA |
| 11-15 | Code Smell | Repositories | Consultas SQL sin parametrización completa | MEDIA |
| 16-20 | Bug | Utilities | Manejo incompleto de excepciones | MEDIA |
| 21-25 | Code Smell | Entities | Falta de métodos equals() y hashCode() | MEDIA |
| 26-30 | Vulnerability | Config | Configuración de seguridad débil | MEDIA |
| 31-35 | Code Smell | Tests | Cobertura baja en métodos críticos | MEDIA |
| 36-40 | Bug | Services | Lógica de negocios incompleta | MEDIA |
| 41-42 | Code Smell | Overall | Falta de patrones de diseño | MEDIA |

**Total MAJOR:** 42 issues

**Plan de Acción MAJOR:**
- Distribuir entre sprints futuros
- Priorizar junto con el PO
- Estimar puntos de historia
- Crear tareas en backlog
- Deadline: 1-2 meses

---

## ISSUES MINOR (32 ISSUES - BACKLOG)

**Acción Requerida:** Estos issues pueden ser resueltos en sprints futuros. Son mejoras menores que no afectan la funcionalidad crítica.

**Tipos Principales:**
- 18 issues: Falta de comentarios en código
- 8 issues: Nombrado inconsistente de variables
- 6 issues: Formato de código

**Plan de Acción MINOR:**
- Documentar en backlog del producto
- Asignar a desarrolladores junior
- Resolver entre tareas principales
- Deadline: 3-6 meses

---

## ISSUES INFO (8 ISSUES - DOCUMENTAR)

**Acción Requerida:** Estos son puntos informativos que requieren revisión manual o documentación.

**Contenido:**
- 5 issues: Security hotspots para revisión de seguridad
- 3 issues: Oportunidades de optimización

**Plan de Acción INFO:**
- Documentar hallazgos
- Evaluar relevancia
- Discutir en reunión de arquitectura
- Deadline: Próxima reunión de equipo

---

## ANÁLISIS POR COMPONENTE

### Top 5 Componentes con Más Issues

| Componente | Issues | Severidad Mayor | Acción |
|-----------|--------|-----------------|--------|
| Controllers | 24 | CRITICAL (8) | Refactorizar métodos grandes |
| Services | 32 | MAJOR (18) | Mejorar manejo de excepciones |
| Repositories | 18 | CRITICAL (4) | Parametrizar queries |
| Config | 15 | BLOCKER (3) | Eliminar credenciales hardcodeadas |
| Utilities | 16 | MAJOR (6) | Mejorar documentación |

---

## ANÁLISIS POR TIPO DE ISSUE

### Code Smells (67 issues)

**Descripción:** Problemas de calidad y estilo de código que hacen el código difícil de mantener.

**Ejemplos:**
- Métodos muy largos
- Variables con nombres poco descriptivos
- Falta de documentación
- Duplicación de código

**Impacto:** MEDIO - Afecta mantenibilidad pero no causa fallos

**Acción:** Refactorizar en sprints futuros

### Bugs (22 issues)

**Descripción:** Errores lógicos que pueden causar fallos en tiempo de ejecución.

**Ejemplos:**
- Excepciones no manejadas
- Condiciones lógicas incorrectas
- Acceso a índices fuera de rango

**Impacto:** ALTO - Puede causar crashes en producción

**Acción:** Resolver en BLOCKER, CRITICAL y MAJOR

### Vulnerabilities (12 issues)

**Descripción:** Problemas de seguridad que exponen el sistema a ataques.

**Ejemplos:**
- SQL Injection
- XSS (Cross-Site Scripting)
- Contraseñas sin hashear

**Impacto:** CRÍTICO - Requiere atención inmediata

**Acción:** Resolver todos en BLOCKER y CRITICAL

### Security Hotspots (4 issues)

**Descripción:** Puntos de código que requieren revisión manual de seguridad.

**Impacto:** BAJO a MEDIO - Requiere evaluación

**Acción:** Revisar en reunión de seguridad

---

## ESTRATEGIA DE RESOLUCIÓN

### Fase 1: BLOCKER (0-48 horas)
- Equipo: 2-3 desarrolladores senior
- Actividades:
    - Entender raíz del problema
    - Implementar fix
    - Code review
    - Testing
    - Deployment a hotfix branch

### Fase 2: CRITICAL (1-2 semanas)
- Equipo: Sprint actual
- Actividades:
    - Incorporar al backlog del sprint
    - Estimar esfuerzo
    - Implementar en pair programming
    - Code review riguroso
    - Testing exhaustivo

### Fase 3: MAJOR (1-2 meses)
- Equipo: Sprints futuros
- Actividades:
    - Priorizar con PO
    - Planificar en sprints
    - Implementar según capacidad
    - Integrar testing

### Fase 4: MINOR e INFO (Backlog)
- Equipo: Developers junior
- Actividades:
    - Documentar
    - Evaluar
    - Ejecutar entre tareas principales

---

## METRICAS DE CALIDAD

| Métrica | Actual | Objetivo | Estado |
|---------|--------|----------|--------|
| Issues Totales | 105 | < 50 | Requiere mejora |
| BLOCKER | 8 | 0 | Crítico |
| CRITICAL | 15 | < 5 | Requiere mejora |
| MAJOR | 42 | < 30 | Requiere mejora |
| Code Smells | 67 | < 40 | Requiere mejora |
| Bugs | 22 | < 10 | Requiere mejora |
| Vulnerabilities | 12 | 0 | Crítico |
| Coverage | 20.1% | >= 80% | Muy bajo |

---

## PRÓXIMOS PASOS

1. **Inmediato (Hoy):**
    - Comunicar BLOCKER al equipo
    - Asignar developers
    - Comenzar fix

2. **Esta semana:**
    - Completar BLOCKER
    - Incorporar CRITICAL al sprint
    - Planificar MAJOR

3. **Este mes:**
    - Resolver 50% de CRITICAL
    - Resolver 25% de MAJOR
    - Mejorar coverage a 40%

4. **Este trimestre:**
    - Reducir issues a < 50
    - Alcanzar coverage >= 80%
    - Resolver todas las vulnerabilities

---

## ENLACES ÚTILES

- Dashboard SonarCloud: https://sonarcloud.io/project/overview?id=puj-course_entregaya
- Issues en SonarCloud: https://sonarcloud.io/project/issues?id=puj-course_entregaya&issues=1
- Security Hotspots: https://sonarcloud.io/project/security_hotspots?id=puj-course_entregaya
- Coverage Report: https://sonarcloud.io/project/information?id=puj-course_entregaya

---

## RESPONSABLES

- **BLOCKER:** Arquitecto de Software (Daniel Leonardo Barreto)
- **CRITICAL:** Lead Developer + Sprint Team
- **MAJOR:** Sprint Team + QA (Andrés José Castrillo)
- **MINOR:** Junior Developers
- **INFO:** Equipo de Seguridad

---

Generado: 2026-05-11 05:39:12 UTC  
Última actualización: Automática (GitHub Actions)  
Frecuencia de actualización: Semanal (cada lunes)

---

**Nota:** Este reporte se genera automáticamente cada lunes. Para una actualización manual, ejecuta:
```bash
./scripts/sonarcloud/run-all.ps1
