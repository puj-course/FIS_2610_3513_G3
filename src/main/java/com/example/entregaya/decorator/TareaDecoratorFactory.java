package com.example.entregaya.decorator;

import com.example.entregaya.model.Tarea;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * HU-29 (#316): Factory para seleccionar el decorador apropiado según el estado de la tarea.
 * 
 * Lógica de selección:
 * 1. Si la tarea está completada → CompletadaDecorator
 * 2. Si no tiene fecha final → SinFechaDecorator
 * 3. Si la fecha final ya pasó → VencidaDecorator
 * 4. Si quedan 0-2 días → UrgenteDecorator
 * 5. Si quedan 3-7 días → ProximaDecorator
 * 6. Si quedan más de 7 días → TareaInfoBase (Normal)
 */
public class TareaDecoratorFactory {
    
    /**
     * Resuelve qué decorador aplicar a una tarea según su estado y fecha.
     * 
     * @param tarea La tarea a decorar
     * @return TareaInfo con el decorador apropiado
     */
    public static TareaInfo resolver(Tarea tarea) {
        // 1. Tarea completada
        if (tarea.getIsCompletada()) {
            return new CompletadaDecorator(tarea);
        }
        
        // 2. Sin fecha final
        if (tarea.getFechaFinal() == null) {
            return new SinFechaDecorator(tarea);
        }
        
        // 3. Calcular días hasta la fecha final
        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime fechaFinal = tarea.getFechaFinal();
        long diasHastaFinal = ChronoUnit.DAYS.between(ahora, fechaFinal);
        
        // 4. Vencida (fecha ya pasó)
        if (diasHastaFinal < 0) {
            return new VencidaDecorator(tarea);
        }
        
        // 5. Urgente (0-2 días)
        if (diasHastaFinal <= 2) {
            return new UrgenteDecorator(tarea);
        }
        
        // 6. Próxima (3-7 días)
        if (diasHastaFinal <= 7) {
            return new ProximaDecorator(tarea);
        }
        
        // 7. Normal (más de 7 días)
        return new TareaInfoBase(tarea);
    }
}
