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

}
