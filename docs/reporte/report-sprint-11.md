# Reporte Sprint 11

## Resumen
- Total: 19
- Completados: 4
- Pendientes: 15
- Cumplimiento: 21.05%

## Historias de usuario

- #393 HU-38: Como colaborador de un trabajo, quiero ver un historial cronológico de los eventos ocurridos (creación de tareas, cambios de estado, ingreso/salida de miembros), para hacer seguimiento de la...
- #405 HU-41:  Como colaborador asignado a una tarea, quiero recibir una notificación interna automática 24 horas antes de que venza una tarea de la que soy responsable, para no perder entregas importantes sin necesidad de revisar constantemente el calendario.
- #401 HU-40:  Como editor o líder de un trabajo, quiero poder asignar etiquetas de texto libre a las tareas (ej. Frontend, Urgente, Revisar), para organizarlas según criterios propios del equipo más allá de los estados y dificultades predefinidos.
- #389 HU-37: Como usuario autenticado de EntregaYa, quiero ver un panel de estadísticas personales con métricas de mis tareas y trabajos, para entender mi rendimiento y productividad sin revisar cada trabajo individualmente.

## Issues completados
- #414 Daily 3 Sprint 11
- #410 Daily 2 Sprint 11
- #408 Daily 1 Sprint 11
- #392 Sprint Planning 11

## Issues pendientes
- #394 TASK: Crear entidad HistorialEvento, repositorio y método de registro en CustomTrabajoDetailsService.
- #393 HU-38: Como colaborador de un trabajo, quiero ver un historial cronológico de los eventos ocurridos (creación de tareas, cambios de estado, ingreso/salida de miembros), para hacer seguimiento de la...
- #396 TASK: Crear vista historial.html y endpoint GET /trabajos/{id}/historial.
- #395 TASK: Ampliar TrabajoObserver y MiembroNotificacionObserver para publicar eventos al historial.
- #417 Sprint Review y Retrospective 11
- #402 TASK: Agregar @ElementCollection etiquetas a Tarea, migración de BD, actualizar CustomTareaDetailsService.
- #405 HU-41:  Como colaborador asignado a una tarea, quiero recibir una notificación interna automática 24 horas antes de que venza una tarea de la que soy responsable, para no perder entregas importantes sin necesidad de revisar constantemente el calendario.
- #401 HU-40:  Como editor o líder de un trabajo, quiero poder asignar etiquetas de texto libre a las tareas (ej. Frontend, Urgente, Revisar), para organizarlas según criterios propios del equipo más allá de los estados y dificultades predefinidos.
- #407 TASK: Agregar campo recordatorioEnviado a entidad Tarea, actualizar BD y lógica para evitar duplicados.
- #406 TASK: Crear RecordatorioScheduler con @Scheduled, query en TareaRepository para tareas próximas a vencer y lógica de creación de Notificacion.
- #403 TASK: Actualizar CrearTarea.html, EditarTarea.html y detalle tarea.html para gestionar/mostrar etiquetas.
- #404 TASK: Implementar filtro por etiqueta en TareaRepository y conectarlo con la búsqueda existente.
- #389 HU-37: Como usuario autenticado de EntregaYa, quiero ver un panel de estadísticas personales con métricas de mis tareas y trabajos, para entender mi rendimiento y productividad sin revisar cada trabajo individualmente.
- #390 TASK: Extender DashboardFacade con métodos getEstadisticasPersonales(User): total tareas, completadas, vencidas y tasa.
- #391 TASK: Crear vista Thymeleaf estadisticas.html y endpoint GET /estadisticas en AuthController.

## Métricas por usuario

### @Silverweta20
- Asignados: 9
- Completados: 4
- Pendientes: 5
- Cumplimiento: 44.44%

### @echaustre12
- Asignados: 9
- Completados: 4
- Pendientes: 5
- Cumplimiento: 44.44%

### @Juanjoyt2
- Asignados: 5
- Completados: 4
- Pendientes: 1
- Cumplimiento: 80.00%

### @andresj-castrillo
- Asignados: 8
- Completados: 4
- Pendientes: 4
- Cumplimiento: 50.00%

### @barretordaniel
- Asignados: 8
- Completados: 4
- Pendientes: 4
- Cumplimiento: 50.00%

