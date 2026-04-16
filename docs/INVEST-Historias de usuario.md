# INVEST – Historias de Usuario

El formato INVEST se usa para evaluar si una historia de usuario está bien definida.
INVEST significa:

- I – Independent (Independiente)
- N – Negotiable (Negociable)
- V – Valuable (Valiosa)
- E – Estimable (Estimable)
- S – Small (Pequeña)
- T – Testable (Testeable)

### HU-01 – Registro e inicio de sesión
- **I:** Independiente de otras funcionalidades.
- **N:** Puede ajustarse el método de autenticación o registro.
- **V:** Permite al estudiante acceder a la plataforma.
- **E:** Fácil de estimar por ser una funcionalidad común.
- **S:** Historia pequeña y clara.
- **T:** Se puede probar registrando un usuario e iniciando sesión.

---

### HU-02 – Crear trabajo académico
- **I:** Independiente del resto de funciones.
- **N:** Se puede negociar la estructura del trabajo.
- **V:** Permite organizar entregas académicas.
- **E:** Estimable por su funcionalidad definida.
- **S:** Tamaño adecuado.
- **T:** Se prueba creando un trabajo correctamente.

---

### HU-03 – Invitar compañeros
- **I:** Depende únicamente de que exista un trabajo.
- **N:** Puede modificarse el método de invitación.
- **V:** Facilita el trabajo colaborativo.
- **E:** Estimable por su alcance claro.
- **S:** Historia manejable.
- **T:** Se prueba enviando invitaciones a usuarios.

---

### HU-04 – Visualizar progreso del trabajo
- **I:** Independiente de otras funcionalidades.
- **N:** Puede cambiar la forma de mostrar el progreso.
- **V:** Permite conocer el estado del trabajo.
- **E:** Fácil de estimar.
- **S:** Historia pequeña.
- **T:** Se prueba verificando el estado de las tareas.

---

### HU-05 – Crear estructura de roles en el grupo
- **I:** Puede desarrollarse sin depender de otras funcionalidades del sistema.
- **N:** Los roles pueden ajustarse según las necesidades del grupo o del proyecto.
- **V:** Permite organizar responsabilidades dentro de los grupos.
- **E:** Estimable porque implica crear modelo, tabla y roles iniciales.
- **S:** Tamaño adecuado para un sprint.
- **T:** Se prueba verificando que los roles se crean y se guardan en la base de datos.

---

### HU-06 – Asignar rol a un miembro del grupo
- **I:** Depende de que existan roles previamente definidos.
- **N:** La forma de asignar o validar roles puede ajustarse.
- **V:** Define responsabilidades claras para cada integrante.
- **E:** Estimable porque requiere endpoint, validación y guardado.
- **S:** Tamaño adecuado para una historia individual.
- **T:** Se prueba asignando un rol a un miembro y verificando que se guarde.

---

### HU-07 – Consultar roles del grupo
- **I:** Depende de que los roles estén asignados a los miembros.
- **N:** La forma de presentar la información puede modificarse.
- **V:** Permite conocer las responsabilidades de cada integrante.
- **E:** Estimable porque solo implica consulta y endpoint.
- **S:** Historia pequeña de consulta de datos.
- **T:** Se prueba verificando que el sistema retorne miembros con su rol.

---

### HU-08 – Modificar rol de un miembro
- **I:** Depende de que los miembros ya tengan un rol asignado.
- **N:** Las reglas para cambiar roles pueden ajustarse.
- **V:** Permite reorganizar responsabilidades dentro del grupo.
- **E:** Estimable porque implica actualizar un registro existente.
- **S:** Tamaño adecuado para una funcionalidad de actualización.
- **T:** Se prueba cambiando el rol de un miembro y verificando el cambio.

---

