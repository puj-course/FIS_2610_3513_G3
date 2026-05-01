# Requisitos – Historias de Usuario
## Plataforma EntregaYa

### HU-01
Como estudiante universitario quiero registrarme e iniciar sesión de manera sencilla para acceder a la plataforma sin necesidad de conocimientos técnicos previos.

### HU-02
Como estudiante quiero crear un trabajo académico dentro de la plataforma para organizar mis entregas de manera estructurada.

### HU-03
Como estudiante quiero invitar a mis compañeros a un trabajo grupal para colaborar en una misma entrega de forma sencilla.

### HU-04
Como estudiante quiero visualizar el progreso de mi trabajo para saber qué tareas están pendientes o finalizadas.

### HU-05
Como sistema quiero tener una estructura de roles dentro de los grupos para poder asignar responsabilidades a los estudiantes.

### HU-06
Como estudiante quiero asignar un rol a un miembro del grupo para definir su responsabilidad dentro del trabajo.

### HU-07
Como estudiante quiero consultar los roles de los miembros del grupo para entender las responsabilidades de cada integrante

### HU-08
Como estudiante quiero modificar el rol de un miembro del grupo para ajustar las responsabilidades cuando sea necesario.

### HU-09
Como estudiante quiero visualizar los roles de cada integrante del grupo en la interfaz para identificar sus responsabilidades.

### HU-10
Como usuario registrado, quiero poder actualizar mi nombre de usuario y cambiar mi contraseña desde una página de perfil, para mantener mis credenciales seguras y actualizadas sin necesidad de contactar a nadie.

### HU-11
Como colaborador de un trabajo, quiero ver indicadores visuales cuando una tarea esté vencida o a menos de 24 horas de su fecha límite, para poder priorizar mi trabajo y no perder entregas importantes.

### HU-12 
Como líder o editor de un trabajo, quiero ver la lista de invitaciones que he enviado junto a su estado actual y poder cancelar las pendientes, para hacer seguimiento a quiénes se han unido o rechazado mi invitación.

### HU-13 
Como editor o líder de un trabajo, quiero poder editar el nombre, descripción, dificultad, fechas y responsables de una tarea ya creada, para corregir errores o ajustar el alcance sin tener que borrar la tarea y crearla de nuevo.

### HU-14 
Como sistema, quiero tener una estructura de roles dentro de los grupos, para poder asignar responsabilidades a los estudiantes.

### HU-15
Como líder de un trabajo, quiero poder editar el nombre, descripción y fechas de un trabajo existente, para mantener la información del proyecto actualizada ante cambios de alcance o cronograma.

### HU-16
Como usuario registrado, quiero tener un correo electrónico asociado a mi cuenta, para poder ser contactado por mis colaboradores y recibir información relevante de mis trabajos.

### HU-17
Como colaborador de un trabajo, quiero poder buscar tareas por nombre y filtrarlas por estado o dificultad, para encontrar rápidamente las tareas relevantes en proyectos con muchas tareas.

### HU-18
Como colaborador de un trabajo, quiero poder dejar comentarios en las tareas, para comunicar avances, dudas o aclaraciones directamente en el contexto de cada tarea sin necesidad de canales externos.

### HU-19
Como líder de un trabajo, quiero visualizar las tareas del trabajo en un calendario mensual, para planificar mejor los plazos de entrega e identificar de un vistazo semanas con alta carga de trabajo.

### HU-20
Como sistema, quiero que las reglas de acceso por rol (solo LIDER, LIDER o EDITOR) esten encapsuladas en clases intercambiables PermisoStrategy, para eliminar los bloques de logica duplicados que hoy existen en CustomTrabajoDetailsService, CustomInvitacionDetailsService y TareaController.

### HU-21
Como desarrollador del equipo, quiero una clase TareaBuilder que permita construir objetos Tarea de forma encadenada y que valide que el nombre no este vacio y que la fecha final no sea anterior a la inicial antes de crear el objeto, para prevenir tareas con datos inconsistentes desde el origen.

### HU-22
Como lider de un trabajo, quiero que al crear o editar una tarea el sistema use TareaBuilder para construir el objeto con validaciones automaticas, para que los errores de datos se detecten antes de llegar a la base de datos y el mensaje de error llegue al formulario de forma clara.

### HU-23
Como desarrollador, quiero una clase DashboardFacade que encapsule los calculos de trabajos activos, progresos, proximos a vencer, tareas vencidas e invitaciones pendientes, para sacar esa logica del controlador y exponerla a traves de un unico punto de acceso.

