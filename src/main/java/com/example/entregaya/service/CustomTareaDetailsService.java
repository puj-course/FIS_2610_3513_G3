package com.example.entregaya.service;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.TrabajoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomTareaDetailsService {
    private final TareaRepository tareaRepository;
    private final TrabajoRepository trabajoRepository;

    public CustomTareaDetailsService(TareaRepository tareaRepository, TrabajoRepository trabajoRepository) {
        this.tareaRepository = tareaRepository;
        this.trabajoRepository = trabajoRepository;
    }

    //Crear una tarea asociada a un trabajo
    public Tarea crearTarea(Tarea tarea, Long trabajoId) {
        Trabajo trabajo =  trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("trabajo no encontrado"));
        tarea.setTrabajo(trabajo);
        return tareaRepository.save(tarea);
    }

    //Lista de tareas de un trabajo
    public List<Tarea> tareas (Long trabajoId) {
            return tareaRepository.findBytrabajoId(trabajoId);
    }

    public Tarea findById(Long tareaId) {
        return tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("tarea no encontrado"));
    }

    public void eliminar (long Id) {
        tareaRepository.deleteById(Id);
    }

    //alternar estado de tarea, completada/pendiente
    public void toggleCompletada(Long tareaId) {
        Tarea tarea = findById(tareaId);
        tarea.setCompletada(!tarea.getIsCompletada());
        tareaRepository.save(tarea);
    }

    // Calcular el progreso de un trabajo teniendo en cuenta las dificultades de las tareas
    public int calcularProgreso(Long trabajoId) {
        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajoId);

        if (tareas.isEmpty()) return 0;

        int pesoTotal = tareas.stream().mapToInt(t -> t.getDificultad().getPeso()).sum();

        int pesoCompletado = tareas.stream().filter(Tarea::getIsCompletada).mapToInt(t -> t.getDificultad().getPeso()).sum();

        return (int) Math.round((pesoCompletado * 100.0) / pesoTotal);
    }
}
