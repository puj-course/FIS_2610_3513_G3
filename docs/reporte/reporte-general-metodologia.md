# Reporte General de Metodología Ágil

Fecha generación: 2026-05-13

---

# 1. Issues del Proyecto

| Métrica | Valor |
|----------|-------|
| Total Issues | 344 |
| Issues Cerrados | 332 |
| Issues Abiertos | 12 |

# 2. Historias de Usuario

| Métrica | Valor |
|----------|-------|
| Total Historias | 62 |
| Historias Cerradas | 58 |
| Historias Abiertas | 4 |

## Historias Detectadas

| Historia | Estado |
|-----------|--------|
| #572 HU-54: Evidenciar el despliegue completo y el funcionamiento de Telegram Como equipo, quiero registrar evidencia verificable del pipeline CD corriendo, de la imagen publicada en DockerHub y de mensajes reales enviados por Telegram... | open |
| #568 HU-53: Resolver los 105 open issues de SonarQube Como equipo, quiero reducir los open issues críticos detectados por SonarQube para mejorar la mantenibilidad y seguridad del sistema, asegurando que ningún issue de severidad.... | open |
| #564 HU-52: Aumentar la cobertura de pruebas al 80% en SonarQube Como equipo de desarrollo, quiero que las pruebas unitarias cubran al menos el 80% del código de producción verificado en SonarQube, para que el Quality Gate pase... | open |
| #560 HU-51: Corregir el pipeline CI y validar Quality Gate de SonarQube Como desarrollador, quiero que el archivo CI-pruebas.yml sea un workflow válido de GitHub Actions con sus triggers correctos, y que bloquee el build si el Quality Gate de SonarQube falla... | open |
| #555 HU-50: Implementar métrica de rendimiento en el código como desarrollador del equipo, quiero medir la latencia de los endpoints REST del sistema de forma automática, para identificar cuellos de botella... | closed |
| #503 HU-48: Ampliar pruebas unitarias a servicios y repositorios | closed |
| #497 R2: Pipeline CI con reporte integrado de todas las métricas y pruebas | closed |
| #492 HU-49: Como QA Lead del equipo,quiero implementar pruebas unitarias con JUnit 5 (org.junit.jupiter.api.*) para los controladores TrabajoController, TareaController y AuthController, instanciándolos directamente e inyectando sus dependencias... | closed |
| #487 R1: Configurar SonarQube para análisis de seguridad y duplicidad de código | closed |
| #482 HU-47:  Como equipo de desarrollo de EntregaYa, quiero configurar JaCoCo en el proyecto Maven para medir la cobertura de código de forma automatizada, para que el pipeline de CI rechace builds cuya cobertura sea inferior al 70%.... | closed |
| #429 HU-46: Como líder de un trabajo quiero descargar el historial de eventos del trabajo en formato PDF para contar con un registro auditable y portable de todo lo ocurrido en el proyecto | closed |
| #428 HU-45: Como miembro de un trabajo que ha dejado un comentario quiero poder editar o eliminar mis propios comentarios en una tarea para corregir errores o limpiar información desactualizada sin depender de un administrador | closed |
| #427 HU-44: Como usuario con muchas notificaciones pendientes quiero marcar todas mis notificaciones como leídas con un solo clic para limpiar rápidamente mi bandeja sin hacerlo una por una | closed |
| #426 HU-43: Como usuario registrado quiero guardar mi Telegram Chat ID en mi perfil de la aplicación para activar las notificaciones externas sin intervención adicional del sistema | closed |
| #425 HU-42: Como responsable de una tarea quiero recibir un mensaje en Telegram cuando mi tarea esté próxima a vencer para ser alertado sin depender de tener la aplicación abierta en ese momento | closed |
| #405 HU-41:  Como colaborador asignado a una tarea, quiero recibir una notificación interna automática 24 horas antes de que venza una tarea de la que soy responsable, para no perder entregas importantes sin necesidad de revisar constantemente el calendario. | closed |
| #401 HU-40:  Como editor o líder de un trabajo, quiero poder asignar etiquetas de texto libre a las tareas (ej. Frontend, Urgente, Revisar), para organizarlas según criterios propios del equipo más allá de los estados y dificultades predefinidos. | closed |
| #397 HU-39: Como líder de un trabajo, quiero exportar la lista de tareas de un trabajo a un archivo PDF, para compartir el estado del proyecto con docentes o interesados externos que no tienen acceso a la plataforma. | closed |
| #393 HU-38: Como colaborador de un trabajo, quiero ver un historial cronológico de los eventos ocurridos (creación de tareas, cambios de estado, ingreso/salida de miembros), para hacer seguimiento de la... | closed |
| #389 HU-37: Como usuario autenticado de EntregaYa, quiero ver un panel de estadísticas personales con métricas de mis tareas y trabajos, para entender mi rendimiento y productividad sin revisar cada trabajo individualmente. | closed |
| #367 HU-36:  Como miembro del equipo de desarrollo, quiero que los issues que sean cerrados se muevan automáticamente al estado “Done” en el tablero Kanban, para mantener actualizado el flujo de trabajo sin necesidad de intervención manual. | closed |
| #365 HU-35: Como desarrollador del proyecto quiero automatizar el merge de las ramas personales hacia la rama develop... | closed |
| #359 HU-34: Como desarrollador del proyecto, quiero implementar un pipeline de despliegue continuo (CD) para el backend... | closed |
| #353 HU-33:  Como desarrollador y analista de datos del proyecto EntregaYa, quiero elaborar el diagrama entidad-relación (ER) de la base de datos PostgreSQL con todas sus tablas... | closed |
| #349 HU-32:  Como desarrollador del equipo de EntregaYa, quiero elaborar el diagrama de clases UML del dominio que incluya las 7 entidades JPA con sus atributos... | closed |
| #345 HU-31: Como arquitecto técnico del proyecto EntregaYa, quiero elaborar y documentar el diagrama de despliegue UML del sistema, mostrando los nodos de infraestructura.... | closed |
| #340 HU-30:  Como desarrollador del equipo de EntregaYa, quiero elaborar el diagrama de componentes UML que muestre la estructura de capas del sistema (Controller, Service, Repository, Model/DTO y Config), sus interfaces provistas y requeridas.... | closed |
| #300 HU-29 - Como colaborador de un trabajo, quiero ver una etiqueta de urgencia dinámica en cada tarea (Normal / Próxima / Urgente / Vencida) según su fecha de cierre, para priorizar visualmente mis tareas sin necesidad de revisar fechas manualmente. | closed |
| #299 HU-28 - Como miembro activo de un trabajo, quiero recibir una notificación interna cuando un nuevo integrante se une al trabajo o un miembro lo abandona, para mantener al equipo al tanto de los cambios en su composición sin depender de comunicación externa. | closed |
| #298 HU-27 - Como colaborador asignado como responsable de una tarea, quiero recibir una notificación interna cuando dicha tarea sea marcada como completada o revertida, para mantenerme informado del avance del trabajo sin necesidad de revisar constantemente la vista de detalle. | closed |
| #297 HU-26 - Como colaborador con rol EDITOR o LIDER dentro de un trabajo, quiero clonar una tarea existente dentro del mismo trabajo, para reutilizar su configuración (nombre, descripción, dificultad, responsables)... | closed |
| #296 HU-25 - Como líder de un trabajo, quiero duplicar un trabajo existente como plantilla, para crear rápidamente nuevos proyectos con la misma estructura sin comenzar desde cero | closed |
| #277 HU-24 - Como usuario autenticado, quiero que el dashboard siga mostrando mis trabajos activos, progreso, proximos a vencer, tareas vencidas e invitaciones pendientes exactamente igual que antes, mientras que como desarrollador quiero que AuthController.... | closed |
| #260 HU-24 - Como usuario autenticado, quiero que el dashboard siga mostrando mis trabajos activos, progreso, proximos a vencer, tareas vencidas e invitaciones pendientes exactamente igual que antes.... | closed |
| #259 HU-23 - Como desarrollador, quiero una clase DashboardFacade que encapsule los calculos de trabajos activos, progresos, proximos a vencer, tareas vencidas e invitaciones pendientes... | closed |
| #258 HU-22 - Como lider de un trabajo, quiero que al crear o editar una tarea el sistema use TareaBuilder para construir el objeto con validaciones automaticas... | closed |
| #257 HU-21 - Como desarrollador del equipo, quiero una clase TareaBuilder que permita construir objetos Tarea de forma encadenada... | closed |
| #256 HU-20 - Como sistema, quiero que las reglas de acceso por rol (solo LIDER, LIDER o EDITOR) esten encapsuladas en clases intercambiables PermisoStrategy,... | closed |
| #240 HU-19 - Como líder de un trabajo, quiero visualizar las tareas del trabajo en un calendario mensual, para planificar mejor los plazos de entrega e identificar de un vistazo semanas con alta carga de trabajo. | closed |
| #236 HU-18 - Como colaborador de un trabajo, quiero poder dejar comentarios en las tareas, para comunicar avances, dudas o aclaraciones directamente en el contexto de cada tarea sin necesidad de canales externos. | closed |
| #232 HU-17 - Como colaborador de un trabajo, quiero poder buscar tareas por nombre y filtrarlas por estado o dificultad, para encontrar rápidamente las tareas relevantes en proyectos con muchas tareas. | closed |
| #227 HU-16 - Como usuario registrado, quiero tener un correo electrónico asociado a mi cuenta, para poder ser contactado por mis colaboradores y recibir información relevante de mis trabajos. | closed |
| #222 HU-15 - Como líder de un trabajo, quiero poder editar el nombre, descripción y fechas de un trabajo existente, para mantener la información del proyecto actualizada ante cambios de alcance o cronograma. | closed |
| #203 Diagrama de clases para HU-05, HU-06, HU-08 | closed |
| #202 Diagrama de despliegue para HU-05, HU-06, HU-08 | closed |
| #201 Diagrama de Análisis (ECB) para HU-05, HU-06, HU-08 | closed |
| #200 Diagrama de componentes para HU-05, HU-06, HU-08 | closed |
| #175 HU-14 - Como líder de un trabajo, quiero poder expulsar a un colaborador del trabajo, para mantener el equipo actualizado cuando alguien ya no deba tener acceso al proyecto. | closed |
| #174 HU-13 - Como editor o líder de un trabajo, quiero poder editar el nombre, descripción, dificultad, fechas y responsables de una tarea ya creada, para corregir errores o ajustar el alcance sin tener que borrar la tarea y crearla de nuevo. | closed |
| #172 HU-12 - Como líder o editor de un trabajo, quiero ver la lista de invitaciones que he enviado junto a su estado actual y poder cancelar las pendientes, para hacer seguimiento a quiénes se han unido o rechazado mi invitación. | closed |
| #171 HU-11 - Como colaborador de un trabajo, quiero ver indicadores visuales cuando una tarea esté vencida o a menos de 24 horas de su fecha límite, para poder priorizar mi trabajo y no perder entregas importantes. | closed |
| #170 HU-10 - Como usuario registrado, quiero poder actualizar mi nombre de usuario y cambiar mi contraseña desde una página de perfil, para mantener mis credenciales seguras y actualizadas sin necesidad de contactar a nadie | closed |
| #136 HU-9 – Visualizar roles en la interfaz | closed |
| #135 HU-8 – Modificar rol de un miembro | closed |
| #134 HU-7 – Consultar roles del grupo | closed |
| #133 HU-6 – Asignar rol a un miembro del grupo | closed |
| #132 HU-5 – Crear estructura de roles en el grupo | closed |
| #72 Historia de Usuario NF #2 | closed |
| #71 Historia de Usuario NF #1 | closed |
| #70 Historia de Usuario #11 | closed |
| #69 Historia de Usuario #10 | closed |
| #68 Historia de Usuario #4 | closed |

