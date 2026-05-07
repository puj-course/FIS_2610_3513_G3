# Reporte Sprint 12

## Resumen
- Total: 27
- Completados: 27
- Pendientes: 0
- Cumplimiento: 100.00%

## Historias de usuario

- #427 HU-44: Como usuario con muchas notificaciones pendientes quiero marcar todas mis notificaciones como leídas con un solo clic para limpiar rápidamente mi bandeja sin hacerlo una por una
- #428 HU-45: Como miembro de un trabajo que ha dejado un comentario quiero poder editar o eliminar mis propios comentarios en una tarea para corregir errores o limpiar información desactualizada sin depender de un administrador
- #426 HU-43: Como usuario registrado quiero guardar mi Telegram Chat ID en mi perfil de la aplicación para activar las notificaciones externas sin intervención adicional del sistema

## Issues completados
- #477 Sprint Review y Retrospective 12
- #465 Daily 3 Sprint 12
- #427 HU-44: Como usuario con muchas notificaciones pendientes quiero marcar todas mis notificaciones como leídas con un solo clic para limpiar rápidamente mi bandeja sin hacerlo una por una
- #442 TASK: Validar que el endpoint solo afecta las notificaciones del usuario autenticado, sin parámetros de usuario externo
- #443 TASK: Iterar la lista y persistir leida=true para cada notificación usando el repositorio en el mismo controlador
- #428 HU-45: Como miembro de un trabajo que ha dejado un comentario quiero poder editar o eliminar mis propios comentarios en una tarea para corregir errores o limpiar información desactualizada sin depender de un administrador
- #449 TASK: Mostrar botones Editar y Eliminar en 'detalle tarea.html' únicamente para el autor del comentario (th:if con el usuario en sesión)
- #458 Daily 2 Sprint 12
- #433 TASK: Capturar excepciones HTTP del envío con log.error sin interrumpir el ciclo del scheduler ni la notificación interna
- #456 Daily 1 Sprint 12
- #430 TASK: Crear TelegramNotificacionService con método enviarMensaje(String chatId, String texto) usando RestTemplate o HttpClient
- #448 TASK: Registrar evento en HistorialEvento (tipo COMENTARIO_EDITADO / COMENTARIO_ELIMINADO) al realizar cada acción
- #434 TASK: Prueba manual: ejecutar el scheduler y verificar log de envío exitoso o captura del mensaje recibido en Telegram
- #431 TASK: Registrar telegram.bot.token y telegram.api.url en application.properties y exponerlos con @Value
- #445 TASK: Crear endpoint PUT /trabajos/{id}/tareas/{tareaId}/comentarios/{comentarioId} en TareaController
- #446 TASK: Crear endpoint DELETE /trabajos/{id}/tareas/{tareaId}/comentarios/{comentarioId} en TareaController
- #444 TASK: Agregar botón 'Marcar todas como leídas' en Notificaciones.html con fetch/AJAX que actualiza el contador del navbar sin recargar
- #440 TASK: Agregar findByDestinatarioAndLeidaFalse(User user) a NotificacionRepository
- #435 TASK: Agregar campo telegramChatId (String, nullable, max 20 chars) a User.java con getter y setter
- #438 TASK: Actualizar vista perfil.html: campo de entrada, pre-visualización del valor actual y mensajes flash successTelegram / errorTelegram
- #437 TASK: Crear endpoint POST /perfil/actualizar-telegram en PerfilController con @RequestParam y RedirectAttributes
- #426 HU-43: Como usuario registrado quiero guardar mi Telegram Chat ID en mi perfil de la aplicación para activar las notificaciones externas sin intervención adicional del sistema
- #432 TASK: Inyectar TelegramNotificacionService en RecordatorioScheduler e invocarlo tras guardar cada Notificacion interna
- #439 TASK: Validar que chatId vacío se guarde como null (desactiva notificaciones externas) sin error de validación
- #436 TASK: Implementar actualizarTelegramChatId(username, chatId) en CustomUserDetailsService con validación de longitud máxima
- #441 TASK: Implementar endpoint PATCH /notificaciones/leer-todas en NotificacionController con @ResponseBody ResponseEntity<Void>
- #424 Sprint Planning 12

## Issues pendientes
- Ninguno

## Métricas por usuario

### @echaustre12
- Asignados: 10
- Completados: 10
- Pendientes: 0
- Cumplimiento: 100.00%

### @Juanjoyt2
- Asignados: 5
- Completados: 5
- Pendientes: 0
- Cumplimiento: 100.00%

### @andresj-castrillo
- Asignados: 11
- Completados: 11
- Pendientes: 0
- Cumplimiento: 100.00%

### @Silverweta20
- Asignados: 11
- Completados: 11
- Pendientes: 0
- Cumplimiento: 100.00%

### @barretordaniel
- Asignados: 10
- Completados: 10
- Pendientes: 0
- Cumplimiento: 100.00%