### HU-24
Como usuario autenticado, quiero que el dashboard siga mostrando mis trabajos activos, progreso, proximos a vencer, tareas vencidas e invitaciones pendientes exactamente igual que antes, mientras que como desarrollador quiero que AuthController solo tenga una dependencia (DashboardFacade) y el metodo dashboard() no supere 5 lineas de codigo.

### HU-25
Como líder de un trabajo, quiero duplicar un trabajo existente como plantilla, para crear rápidamente nuevos proyectos con la misma estructura sin comenzar desde cero.

### HU-26
Como colaborador con rol EDITOR o LIDER dentro de un trabajo, quiero clonar una tarea existente dentro del mismo trabajo, para reutilizar su configuración (nombre, descripción, dificultad, responsables) en tareas repetitivas sin reescribir manualmente cada campo.

### HU-27
Como colaborador asignado como responsable de una tarea, quiero recibir una notificación interna cuando dicha tarea sea marcada como completada o revertida, para mantenerme informado del avance del trabajo sin necesidad de revisar constantemente la vista de detalle.

### HU-28
Como miembro activo de un trabajo, quiero recibir una notificación interna cuando un nuevo integrante se une al trabajo o un miembro lo abandona, para mantener al equipo al tanto de los cambios en su composición sin depender de comunicación externa.

### HU-29
Como colaborador de un trabajo, quiero ver una etiqueta de urgencia dinámica en cada tarea (Normal / Próxima / Urgente / Vencida) según su fecha de cierre, para priorizar visualmente mis tareas sin necesidad de revisar fechas manualmente.

### HU-30
Como desarrollador del equipo de EntregaYa, quiero elaborar el diagrama de componentes UML que muestre la estructura de capas del sistema (Controller, Service, Repository, Model/DTO y Config), sus interfaces provistas y requeridas, las dependencias entre capas y el rol transversal de Spring Security, en notación UML 2.x, para que el equipo disponga de una referencia visual clara de la arquitectura en capas que facilite la detección de dependencias circulares, el análisis del impacto de cambios y el onboarding de nuevos integrantes al proyecto.

### HU-31
Como arquitecto técnico del proyecto EntregaYa, quiero elaborar y documentar el diagrama de despliegue UML del sistema, mostrando los nodos de infraestructura (servidor de aplicación Spring Boot, servidor PostgreSQL, navegador del cliente), los artefactos desplegados (.jar), los protocolos de comunicación (HTTP/8081, JDBC/5432) y las dependencias entre componentes físicos, para que el equipo de desarrollo y operaciones disponga de una referencia visual clara de la arquitectura de despliegue, facilitando la configuración de entornos, la detección de cuellos de botella y las decisiones de escalabilidad futura.

### HU-32
Como desarrollador del equipo de EntregaYa, quiero elaborar el diagrama de clases UML del dominio que incluya las 7 entidades JPA con sus atributos, relaciones y cardinalidades, y los 6 patrones de diseño GoF implementados (Builder, Prototype, Decorator, Facade, Observer y Strategy) con sus interfaces y clases concretas, en notación UML 2.x, para que el equipo tenga una referencia visual precisa de la estructura estática del dominio y los patrones aplicados, facilitando el onboarding de nuevos miembros, la toma de decisiones de refactoring y la documentación oficial del proyecto.

### HU-33
Como desarrollador y analista de datos del proyecto EntregaYa, quiero elaborar el diagrama entidad-relación (ER) de la base de datos PostgreSQL con todas sus tablas, columnas, tipos, claves primarias, foráneas e índices, y además crear el diagrama de comunicación EBC para los flujos más críticos del sistema (cambio de estado de tarea con disparo del Observer de Notificación, y flujo de autenticación con Spring Security), para que el equipo disponga de documentación precisa de la capa de persistencia para optimizar consultas, planificar migraciones de esquema, y que los flujos de comunicación críticos estén documentados para facilitar el diagnóstico de errores y el diseño de pruebas de integración.

### HU-34
Como desarrollador del proyecto, quiero implementar un pipeline de despliegue continuo (CD) para el backend en Spring Boot, para automatizar la publicación del artefacto de la aplicación en GitHub Releases cada vez que se integren cambios a la rama principal.

### HU-35
Como desarrollador del proyecto quiero automatizar el merge de las ramas personales hacia la rama develop, para integrar cambios de forma rápida y segura sin intervención manual, siempre que no existan conflictos y el código haya sido validado correctamente.

### HU-36
Como miembro del equipo de desarrollo, quiero que los issues que sean cerrados se muevan automáticamente al estado “Done” en el tablero Kanban, para mantener actualizado el flujo de trabajo sin necesidad de intervención manual.