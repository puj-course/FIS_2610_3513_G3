package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * HU-29: Clase base del patrón Decorator para información de tareas.
 * Envuelve una entidad Tarea y delega todos los métodos de acceso.
 * Proporciona implementación por defecto de las etiquetas de urgencia.
 */
public class TareaInfoBase implements TareaInfo {
    
    protected final Tarea tarea;
    
    public TareaInfoBase(Tarea tarea) {
        if (tarea == null) {
            throw new IllegalArgumentException("La tarea no puede ser null");
        }
        this.tarea = tarea;
    }
    
    @Override
    public String getEtiquetaUrgencia() {
        return "Normal";
    }
    
    @Override
    public String getColorEtiqueta() {
        return "#10b981";  // Verde
    }
    
    @Override
    public Long getId() {
        return tarea.getId();
    }
    
    @Override
    public String getNombre() {
        return tarea.getNombre();
    }
    
    @Override
    public String getDescripcion() {
        return tarea.getDescripcion();
    }
    
    @Override
    public LocalDateTime getFechaInicio() {
        return tarea.getFechaInicio();
    }
    
    @Override
    public LocalDateTime getFechaFinal() {
        return tarea.getFechaFinal();
    }
    
    @Override
    public Trabajo getTrabajo() {
        return tarea.getTrabajo();
    }
    
    @Override
    public Tarea.Dificultad getDificultad() {
        return tarea.getDificultad();
    }
    
    @Override
    public boolean getIsCompletada() {
        return tarea.getIsCompletada();
    }
    
    @Override
    public Set<User> getResponsables() {
        return tarea.getResponsables();
    }
    
    @Override
    public Tarea getTareaOriginal() {
        return tarea;
    }
}