### HU-09 – Visualizar roles en la interfaz
- **I:** Depende de la consulta de roles desde el backend.
- **N:** El diseño de la interfaz puede modificarse.
- **V:** Facilita identificar las responsabilidades de cada integrante.
- **E:** Estimable porque consiste en consumir un endpoint y mostrar datos.
- **S:** Historia pequeña de frontend.
- **T:** Se prueba verificando que la interfaz muestre correctamente los roles de cada miembro.

---

### HU-10 – Edición de perfil y cambio de contraseña
- **I:** Independiente, usa entidades ya existentes (User, PasswordConfig).
- **N:** El diseño de la vista puede simplificarse; lo esencial es la funcionalidad.
- **V:** Permite a los usuarios mantener sus cuentas seguras sin intervención externa.
- **E:** Estimable: 1 vista + 1 endpoint + validaciones conocidas.
- **S:** Alcance delimitado a username y contraseña, sin datos adicionales.
- **T:** Se prueba cambiando el username, intentando uno duplicado y cambiando la contraseña.

---

### HU-11 – Alertas visuales de tareas vencidas y próximas a vencer
- **I:** Independiente, usa campos ya existentes (fechaFinal, completada) sin cambiar el modelo.
- **N:** El umbral de 24 horas puede ajustarse si el equipo lo considera necesario.
- **V:** Reduce el riesgo de que los colaboradores pierdan fechas de entrega importantes.
- **E:** Estimable por la lógica de fechas sencilla y los cambios acotados en las vistas.
- **S:** Solo afecta el controlador y las plantillas detalle.html y dashboard.html.
- **T:** Se prueba creando tareas con fechas pasadas y verificando que los badges aparecen.

---

### HU-12 – Historial de invitaciones enviadas con opción de cancelar
- **I:** Independiente, la entidad Invitacion y su enum Estado ya están completos.
- **N:** La vista puede integrarse en detalle.html o ser una sección separada.
- **V:** Evita invitaciones duplicadas y da visibilidad al líder sobre el estado del equipo.
- **E:** Estimable: 1 endpoint nuevo y una sección adicional en la vista existente.
- **S:** Alcance claro: listar y cancelar invitaciones PENDIENTE, sin reenvío.
- **T:** Se prueba enviando, cancelando e intentando duplicar una invitación.

--- 

### HU-13 – Edición de tareas existentes
- **I:** Independiente, la entidad Tarea y sus relaciones ya están completas.
- **N:** El formulario puede reutilizar CrearTarea.html o ser una vista nueva.
- **V:** Evita perder el historial de asignaciones que ocurre al borrar y recrear una tarea.
- **E:** Estimable: 1 vista con pre-carga + 2 endpoints + método update en el servicio.
- **S:** Limitado a los campos de Tarea, sin cambiar el trabajo al que pertenece.
- **T:** Se prueba editando campos y verificando que los cambios persisten correctamente.

---

### HU-14 – Estructura de roles dentro de los grupos
- **I:** Independiente, el enum Rol y ColaboradorTrabajo ya existen en el modelo.
- **N:** Los nombres de los roles y sus permisos pueden ajustarse antes de implementar.
- **V:** Define claramente qué puede hacer cada estudiante dentro de un grupo.
- **E:** Estimable por tener la base de datos ya estructurada con la columna de rol.
- **S:** Acotado a los tres roles existentes sin permisos granulares adicionales.
- **T:** Se prueba asignando roles y verificando que cada uno accede solo a lo permitido.

---

### HU-15 - Editar informacion de un trabajo
- **I:** Independiente, solo afecta TrabajoController y servicio, sin dependencias cruzadas nuevas.
- **N:** Formulario reutiliza el diseño de formulario.html existente.
- **V:** El líder puede corregir errores sin recrear el trabajo desde cero.
- **E:** 1–2 días con un desarrollador. No requiere nueva infraestructura.
- **S:** No bloquea ni depende de otras HU del sprint.
- **T:** Se verifica con prueba manual de edición y validación de rol.

---

