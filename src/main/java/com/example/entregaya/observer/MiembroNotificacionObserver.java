package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.HistorialEventoRepository;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.TrabajoRepository;
import org.springframework.stereotype.Component;

/**
 * Observer concreto que realiza dos acciones:
 * 1. Crea notificaciones para los miembros de un trabajo cuando alguien se une o abandona
 * 2. Registra el evento en el historial del trabajo (para HU-30)
 */
@Component
public class MiembroNotificacionObserver implements TrabajoObserver {

    private final NotificacionRepository notificacionRepository;
    private final TrabajoRepository trabajoRepository;
    private final HistorialEventoRepository historialEventoRepository;

    public MiembroNotificacionObserver(NotificacionRepository notificacionRepository,
                                       TrabajoRepository trabajoRepository,
                                       HistorialEventoRepository historialEventoRepository) {
        this.notificacionRepository = notificacionRepository;
        this.trabajoRepository = trabajoRepository;
        this.historialEventoRepository = historialEventoRepository;
    }

    @Override
    public void actualizar(TrabajoEventoDTO evento) {
        // Obtener el trabajo
        Trabajo trabajo = trabajoRepository.findById(evento.trabajoId())
                .orElse(null);
        if (trabajo == null) return;

        // 1. Crear notificaciones para los miembros
        crearNotificacionesMiembros(evento, trabajo);

        // 2. Registrar el evento en el historial
        registrarEnHistorial(evento, trabajo);
    }

    /**
     * Crea notificaciones para todos los miembros del trabajo
     * (excepto el afectado por el evento)
     */
    private void crearNotificacionesMiembros(TrabajoEventoDTO evento, Trabajo trabajo) {
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


    private void registrarEnHistorial(TrabajoEventoDTO evento, Trabajo trabajo) {
        HistorialEvento.TipoEvento tipoHistorial = evento.tipoEvento() == TrabajoEventoDTO.TipoEvento.INGRESO
                ? HistorialEvento.TipoEvento.INGRESO_MIEMBRO
                : HistorialEvento.TipoEvento.SALIDA_MIEMBRO;

        String descripcion = evento.tipoEvento() == TrabajoEventoDTO.TipoEvento.INGRESO
                ? evento.afectadoUsername() + " se unió al trabajo"
                : evento.afectadoUsername() + " abandonó el trabajo";

        // CORRECTO (debe ser):
        HistorialEvento historialEvento = new HistorialEvento(
                trabajo,
                tipoHistorial,
                descripcion,
                "Ejecutado por: " + evento.realizadoPorUsername(),  // detalles
                evento.afectadoUsername(),                          // usuarioAccion
                evento.fechaEvento(),
                null  // tarea (no aplica para eventos de miembros)
        );

        historialEventoRepository.save(historialEvento);
    }
}
