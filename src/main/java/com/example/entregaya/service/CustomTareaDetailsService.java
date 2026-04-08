package com.example.entregaya.service;

import com.example.entregaya.builder.TareaBuilder;
import com.example.entregaya.dto.TareaEventoDTO;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.observer.TareaObserver;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CustomTareaDetailsService {

    private final TareaRepository tareaRepository;
    private final TrabajoRepository trabajoRepository;
    private final UserRepository userRepository;

    // ── Lista de observers (patrón Observer) ──
    private final List<TareaObserver> observers = new ArrayList<>();

    /**
     * Spring inyecta automáticamente todos los beans que implementen TareaObserver.
     * Agregar o quitar observers NO requiere modificar este servicio (CA5).
     */
    public CustomTareaDetailsService(TareaRepository tareaRepository,
                                     TrabajoRepository trabajoRepository,
                                     UserRepository userRepository,
                                     List<TareaObserver> observers) {
        this.tareaRepository  = tareaRepository;
        this.trabajoRepository = trabajoRepository;
        this.userRepository   = userRepository;
        this.observers.addAll(observers);
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
     */
    public Tarea crearTarea(Tarea tarea, Long trabajoId, List<Long> responsableIds) {
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("trabajo no encontrado"));

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

        return tareaRepository.save(tareaValidada);
    }

    public Tarea crearTarea(Tarea tarea, Long trabajoId) {
        return crearTarea(tarea, trabajoId, null);
    }

    /** Lista de tareas de un trabajo. */
    public List<Tarea> tareas(Long trabajoId) {
        return tareaRepository.findBytrabajoId(trabajoId);
    }

    public Tarea findById(Long tareaId) {
        return tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("tarea no encontrado"));
    }

    public void eliminar(long id) {
        tareaRepository.deleteById(id);
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
                nuevoEstado ? "COMPLETADA" : "PENDIENTE",
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

    /** Calcula el progreso de un trabajo ponderado por dificultad de tareas. */
    public int calcularProgreso(Long trabajoId) {
        List<Tarea> tareas = tareaRepository.findBytrabajoId(trabajoId);
        if (tareas.isEmpty()) return 0;
        int pesoTotal      = tareas.stream().mapToInt(t -> t.getDificultad().getPeso()).sum();
        int pesoCompletado = tareas.stream().filter(Tarea::getIsCompletada)
                                            .mapToInt(t -> t.getDificultad().getPeso()).sum();
        return (int) Math.round((pesoCompletado * 100.0) / pesoTotal);
    }

    public Tarea editarTarea(Long tareaId, Tarea tareaActualizada, List<Long> responsableIds) {
        Tarea tarea = findById(tareaId);
        tarea.setNombre(tareaActualizada.getNombre());
        tarea.setDescripcion(tareaActualizada.getDescripcion());
        tarea.setFechaInicio(tareaActualizada.getFechaInicio());
        tarea.setFechaFinal(tareaActualizada.getFechaFinal());
        tarea.setDificultad(tareaActualizada.getDificultad());
        if (responsableIds != null && !responsableIds.isEmpty()) {
            Set<User> responsables = new HashSet<>(userRepository.findAllById(responsableIds));
            tarea.setResponsables(responsables);
        } else {
            tarea.setResponsables(new HashSet<>());
        }
        return tareaRepository.save(tarea);
    }
}