### HU-16 - Registar correo 
- **I:** Cambio autocontenido en User, register.html y perfil.html.
- **N:** Extiende funcionalidad de perfil ya implementada.
- **V:** Los colaboradores pueden contactarse fuera de la app.
- **E:** Migración simple de BD + 2 vistas. 1–2 días.
- **S:** No bloquea ni es bloqueada por otras historias.
- **T:** Se verifica con registro, login y edición de email en perfil.

---

### HU-17 - Buscar o filtrar tareas por estado o nombre 
- **I:** Todo el filtrado es en JS del cliente sobre detalle.html. Sin cambios al backend.
- **N:** Mejora directa de la vista más usada de la app.
- **V:** En trabajos con 20+ tareas, la usabilidad mejora notablemente.
- **E:** Solo JS en el cliente. Implementable en 1 día.
- **S:** Independiente de otras HU. No bloquea nada.
- **T:** Prueba manual con varios filtros combinados y caso sin resultados.

---

### HU-18 - Dejar comentarios en una tarea 
- **I:** Todo el filtrado es en JS del cliente sobre detalle.html. Sin cambios al backend.
- **N:** Mejora directa de la vista más usada de la app.
- **V:** En trabajos con 20+ tareas, la usabilidad mejora notablemente.
- **E:** Solo JS en el cliente. Implementable en 1 día.
- **S:** Independiente de otras HU. No bloquea nada.
- **T:** Prueba manual con varios filtros combinados y caso sin resultados.

---

### HU-19 - Calendario
- **I:** Nuevo endpoint JSON + nueva vista calendario.html. Sin cambios a modelos.
- **N:** Complementa la vista de detalle con una perspectiva temporal nueva.
- **V:** Permite al líder detectar cuellos de botella de fechas visualmente.
- **E:** FullCalendar vía CDN. La mayor parte del esfuerzo es la integración JS.
- **S:** No bloquea otras HU. Depende de Tarea (ya implementada).
- **T:** Prueba con tareas con distintas fechas, verificar eventos y estilo de completadas.

---

### HU-20 - centralizar verificacion de permisos con Strategy
- **I:** No depende de ninguna otra HU; los modelos y repositorios que necesita ya existen.
- **N:** El nombre del metodo tienePermiso() y los nombres de las clases pueden ajustarse si el equipo lo decide.
- **V:** Elimina 4 bloques de logica de roles duplicados distribuidos en 3 archivos diferentes.
- **E:** Alcance claro: 3 clases nuevas + refactor de 3 archivos existentes sin cambiar comportamiento observable.
- **S:** Los cambios no alteran funcionalidad visible para el usuario; solo reorganizan la logica interna.
- **T:** Un COLABORADOR que intenta editar tarea o cancelar invitacion sigue recibiendo error; LIDER/EDITOR siguen con acceso.

---

### HU-21 - Implementar Builder para tareas
- **I:** No depende de ninguna otra HU; Tarea ya existe y sus campos estan definidos.
- **N:** Las validaciones concretas del metodo build() pueden ajustarse segun criterio del equipo.
- **V:** Hoy crearTarea() recibe un objeto Tarea del formulario sin ninguna validacion de construccion; nombre vacio o fechas cruzadas no se detectan.
- **E:** Alcance muy preciso: una clase nueva con metodos fluidos y dos validaciones en build().
- **S:** Solo toca TareaBuilder.java; no modifica ningun archivo existente en esta historia.
- **T:** Llamar a build() con nombre nulo o vacio debe lanzar IllegalStateException; con fechas cruzadas.

---

