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
