package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;

/**
 * HU-29 (#316): Decorador para tareas sin fecha final.
 * Muestra etiqueta "Sin fecha" en color gris.
 */
public class SinFechaDecorator extends TareaInfoBase {
    
    public SinFechaDecorator(Tarea tarea) {
        super(tarea);
    }
    
    @Override
    public String getEtiquetaUrgencia() {
        return "Sin fecha";
    }
    
    @Override
    public String getColorEtiqueta() {
        return "#6b7280";  // Gris
    }
}