### HU-22 - Integrar el Builder de tarea en servicio y controlador 
- **I:** Depende de HU-21 (TareaBuilder debe existir); es independiente de HU-20 y HU-23.
- **N:** El equipo decide si editarTarea() tambien usa el Builder o solo crearTarea().
- **V:** Los usuarios ya no podran guardar tareas sin nombre o con fechas inconsistentes; el error aparece en el formulario.
- **E:** Alcance definido: modificar 2 metodos en el servicio y gestionar la excepcion en el controlador.
- **S:** Solo toca CustomTareaDetailsService y TareaController sin cambiar la logica de negocio.
- **T:** El formulario de creacion rechaza datos invalidos y muestra el mensaje de error antes de redirigir.

---

### HU-23 - Crear Dashboard facade y Dashboard DTO
- **I:** No depende de HU-20, HU-21 ni HU-22; los servicios que consume ya existen.
- **N:** Los campos del DashboardDTO pueden ajustarse si la plantilla Thymeleaf necesita otros datos.
- **V:** AuthController.dashboard() tiene hoy 40 lineas de logica de negocio (streams, filtros, calculos de fechas) que no deberian estar en el controlador.
- **E:** 2 clases nuevas con responsabilidades claras; sin modificar ningun archivo existente en esta historia.
- **S:** Solo crea DashboardFacade y DashboardDTO; el controlador se refactoriza en HU-24.
- **T:** getDashboardData() retorna un DashboardDTO con todos los campos que el dashboard necesita.

---

### HU-24 - Refactorizar DashboardFacade y AuthController 
- **I:** Depende de HU-23 (DashboardFacade debe existir); es independiente de HU-20, HU-21 y HU-22.
- **N:** El equipo puede decidir si el DTO se pasa directamente al modelo o se desglosa en atributos individuales.
- **V:** El dashboard sigue mostrando los mismos datos; AuthController pasa de inyectar 4 servicios a inyectar 1 Facade.
- **E:** Refactor de 1 metodo en 1 controlador + ajuste de referencias en la plantilla Thymeleaf.
- **S:** No cambia comportamiento observable para el usuario; solo simplifica el controlador.
- **T:** El dashboard muestra exactamente los mismos datos antes y despues del refactor (verificado con sesion real).

---

### HU-25 - Duplicar trabajo como plantilla 
- **I:** No depende de otras historias activas; reutiliza modelos y servicios ya existentes.
- **N:** El alcance de qué campos se clonan (ej. fecha de entrega sí/no) puede ajustarse en el sprint planning.
- **V:** Reduce el tiempo de creación de proyectos recurrentes; impacto directo en productividad del líder.
- **E:** 10 h: 4h interfaz+modelo, 4h servicio, 2h endpoint+prueba manual.
- **S:** Alcance único: clonar un Trabajo. Sin UI compleja ni integraciones externas.
- **T:** Se puede verificar manualmente y con aserciones sobre el objeto retornado por el endpoint.

---

### HU-26 - Clonar tarea dentor de un trabajo 
- **I:** Independiente de HU-1; comparte la misma interfaz conceptual Prototype pero en modelo Tarea.
- **N:** Se puede negociar si las fechas se copian o quedan en null en la primera versión.
- **V:** Ahorra tiempo a editores en trabajos con tareas recurrentes (ej. sprints, checklist semanal).
- **E:** 10 h: 2h interfaz, 4h implementación en Tarea+Servicio, 2h endpoint, 2h UI.
- **S:** Alcance acotado: una sola entidad (Tarea), un endpoint, un botón en UI.
- **T:** Verificable comparando la tarea original y la clonada campo a campo mediante el endpoint.

---

### HU-27 - Notificar al cambiar un estado de una tarea 
- **I:** No bloquea ni es bloqueada por HU-1 o HU-2. Requiere solo la lógica existente de Tarea y User.
- **N:** El canal (in-app, email, push) es negociable; en este sprint solo se implementa in-app.
- **V:** Reduce la carga cognitiva de los colaboradores que deben monitorear manualmente el estado de sus tareas.
- **E:** 10 h: 2h modelo Notificacion, 4h Observer+Servicio, 4h UI badge+endpoint.
- **S:** Un solo tipo de evento (cambio de estado). Email y push quedan para futuros sprints.
- **T:** Se puede verificar manualmente completando una tarea y comprobando la notificación en el dashboard del responsable.

