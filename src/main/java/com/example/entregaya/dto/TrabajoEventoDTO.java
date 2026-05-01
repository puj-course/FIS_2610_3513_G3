package com.example.entregaya.dto;

import java.time.LocalDateTime;

/**
 * DTO que encapsula la información de un evento relacionado con cambios
 * en la composición de un trabajo (ingreso/salida de miembros).
 */
public record TrabajoEventoDTO(
        Long trabajoId,
        String nombreTrabajo,
        TipoEvento tipoEvento,  // INGRESO | SALIDA
        String afectadoUsername,     // Usuario que se unió o abandonó
        String realizadoPorUsername, // Usuario que ejecutó la acción
        LocalDateTime fechaEvento
) {
    public enum TipoEvento {
        INGRESO,
        SALIDA
    }
}
