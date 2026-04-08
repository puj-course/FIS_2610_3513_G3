package com.example.entregaya.observer;

import com.example.entregaya.dto.TareaEventoDTO;
import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.model.Tarea;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Observer concreto que persiste una Notificacion por cada responsable
 * de la tarea cuando se recibe un evento de cambio de estado.
 *
 * Si la tarea no tiene responsables, el evento llega igualmente
 * pero no se genera ninguna notificación (cumple CA6).
 */
@Component
public class NotificacionObserver implements TareaObserver {

    private final NotificacionRepository notificacionRepository;
    private final TareaRepository tareaRepository;

    public NotificacionObserver(NotificacionRepository notificacionRepository,
                                TareaRepository tareaRepository) {
        this.notificacionRepository = notificacionRepository;
        this.tareaRepository = tareaRepository;
    }

    /**
     * Crea y persiste una Notificacion para cada responsable de la tarea.
     * El mensaje cumple CA2: incluye nombre de tarea, nombre del trabajo,
     * nuevo estado, quién realizó el cambio y fecha/hora del evento.
     */
    @Override
    public void actualizar(TareaEventoDTO evento) {
        // Obtener responsables actuales de la tarea
        Tarea tarea = tareaRepository.findById(evento.tareaId()).orElse(null);
        if (tarea == null) return;

        Set<User> responsables = tarea.getResponsables();

        // CA6: si no hay responsables, no se generan notificaciones
        if (responsables == null || responsables.isEmpty()) return;

        String mensaje = String.format(
                "La tarea \"%s\" del trabajo \"%s\" fue marcada como %s por %s el %s.",
                evento.nombreTarea(),
                evento.nombreTrabajo(),
                evento.nuevoEstado(),
                evento.realizadoPor(),
                evento.fechaEvento().toLocalDate()
        );

        for (User responsable : responsables) {
            Notificacion notificacion = new Notificacion(responsable, mensaje);
            notificacionRepository.save(notificacion);
        }
    }
}