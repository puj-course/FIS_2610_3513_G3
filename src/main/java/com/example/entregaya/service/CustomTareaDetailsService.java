package com.example.entregaya.service;

import com.example.entregaya.builder.TareaBuilder;
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

    /**
     * HU-22 (#269): Crear una tarea usando TareaBuilder con validaciones automáticas.
     * 
     * @param tarea Objeto con los datos de la tarea (nombre, descripción, fechas, dificultad)
     * @param trabajoId ID del trabajo al que pertenece la tarea
     * @param responsableIds IDs de los usuarios responsables
     * @return Tarea guardada en la base de datos
     * @throws IllegalStateException si el nombre está vacío o las fechas son inconsistentes
     * @throws RuntimeException si el trabajo no existe
     */
    public Tarea crearTarea(Tarea tarea, Long trabajoId, List<Long> responsableIds) {
        // 1. Buscar el trabajo
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("trabajo no encontrado"));
        
        // 2. Buscar responsables si se proporcionaron
        Set<User> responsables = new HashSet<>();
        if (responsableIds != null && !responsableIds.isEmpty()) {
            responsables = new HashSet<>(userRepository.findAllById(responsableIds));
        }
        
        // 3. USAR BUILDER para construir la tarea con validaciones automáticas
        // Las validaciones del Builder se ejecutan en build() y lanzan IllegalStateException si hay errores
        Tarea tareaValidada = new TareaBuilder()
                .nombre(tarea.getNombre())
                .descripcion(tarea.getDescripcion())
                .fechaInicio(tarea.getFechaInicio())
                .fechaFinal(tarea.getFechaFinal())
                .dificultad(tarea.getDificultad())
                .trabajo(trabajo)
                .responsables(responsables)
                .build();  // ← Aquí se ejecutan las validaciones
        
        // 4. Guardar la tarea validada
        return tareaRepository.save(tareaValidada);
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

    /**
     * HU-22 (#270): Editar una tarea usando TareaBuilder con validaciones automáticas.
     * Usa el Builder para validar los nuevos datos antes de actualizar la tarea existente.
     * Preserva el ID y el trabajo de la tarea original.
     * 
     * @param tareaId ID de la tarea a editar
     * @param tareaActualizada Objeto con los nuevos datos
     * @param responsableIds IDs de los nuevos responsables
     * @return Tarea actualizada y guardada
     * @throws IllegalStateException si el nombre está vacío o las fechas son inconsistentes
     * @throws RuntimeException si la tarea no existe
     */
    public Tarea editarTarea(Long tareaId, Tarea tareaActualizada, List<Long> responsableIds) {
        // 1. Obtener tarea existente de la BD
        Tarea tareaExistente = findById(tareaId);
        
        // 2. Preparar responsables
        Set<User> responsables = new HashSet<>();
        if (responsableIds != null && !responsableIds.isEmpty()) {
            responsables = new HashSet<>(userRepository.findAllById(responsableIds));
        }
        
        // 3. USAR BUILDER para validar los nuevos datos
        // Mantener el mismo trabajo que la tarea existente
        Tarea tareaValidada = new TareaBuilder()
                .nombre(tareaActualizada.getNombre())
                .descripcion(tareaActualizada.getDescripcion())
                .fechaInicio(tareaActualizada.getFechaInicio())
                .fechaFinal(tareaActualizada.getFechaFinal())
                .dificultad(tareaActualizada.getDificultad())
                .trabajo(tareaExistente.getTrabajo())  // Preservar trabajo original
                .responsables(responsables)
                .build();  // ← Valida todo aquí
        
        // 4. Si llegamos aquí, los datos son válidos
        // Copiar campos validados a la tarea existente (preserva ID)
        tareaExistente.setNombre(tareaValidada.getNombre());
        tareaExistente.setDescripcion(tareaValidada.getDescripcion());
        tareaExistente.setFechaInicio(tareaValidada.getFechaInicio());
        tareaExistente.setFechaFinal(tareaValidada.getFechaFinal());
        tareaExistente.setDificultad(tareaValidada.getDificultad());
        tareaExistente.setResponsables(tareaValidada.getResponsables());
        
        // 5. Guardar tarea existente (preserva ID y relaciones)
        return tareaRepository.save(tareaExistente);
    }

}
