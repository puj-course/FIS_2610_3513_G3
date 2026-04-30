package com.example.entregaya.dto;

import java.time.LocalDateTime;

/**
 * DTO para serializar eventos del historial en respuestas JSON
 */
public record HistorialEventoDTO(
        Long id,
        String tipoEvento,
        String descripcion,
        String detalles,
        String usuarioAccion,
        LocalDateTime fechaEvento,
        Long tareaId,
        String nombreTarea
) {
    public static HistorialEventoDTO fromEntity(com.example.entregaya.model.HistorialEvento evento) {
        return new HistorialEventoDTO(
                evento.getId(),
                evento.getTipoEvento().name(),
                evento.getDescripcion(),
                evento.getDetalles(),
                evento.getUsuarioAccion(),
                evento.getFechaEvento(),
                evento.getTarea() != null ? evento.getTarea().getId() : null,
                evento.getTarea() != null ? evento.getTarea().getNombre() : null
        );
    }
}