---

### HU-28 - Alertar al equipo por cambio en el equipo de trabajo 
- **I:** Depende conceptualmente de HU-3 (infraestructura Notificacion), pero puede desarrollarse en paralelo si se define el contrato de Notificacion primero.
- **N:** El texto exacto del mensaje y la diferenciación visual son negociables con el equipo de diseño.
- **V:** Mejora la coordinación del equipo y reduce la desinformación sobre cambios de membresía.
- **E:** 10 h: 2h reutilizar infraestructura, 4h observer+servicio, 2h UI, 2h pruebas manuales.
- **S:** Solo dos eventos (INGRESO, SALIDA); no cubre cambios de rol en este sprint.
- **T:** Flujo manual verificable: aceptar invitación, revisar notificaciones de los otros miembros.

---

### HU-29 - Etiqueta dinamica de urgencia en tareas 
- **I:** Completamente independiente de HU-1 a HU-4. Solo requiere la entidad Tarea y las plantillas existentes.
- **N:** Los umbrales de días (7 y 3) y los colores de badge son negociables con el equipo.
- **V:** Impacto visual inmediato: los colaboradores pueden priorizar sin abrir cada tarea. Reduce tareas vencidas.
- **E:** 10 h: 4h jerarquía de decoradores+factory, 4h integración en servicio+DTO, 2h UI plantillas.
- **S:** Solo afecta la capa de presentación de urgencia. No modifica la entidad Tarea ni la BD.
- **T:** Crear 5 tareas con fechas distintas y verificar que cada una muestra la etiqueta correcta en la UI.

---

### HU-30 - Diagrama de componentes 
- **I:** El diagrama de componentes es independiente del diagrama de clases y de otras HU; puede elaborarse en paralelo sin bloquear ni ser bloqueado.
- **N:** El nivel de granularidad (mostrar componentes individuales como DashboardFacade o agruparlos por capa) es negociable con el equipo según el detalle requerido.
- **V:** Permite identificar visualmente violaciones de la arquitectura en capas, facilita decisiones de refactoring y sirve de contrato visual de las dependencias permitidas.
- **E:** Estimado en 10 horas: identificación de componentes y capas M (4h), modelado de dependencias e interfaces M (4h), revisión y documentación S (2h).
- **S:** El alcance se limita a los componentes existentes en el código fuente actual; no incluye diseño de nuevos módulos ni funcionalidades futuras.
- **T:** El diagrama es aceptado cuando un revisor puede trazar cada dependencia del diagrama hasta el código fuente (imports y constructores) sin encontrar discrepancias.

---

### HU-31 - Diagrama de despliegue 
- **I:** La creación del diagrama de despliegue es independiente de los demás diagramas. No requiere que otros diagramas estén finalizados.
- **N:** El nivel de detalle (p. ej. incluir o no un proxy inverso o balanceador) es negociable con el equipo según el alcance del sprint.
- **V:** Permite al equipo de operaciones configurar entornos correctamente y sirve de base para decisiones de infraestructura como dockerización o migración a la nube.
- **E:** Estimado en 10 horas: modelado de nodos M (4h), documentación de artefactos y protocolos M (4h), revisión S (2h).
- **S:** El alcance se limita al diagrama de despliegue; la arquitectura de clases o componentes se aborda en HU-04.
- **T:** El diagrama es aceptado cuando refleja fielmente la configuración real de application.properties y el equipo lo valida en revisión.

---