# 3. Pull Requests

| Métrica | Valor |
|----------|-------|
| Total PRs | 284 |
| PRs Mergeados | 247 |
| PRs Abiertos | 0 |
| PRs Cerrados sin Merge | 37 |

# 4. Participación por Commits

| Usuario | Commits |
|----------|----------|
| @barretordaniel | 218 |
| @Silverweta20 | 155 |
| @andresj-castrillo | 110 |
| @echaustre12 | 73 |
| @Juanjoyt2 | 42 |
| @actions-user | 10 |
| @JuanParias29 | 2 |

# 5. Participación por Issues

| Usuario | Asignados | Cerrados |
|----------|------------|-----------|
| @andresj-castrillo | 133 | 131 |
| @echaustre12 | 125 | 125 |
| @barretordaniel | 120 | 117 |
| @Silverweta20 | 119 | 115 |
| @Juanjoyt2 | 114 | 111 |

# 6. Milestones

| Milestone | Estado | Issues Abiertos | Issues Cerrados |
|------------|---------|------------------|------------------|
| Versión 1.0 - Entregar Documentación | open | 0 | 14 |
| Modulo Inicio de Sesión, dockerizar y conectar base de datos, DDL | open | 0 | 31 |
| Creación de roles en Trabajos | open | 0 | 57 |
| Implementar patrones GoF | open | 0 | 55 |
| Sprint: Edición, comunicación y visualización de trabajos y tareas | open | 0 | 26 |
| Segunda Entrega | open | 0 | 27 |
| Calidad y métricas | open | 12 | 73 |

# 7. Estructura de Ramas

- Total ramas: 9
- Ramas principales: 2
- Feature branches: 2
- Ramas personales: 5

## Ramas Principales

| Rama |
|------|
| develop |
| main |

## Feature Branches

| Rama |
|------|
| feature/docker-deploy |
| feature/pipelines |

