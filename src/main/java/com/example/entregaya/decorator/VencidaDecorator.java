package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;

/**
 * HU-29 (#316): Decorador para tareas vencidas.
 * Muestra etiqueta "Vencida" en color rojo.
 * Una tarea está vencida si su fecha final ya pasó y no está completada.
 */
public class VencidaDecorator extends TareaInfoBase {
    
    public VencidaDecorator(Tarea tarea) {
        super(tarea);
    }
    
    @Override
    public String getEtiquetaUrgencia() {
        return "Vencida";
    }
    
    @Override
    public String getColorEtiqueta() {
        return "#ef4444";  // Rojo
    }
}
