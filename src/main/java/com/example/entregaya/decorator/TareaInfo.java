package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * HU-29: Interfaz para el patrón Decorator de información de tareas.
 * Define el contrato para obtener información de una tarea, incluyendo
 * las etiquetas de urgencia dinámicas basadas en la fecha de cierre.
 */
public interface TareaInfo {
    
    // Métodos de urgencia
    String getEtiquetaUrgencia();
    String getColorEtiqueta();
    
    // Métodos delegados de Tarea
    Long getId();
    String getNombre();
    String getDescripcion();
    LocalDateTime getFechaInicio();
    LocalDateTime getFechaFinal();
    Trabajo getTrabajo();
    Tarea.Dificultad getDificultad();
    boolean getIsCompletada();
    Set<User> getResponsables();
    Tarea getTareaOriginal();
}
