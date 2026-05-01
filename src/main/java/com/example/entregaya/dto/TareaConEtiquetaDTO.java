package com.example.entregaya.dto;

import com.example.entregaya.decorator.TareaInfo;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * HU-29 (#316): DTO (record) que contiene toda la información de una tarea
 * más las etiquetas de urgencia calculadas por el decorador.
 * 
 * Este record se usa para pasar datos de tareas con etiquetas desde el
 * servicio hacia las vistas Thymeleaf.
 */
public record TareaConEtiquetaDTO(
    Long id,
    String nombre,
    String descripcion,
    LocalDateTime fechaInicio,
    LocalDateTime fechaFinal,
    Tarea.Dificultad dificultad,
    boolean isCompletada,
    Trabajo trabajo,
    Set<User> responsables,
    String etiquetaUrgencia,
    String colorEtiqueta
) {
    /**
     * Crea un DTO a partir de un TareaInfo (que ya tiene el decorador aplicado).
     * 
     * @param tareaInfo TareaInfo con el decorador de urgencia aplicado
     * @return TareaConEtiquetaDTO con todos los datos de la tarea y las etiquetas
     */
    public static TareaConEtiquetaDTO fromTareaInfo(TareaInfo tareaInfo) {
        return new TareaConEtiquetaDTO(
            tareaInfo.getId(),
            tareaInfo.getNombre(),
            tareaInfo.getDescripcion(),
            tareaInfo.getFechaInicio(),
            tareaInfo.getFechaFinal(),
            tareaInfo.getDificultad(),
            tareaInfo.getIsCompletada(),
            tareaInfo.getTrabajo(),
            tareaInfo.getResponsables(),
            tareaInfo.getEtiquetaUrgencia(),
            tareaInfo.getColorEtiqueta()
        );
    }
}
