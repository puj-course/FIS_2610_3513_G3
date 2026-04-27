package com.example.entregaya.observer;

import com.example.entregaya.dto.TareaEventoDTO;
import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.repository.HistorialEventoRepository;
import com.example.entregaya.repository.TareaRepository;
import org.springframework.stereotype.Component;

/**
 * Observer que registra eventos de tareas en el historial del trabajo.
 * Captura: creación de tareas, cambios de estado (completada/incompletada)
 */
@Component
public class TareaHistorialObserver implements TareaObserver {

    private final HistorialEventoRepository historialEventoRepository;
    private final TareaRepository tareaRepository;

    public TareaHistorialObserver(HistorialEventoRepository historialEventoRepository,
                                  TareaRepository tareaRepository) {
        this.historialEventoRepository = historialEventoRepository;
        this.tareaRepository = tareaRepository;
    }

    @Override
    public void actualizar(TareaEventoDTO evento) {
        // Obtener la tarea
        Tarea tarea = tareaRepository.findById(evento.tareaId())
                .orElse(null);
        if (tarea == null || tarea.getTrabajo() == null) return;

        // Registrar en el historial del trabajo
        String descripcion;
        switch (evento.tipoEvento()) {
            case CREACION -> descripcion = "Tarea \"" + evento.nombreTarea() + "\" fue creada";
            case COMPLETADA -> descripcion = "Tarea \"" + evento.nombreTarea() + "\" fue marcada como completada";
            case INCOMPLETADA -> descripcion = "Tarea \"" + evento.nombreTarea() + "\" fue marcada como incompleta";
            case EDITADA -> descripcion = "Tarea \"" + evento.nombreTarea() + "\" fue editada";
            default -> descripcion = "Cambio en la tarea \"" + evento.nombreTarea() + "\"";
        }

        HistorialEvento historialEvento = new HistorialEvento(
                tarea.getTrabajo(),
                mapearTipoEventoTarea(evento.tipoEvento()),
                descripcion,
                evento.detalles(),
                evento.usuarioAccion(),
                evento.fechaEvento(),
                tarea
        );

        historialEventoRepository.save(historialEvento);
    }

    private HistorialEvento.TipoEvento mapearTipoEventoTarea(TareaEventoDTO.TipoEvento tipoTarea) {
        return switch (tipoTarea) {
            case CREACION -> HistorialEvento.TipoEvento.CREACION_TAREA;
            case COMPLETADA, INCOMPLETADA -> HistorialEvento.TipoEvento.CAMBIO_ESTADO_TAREA;
            case EDITADA -> HistorialEvento.TipoEvento.EDICION_TRABAJO;
        };
    }
}
