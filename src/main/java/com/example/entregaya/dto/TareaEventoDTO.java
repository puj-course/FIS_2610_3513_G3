package com.example.entregaya.dto;

import java.time.LocalDateTime;

/**
 * DTO que encapsula eventos de cambios en tareas.
 * Utilizado por el patrón Observer para notificar cambios de estado.
 */
public record TareaEventoDTO(
        Long tareaId,
        String nombreTarea,
        Long trabajoId,
        String nombreTrabajo,
        TipoEvento tipoEvento,     // CREACION, COMPLETADA, INCOMPLETADA, EDITADA
        String detalles,            // Información adicional del evento
        String usuarioAccion,       // Username quien ejecutó la acción
        LocalDateTime fechaEvento
) {
    // Para compatibilidad con código existente que usa nuevoEstado
    public String nuevoEstado() {
        return tipoEvento == TipoEvento.COMPLETADA ? "COMPLETADA" : "PENDIENTE";
    }

    // Para compatibilidad con código existente que usa realizadoPor
    public String realizadoPor() {
        return usuarioAccion;
    }

    public enum TipoEvento {
        CREACION("Tarea creada"),
        COMPLETADA("Marcada como completada"),
        INCOMPLETADA("Marcada como incompleta"),
        EDITADA("Tarea editada");

        private final String descripcion;

        TipoEvento(String descripcion) {
            this.descripcion = descripcion;
        }

        public String getDescripcion() {
            return descripcion;
        }
    }
}
