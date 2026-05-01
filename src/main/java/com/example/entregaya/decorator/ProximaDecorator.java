package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;

/**
 * HU-29 (#316): Decorador para tareas próximas.
 * Muestra etiqueta "Próxima" en color amarillo/naranja.
 * Una tarea es próxima si le quedan entre 3 y 7 días para la fecha final.
 */
public class ProximaDecorator extends TareaInfoBase {
    
    public ProximaDecorator(Tarea tarea) {
        super(tarea);
    }
    
    @Override
    public String getEtiquetaUrgencia() {
        return "Próxima";
    }
    
    @Override
    public String getColorEtiqueta() {
        return "#f59e0b";  // Amarillo/Naranja
    }
}
