package com.example.entregaya.dto;

import java.time.LocalDateTime;

public record TareaEventoDTO(
        Long tareaId,
        String nombreTarea,
        String tipoEvento, // "CREADA", "COMPLETADA", etc.
        LocalDateTime fecha,
        String usuario
) {}
//no 