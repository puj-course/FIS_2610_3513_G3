package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;

/**
 * HU-29 (#316): Decorador para tareas urgentes.
 * Muestra etiqueta "Urgente" en color rojo.
 * Una tarea es urgente si le quedan entre 0 y 2 días para la fecha final.
 */
public class UrgenteDecorator extends TareaInfoBase {
    
    public UrgenteDecorator(Tarea tarea) {
        super(tarea);
    }
    
    @Override
    public String getEtiquetaUrgencia() {
        return "Urgente";
    }
    
    @Override
    public String getColorEtiqueta() {
        return "#ef4444";  // Rojo
    }
}
