package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TrabajoRepository;
import org.springframework.stereotype.Component;

/**
 * Observer concreto que crea notificaciones para los miembros de un trabajo
 * cuando alguien se une o abandona.
 *
 * Itera sobre todos los colaboradores del trabajo (excluyendo al afectado)
 * y crea una Notificacion persistida para cada uno.
 */
@Component
public class MiembroNotificacionObserver implements TrabajoObserver {

    private final NotificacionRepository notificacionRepository;
    private final TrabajoRepository trabajoRepository;

    public MiembroNotificacionObserver(NotificacionRepository notificacionRepository,
                                       TrabajoRepository trabajoRepository) {
        this.notificacionRepository = notificacionRepository;
        this.trabajoRepository = trabajoRepository;
    }

    @Override
    public void actualizar(TrabajoEventoDTO evento) {
        // Obtener el trabajo
        Trabajo trabajo = trabajoRepository.findById(evento.trabajoId())
                .orElse(null);
        if (trabajo == null) return;

        // Construir mensaje según el tipo de evento
        String mensajeTipo = evento.tipoEvento() == TrabajoEventoDTO.TipoEvento.INGRESO
                ? "se unió al"
                : "abandonó el";

        String mensaje = String.format(
                "%s %s trabajo \"%s\" el %s.",
                evento.afectadoUsername(),
                mensajeTipo,
                evento.nombreTrabajo(),
                evento.fechaEvento().toLocalDate()
        );

        // Iterar sobre todos los colaboradores
        trabajo.getColaboradores().stream()
                // Excluir al usuario afectado
                .filter(c -> !c.getUser().getUsername().equals(evento.afectadoUsername()))
                .forEach(colaborador -> {
                    // Crear y persistir una notificación por cada colaborador
                    Notificacion notificacion = new Notificacion(
                            colaborador.getUser(),
                            mensaje,
                            Notificacion.TipoNotificacion.MIEMBRO
                    );
                    notificacionRepository.save(notificacion);
                });
    }
}
