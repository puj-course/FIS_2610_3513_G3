package com.example.entregaya.service;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomTareaDetailsService {
    private final TareaRepository tareaRepository;
    private final TrabajoRepository trabajoRepository;
    private final UserRepository userRepository;

    public CustomTareaDetailsService(TareaRepository tareaRepository, TrabajoRepository trabajoRepository,  UserRepository userRepository) {
        this.tareaRepository = tareaRepository;
        this.trabajoRepository = trabajoRepository;
        this.userRepository = userRepository;
    }

    //Crear una tarea asociada a un trabajo
    public Tarea crearTarea(Tarea tarea, Long trabajoId, List<Long> responsableIds) {
        Trabajo trabajo =  trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("trabajo no encontrado"));
        tarea.setTrabajo(trabajo);
        if(responsableIds != null && !responsableIds.isEmpty()) {
            Set<User> responsables = new HashSet<>(userRepository.findAllById(responsableIds));
            tarea.setResponsables(responsables);
        }
        return tareaRepository.save(tarea);
    }
    public Tarea crearTarea(Tarea tarea, Long trabajoId) {
        return crearTarea(tarea, trabajoId, null);
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

    public void actualizarResponsables(Long tareaId, List<Long> responsableIds) {
        Tarea tarea = findById(tareaId);
        if(responsableIds != null && !responsableIds.isEmpty()) {
            Set<User> responsables = new HashSet<>(userRepository.findAllById(responsableIds));
            tarea.setResponsables(responsables);
        }
        else{
            tarea.setResponsables(new HashSet<>());
        }
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

    public Tarea editarTarea(Long tareaId, Tarea tareaActualizada, List<Long> responsableIds) {
        Tarea tarea = findById(tareaId);

        // Actualizar campos
        tarea.setNombre(tareaActualizada.getNombre());
        tarea.setDescripcion(tareaActualizada.getDescripcion());
        tarea.setFechaInicio(tareaActualizada.getFechaInicio());
        tarea.setFechaFinal(tareaActualizada.getFechaFinal());
        tarea.setDificultad(tareaActualizada.getDificultad());

        // Actualizar responsables
        if (responsableIds != null && !responsableIds.isEmpty()) {
            Set<User> responsables = new HashSet<>(userRepository.findAllById(responsableIds));
            tarea.setResponsables(responsables);
        } else {
            tarea.setResponsables(new HashSet<>());
        }

        return tareaRepository.save(tarea);
    }

}
