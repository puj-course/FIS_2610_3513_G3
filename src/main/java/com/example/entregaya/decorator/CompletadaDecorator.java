package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;

/**
 * HU-29 (#316): Decorador para tareas completadas.
 * Muestra etiqueta "Completada" en color verde.
 */
public class CompletadaDecorator extends TareaInfoBase {
    
    public CompletadaDecorator(Tarea tarea) {
        super(tarea);
    }
    
    @Override
    public String getEtiquetaUrgencia() {
        return "Completada";
    }
    
    @Override
    public String getColorEtiqueta() {
        return "#10b981";  // Verde
    }
}