### HU-32 - Diagrama de clases 
- **I:** El diagrama de clases es independiente del diagrama de componentes (HU-02) y de otras HU; puede elaborarse en paralelo sin bloquear a nadie.
- **N:** El nivel de detalle de atributos y métodos (incluir o no getters/setters, visibilidad) es negociable con el equipo según el propósito de la documentación.
- **V:** Centraliza el conocimiento del dominio y los patrones en un único artefacto visual, reduciendo el tiempo de comprensión del código para nuevos integrantes y revisores.
- **E:** Estimado en 10 horas: entidades JPA y relaciones M (4h), patrones de diseño con interfaces M (4h), revisión y documentación S (2h).
- **S:** El alcance está acotado a las clases existentes en el código fuente actual; no incluye diseño de nuevas entidades ni funcionalidades futuras.
- **T:** El diagrama es aceptado cuando un revisor independiente puede trazar cada clase, relación y patrón del diagrama hasta el código fuente sin encontrar discrepancias.

---

### HU-33 - Diagramas ER y EBC
- **I:** El diagrama ER es independiente del diagrama de comunicación y de otras HU; puede elaborarse en paralelo.
- **N:** El alcance de los flujos EBC (número de escenarios a documentar) puede ajustarse con el equipo según prioridad del sprint.
- **V:** El diagrama ER es la base para optimización de queries, índices y migraciones. El EBC reduce el tiempo de diagnóstico en errores de comunicación entre componentes.
- **E:** Estimado en 10 horas: diagrama ER M (4h), EBC flujo Observer M (4h), EBC flujo autenticación S (2h).
- **S:** El alcance está limitado al esquema actual de la BD y a dos flujos críticos del sistema; no incluye diseño de nuevas tablas.
- **T:** El diagrama ER es aceptado cuando coincide con el DDL generado por Hibernate (spring.jpa.show-sql=true). El EBC es aceptado cuando un revisor puede trazar el flujo completo en el código.

---

### HU-34 - Pipeline CD
- **I:** No depende de otras historias activas, solo del repositorio y la configuración del entorno de GitHub Actions.
- **N:** El mecanismo de publicación (GitHub Releases) puede reemplazarse por otro destino sin afectar la lógica principal del pipeline.
- **V:** Permite automatizar la generación y publicación del artefacto del sistema, reduciendo tiempo y errores manuales en cada entrega.
- **E:** 8 h: 2h estructura del workflow, 3h configuración del pipeline y pruebas de compilación, 2h pruebas de release automático, 1h revisión de equipo.
- **S:** Alcance limitado al backend, sin incluir frontend ni base de datos. No requiere infraestructura externa.
- **T:** Se puede verificar realizando un merge a main y comprobando que se crea automáticamente un nuevo Release en la pestaña Releases del repositorio con el .jar adjunto.

---

### HU-35 - Automatiozar merge de ramas a develop 
- **I:** No depende de otras historias activas; reutiliza la infraestructura de CI ya implementada en el repositorio.
- **N:** Se puede ajustar si el merge requiere condiciones adicionales como aprobación manual o validaciones más estrictas.
- **V:** Reduce el tiempo de integración de cambios y evita errores humanos en merges manuales.
- **E:** 1h configuración del workflow, 1h pruebas y ajustes.
- **S:** Se limita a la automatización del merge hacia la rama develop sin modificar otras ramas o procesos.
- **T:** Se puede verificar creando un pull request desde una rama personal hacia develop, agregando el label correspondiente y comprobando que el merge se realiza automáticamente sin conflictos.

---

### HU-36 - Automatizar a done 
- **I:** No depende de otras historias activas; se basa en la configuración del tablero del proyecto.
- **N:** Se puede ajustar el evento disparador o el estado destino según cambios en el flujo de trabajo.
- **V:** Mantiene actualizado el tablero automáticamente, reduciendo errores humanos y mejorando la visibilidad del progreso.
- **E:** 2h: 1h configuración, 1h pruebas y ajustes
- **S:** Alcance limitado a una automatización específica del tablero.
- **T:** Se puede verificar cerrando un issue y comprobando que cambia automáticamente a “Done”.