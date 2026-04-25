package com.example.entregaya.service;

import com.example.entregaya.builder.TareaBuilder;
import com.example.entregaya.decorator.TareaDecoratorFactory;
import com.example.entregaya.decorator.TareaInfo;
import com.example.entregaya.dto.TareaConEtiquetaDTO;
import com.example.entregaya.dto.TareaEventoDTO;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.observer.TareaObserver;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.stereotype.Service;
import com.example.entregaya.strategy.Lideroeditorstrategy;
import com.example.entregaya.service.CustomTrabajoDetailsService;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomTareaDetailsService {

    private final TareaRepository tareaRepository;
    private final TrabajoRepository trabajoRepository;
    private final UserRepository userRepository;
    private final CustomTrabajoDetailsService customTrabajoDetailsService;
    private final Lideroeditorstrategy lideroeditorstrategy;

    // ── Lista de observers (patrón Observer) ──
    private final List<TareaObserver> observers = new ArrayList<>();

    /**
     * Spring inyecta automáticamente todos los beans que implementen TareaObserver.
     * Agregar o quitar observers NO requiere modificar este servicio (CA5).
     */
    public CustomTareaDetailsService(TareaRepository tareaRepository,
                                     TrabajoRepository trabajoRepository,
                                     UserRepository userRepository,
                                     CustomTrabajoDetailsService customTrabajoDetailsService,
                                     Lideroeditorstrategy lideroeditorstrategy) {
        this.tareaRepository = tareaRepository;
        this.trabajoRepository = trabajoRepository;
        this.userRepository = userRepository;
        this.customTrabajoDetailsService = customTrabajoDetailsService;
        this.lideroeditorstrategy = lideroeditorstrategy;
    }

    // ── Gestión de observers ──

    public void registrarObserver(TareaObserver observer) {
        observers.add(observer);
    }

    public void eliminarObserver(TareaObserver observer) {
        observers.remove(observer);
    }

    /** Notifica a todos los observers registrados con el evento dado. */
    private void notificarObservers(TareaEventoDTO evento) {
        for (TareaObserver observer : observers) {
            observer.actualizar(evento);
        }
    }

    // ── Lógica de negocio ──

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

    /**
     * HU-29 (#316): Lista de tareas de un trabajo con etiquetas de urgencia.
     * 
     * Usa el patrón Decorator para añadir etiquetas dinámicas (Normal, Próxima, 
     * Urgente, Vencida, Sin fecha, Completada) según la fecha final de cada tarea.
     * 
     * @param trabajoId ID del trabajo
     * @return Lista de TareaConEtiquetaDTO con etiquetas de urgencia
     */
    public List<TareaConEtiquetaDTO> tareasConEtiquetas(Long trabajoId) {
        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajoId);
        
        return tareas.stream()
                .map(TareaDecoratorFactory::resolver)  // Aplica decorador según fecha
                .map(TareaConEtiquetaDTO::fromTareaInfo)  // Convierte a DTO
                .collect(Collectors.toList());
    }

    /**
     * HU-29 (#317): Obtiene una tarea individual con etiqueta de urgencia.
     * 
     * @param tareaId ID de la tarea
     * @return TareaConEtiquetaDTO con etiqueta de urgencia aplicada
     */
    public TareaConEtiquetaDTO findByIdConEtiqueta(Long tareaId) {
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("tarea no encontrado"));
        
        TareaInfo tareaInfo = TareaDecoratorFactory.resolver(tarea);
        return TareaConEtiquetaDTO.fromTareaInfo(tareaInfo);
    }


    public Tarea findById(Long tareaId) {
        return tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("tarea no encontrado"));
    }

    public void eliminar (long Id) {
        tareaRepository.deleteById(Id);
    }

    /**
     * Alterna el estado completada/pendiente de una tarea y dispara
     * el evento Observer a todos los observers registrados (CA1).
     *
     * @param tareaId     ID de la tarea a alternar
     * @param realizadoPor username del usuario que realiza el cambio (CA2)
     */
    public void toggleCompletada(Long tareaId, String realizadoPor) {
        Tarea tarea = findById(tareaId);
        boolean nuevoEstado = !tarea.getIsCompletada();
        tarea.setCompletada(nuevoEstado);
        tareaRepository.save(tarea);

        // Construir y disparar el evento
        TareaEventoDTO evento = new TareaEventoDTO(
                tarea.getId(),
                tarea.getNombre(),
                tarea.getTrabajo().getId(),
                tarea.getTrabajo().getNombreTrabajo(),
                nuevoEstado ? TareaEventoDTO.TipoEvento.COMPLETADA : TareaEventoDTO.TipoEvento.INCOMPLETADA,
                null,  // detalles
                realizadoPor,
                LocalDateTime.now()
        );
        notificarObservers(evento);
    }

    /**
     * Sobrecarga de compatibilidad para llamadas existentes sin username.
     * Usa "sistema" como realizadoPor.
     */
    public void toggleCompletada(Long tareaId) {
        toggleCompletada(tareaId, "sistema");
    }

    public void actualizarResponsables(Long tareaId, List<Long> responsableIds) {
        Tarea tarea = findById(tareaId);
        if (responsableIds != null && !responsableIds.isEmpty()) {
            Set<User> responsables = new HashSet<>(userRepository.findAllById(responsableIds));
            tarea.setResponsables(responsables);
        } else {
            tarea.setResponsables(new HashSet<>());
        }
        tareaRepository.save(tarea);
    }

    // Calcular el progreso de un trabajo teniendo en cuenta las dificultades de las tareas
    public int calcularProgreso(Long trabajoId) {
        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajoId);
        if (tareas.isEmpty()) return 0;
        int pesoTotal      = tareas.stream().mapToInt(t -> t.getDificultad().getPeso()).sum();
        int pesoCompletado = tareas.stream().filter(Tarea::getIsCompletada)
                                            .mapToInt(t -> t.getDificultad().getPeso()).sum();
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

    /**
     * Clona una tarea existente dentro del mismo trabajo.
     * Solo LIDER o EDITOR pueden clonar.
     *
     * @param tareaId  id de la tarea a clonar
     * @param trabajoId id del trabajo contenedor
     * @param username usuario autenticado que solicita la clonación
     * @return la nueva Tarea persistida
     * @throws SecurityException si el usuario no tiene rol LIDER o EDITOR
     */
    @Transactional
    public Tarea clonarTarea(Long tareaId, Long trabajoId, String username) {
        if (!customTrabajoDetailsService.verificarPermiso(trabajoId, username, lideroeditorstrategy)) {
            throw new SecurityException("Solo LIDER o EDITOR pueden clonar tareas.");
        }
        Tarea original = findById(tareaId);
        Trabajo trabajo = trabajoRepository.findById(trabajoId).orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));
        Tarea clon = original.clonar(trabajo);
        return tareaRepository.save(clon);
    }
}
