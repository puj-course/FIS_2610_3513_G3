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
import org.springframework.stereotype.Service;
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

    public void registrarObserver(TareaObserver observer) { observers.add(observer); }
    public void eliminarObserver(TareaObserver observer)  { observers.remove(observer); }

    private void notificarObservers(TareaEventoDTO evento) {
        for (TareaObserver observer : observers) observer.actualizar(evento);
    }

    // ── Validación de etiquetas (HU-40) ──

    /**
     * Valida y normaliza la lista de etiquetas:
     * - Máximo 5 etiquetas
     * - Cada etiqueta máximo 20 caracteres
     * - Elimina duplicados y vacíos
     *
     * @throws IllegalArgumentException si se viola alguna regla
     */
    public List<String> validarEtiquetas(List<String> etiquetas) {
        if (etiquetas == null) return new ArrayList<>();

        List<String> normalizadas = etiquetas.stream()
                .filter(e -> e != null && !e.isBlank())
                .map(String::trim)
                .distinct()
                .collect(Collectors.toList());

        for (String etiqueta : normalizadas) {
            if (etiqueta.length() > 20) {
                throw new IllegalArgumentException(
                        "La etiqueta \"" + etiqueta + "\" supera los 20 caracteres permitidos.");
            }
        }

        if (normalizadas.size() > 5) {
            throw new IllegalArgumentException(
                    "Se permiten máximo 5 etiquetas por tarea. Se enviaron " + normalizadas.size() + ".");
        }

        return normalizadas;
    }

    // ── Lógica de negocio ──

    /**
     * HU-40 (Task #402): Crear tarea con etiquetas.
     */
    public Tarea crearTarea(Tarea tarea, Long trabajoId,
                            List<Long> responsableIds, List<String> etiquetas) {
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("trabajo no encontrado"));

        // 2. Buscar responsables si se proporcionaron
        Set<User> responsables = new HashSet<>();
        if (responsableIds != null && !responsableIds.isEmpty()) {
            responsables = new HashSet<>(userRepository.findAllById(responsableIds));
        }

        Tarea tareaValidada = new TareaBuilder()
                .nombre(tarea.getNombre())
                .descripcion(tarea.getDescripcion())
                .fechaInicio(tarea.getFechaInicio())
                .fechaFinal(tarea.getFechaFinal())
                .dificultad(tarea.getDificultad())
                .trabajo(trabajo)
                .responsables(responsables)
                .build();

        tareaValidada.setEtiquetas(validarEtiquetas(etiquetas));

        return tareaRepository.save(tareaValidada);
    }

    public Tarea crearTarea(Tarea tarea, Long trabajoId, List<Long> responsableIds) {
        return crearTarea(tarea, trabajoId, responsableIds, null);
    }

    public Tarea crearTarea(Tarea tarea, Long trabajoId) {
        return crearTarea(tarea, trabajoId, null, null);
    }

    public List<Tarea> tareas(Long trabajoId) {
        return tareaRepository.findBytrabajoId(trabajoId);
    }

    /**
     * HU-40 (Task #404): Lista de tareas filtradas por etiqueta.
     */
    public List<Tarea> tareasPorEtiqueta(Long trabajoId, String etiqueta) {
        if (etiqueta == null || etiqueta.isBlank()) return tareas(trabajoId);
        return tareaRepository.findByTrabajoIdAndEtiqueta(trabajoId, etiqueta);
    }

    public List<TareaConEtiquetaDTO> tareasConEtiquetas(Long trabajoId) {
        return tareaRepository.findBytrabajoId(trabajoId).stream()
                .map(TareaDecoratorFactory::resolver)
                .map(TareaConEtiquetaDTO::fromTareaInfo)
                .collect(Collectors.toList());
    }

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

    public void eliminar(long id) { tareaRepository.deleteById(id); }

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

    public void toggleCompletada(Long tareaId) { toggleCompletada(tareaId, "sistema"); }

    public void actualizarResponsables(Long tareaId, List<Long> responsableIds) {
        Tarea tarea = findById(tareaId);
        if (responsableIds != null && !responsableIds.isEmpty()) {
            tarea.setResponsables(new HashSet<>(userRepository.findAllById(responsableIds)));
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
     * HU-40 (Task 402): Editar tarea con etiquetas.
     */
    public Tarea editarTarea(Long tareaId, Tarea tareaActualizada,
                             List<Long> responsableIds, List<String> etiquetas) {
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
                .trabajo(tareaExistente.getTrabajo())
                .responsables(responsables)
                .build();

        tareaExistente.setNombre(tareaValidada.getNombre());
        tareaExistente.setDescripcion(tareaValidada.getDescripcion());
        tareaExistente.setFechaInicio(tareaValidada.getFechaInicio());
        tareaExistente.setFechaFinal(tareaValidada.getFechaFinal());
        tareaExistente.setDificultad(tareaValidada.getDificultad());
        tareaExistente.setResponsables(tareaValidada.getResponsables());
        tareaExistente.setEtiquetas(validarEtiquetas(etiquetas));

        return tareaRepository.save(tareaExistente);
    }

    /** Sobrecarga de compatibilidad sin etiquetas. */
    public Tarea editarTarea(Long tareaId, Tarea tareaActualizada, List<Long> responsableIds) {
        return editarTarea(tareaId, tareaActualizada, responsableIds, null);
    }

    @Transactional
    public Tarea clonarTarea(Long tareaId, Long trabajoId, String username) {
        if (!customTrabajoDetailsService.verificarPermiso(trabajoId, username, lideroeditorstrategy)) {
            throw new SecurityException("Solo LIDER o EDITOR pueden clonar tareas.");
        }
        Tarea original = findById(tareaId);
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));
        return tareaRepository.save(original.clonar(trabajo));
    }
}
