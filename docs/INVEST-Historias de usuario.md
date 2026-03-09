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

### HU-10 – Sugerencias educativas
- **I:** Función independiente.
- **N:** Puede ajustarse el contenido de las sugerencias.
- **V:** Ayuda a mejorar las habilidades digitales.
- **E:** Estimable según el tipo de mensajes.
- **S:** Alcance moderado.
- **T:** Se prueba mostrando mensajes educativos.

---

### HU-11 – Panel principal con trabajos
- **I:** Independiente de otras funciones.
- **N:** Puede cambiar el diseño del panel.
- **V:** Permite ver todos los trabajos rápidamente.
- **E:** Fácil de estimar.
- **S:** Historia clara.
- **T:** Se prueba visualizando el panel con los trabajos.

---

### HU-12 – Reemplazar archivo
- **I:** Depende solo del trabajo existente.
- **N:** Puede modificarse el método de subida.
- **V:** Permite corregir errores antes de la entrega.
- **E:** Estimable por su alcance.
- **S:** Historia pequeña.
- **T:** Se prueba reemplazando un archivo subido.

---

### HU-13 – Notificaciones de cambios
- **I:** Funciona independientemente de otras funciones.
- **N:** Puede modificarse el tipo de notificación.
- **V:** Mantiene informados a los integrantes.
- **E:** Estimable.
- **S:** Tamaño adecuado.
- **T:** Se prueba enviando notificaciones de cambios.

---

### HU-14 – Recordatorios de entrega
- **I:** Independiente de otras historias.
- **N:** Se puede ajustar la frecuencia de recordatorios.
- **V:** Ayuda a organizar mejor el tiempo.
- **E:** Estimable.
- **S:** Historia pequeña.
- **T:** Se prueba generando recordatorios antes de la fecha límite.

---

### HU-15 – Retirar integrante
- **I:** Depende solo del trabajo grupal.
- **N:** Se puede negociar quién tiene permisos para hacerlo.
- **V:** Permite mantener actualizado el equipo.
- **E:** Estimable.
- **S:** Alcance claro.
- **T:** Se prueba eliminando un integrante del grupo.

---

### HU-16 – Asignar roles
- **I:** Independiente de otras funciones.
- **N:** Puede ajustarse la cantidad de roles disponibles.
- **V:** Define responsabilidades dentro del grupo.
- **E:** Estimable.
- **S:** Historia manejable.
- **T:** Se prueba asignando roles a integrantes.

---

### HU-17 – Historial de cambios
- **I:** Independiente del resto.
- **N:** Puede ajustarse la información mostrada.
- **V:** Permite ver modificaciones realizadas.
- **E:** Estimable.
- **S:** Tamaño adecuado.
- **T:** Se prueba registrando cambios en el trabajo.

---

### HU-18 – Comentarios en el trabajo
- **I:** Función independiente.
- **N:** Se puede modificar el formato de comentarios.
- **V:** Facilita la comunicación del grupo.
- **E:** Estimable.
- **S:** Historia pequeña.
- **T:** Se prueba agregando comentarios dentro del trabajo.

---

### HU-19 – Plantillas académicas
- **I:** Independiente de otras historias.
- **N:** Se pueden agregar o modificar plantillas.
- **V:** Facilita cumplir formatos académicos.
- **E:** Estimable.
- **S:** Tamaño moderado.
- **T:** Se prueba creando trabajos con plantillas.
---

### HU-20 – Sección de ayuda
- **I:** Función independiente.
- **N:** Puede modificarse el contenido de ayuda.
- **V:** Ayuda a resolver dudas del usuario.
- **E:** Estimable.
- **S:** Historia pequeña.
- **T:** Se prueba accediendo a la sección de ayuda.
---

### HU-21 – Tiempo restante para la entrega
- **I:** Independiente de otras funciones.
- **N:** Se puede ajustar la forma de visualización.
- **V:** Ayuda a planificar mejor el trabajo.
- **E:** Estimable.
- **S:** Historia pequeña.
- **T:** Se prueba mostrando el tiempo restante.

---

### HU-22 – Configurar notificaciones
- **I:** Función independiente.
- **N:** Puede modificarse el tipo de notificaciones.
- **V:** Permite recibir solo avisos importantes.
- **E:** Estimable.
- **S:** Historia pequeña.
- **T:** Se prueba activando y desactivando notificaciones.

---

### HU-23 – Editar perfil
- **I:** Independiente de otras funciones.
- **N:** Se puede modificar la información editable.
- **V:** Permite mantener actualizada la información del usuario.
- **E:** Estimable.
- **S:** Historia pequeña.
- **T:** Se prueba editando los datos del perfil.

---

### HU-24 – Ver responsable de tareas
- **I:** Depende solo de las tareas del trabajo.
- **N:** Puede ajustarse la forma de asignación.
- **V:** Evita confusiones dentro del grupo.
- **E:** Estimable.
- **S:** Tamaño adecuado.
- **T:** Se prueba mostrando el responsable de cada tarea.

---

## Historias No Funcionales

### HU-NF-01 – Tiempo de carga del panel
- **I:** Independiente de otras funciones.
- **N:** El tiempo puede ajustarse según requisitos.
- **V:** Mejora la experiencia del usuario.
- **E:** Estimable mediante pruebas de rendimiento.
- **S:** Alcance claro.
- **T:** Se prueba midiendo el tiempo de carga del panel.

---

### HU-NF-02 – Guardado automático
- **I:** Independiente de otras funciones.
- **N:** Puede cambiar el intervalo de guardado.
- **V:** Evita pérdida de información.
- **E:** Estimable técnicamente.
- **S:** Tamaño moderado.
- **T:** Se prueba verificando el guardado automático cada minuto.
