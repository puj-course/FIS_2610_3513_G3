package com.example.entregaya.dto;

import java.time.LocalDateTime;

public record TareaEventoDTO(
        Long          tareaId,
        String        nombreTarea,
        Long          trabajoId,
        String        nombreTrabajo,
        String        nuevoEstado,   // "COMPLETADA" | "PENDIENTE"
        String        realizadoPor,  // username del usuario que hizo el cambio
        LocalDateTime fechaEvento
) {}