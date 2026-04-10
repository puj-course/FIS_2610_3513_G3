package com.example.entregaya.service;

import com.example.entregaya.dto.MiembroRolDTO;
import com.example.entregaya.dto.TrabajoEventoDTO;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.ColaboradorTrabajoId;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.observer.TrabajoObserver;
import com.example.entregaya.repository.ColaboradorTrabajoRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import com.example.entregaya.strategy.Permisostrategy;
import com.example.entregaya.strategy.Sololiderstrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CustomTrabajoDetailsService {
    private final UserRepository userRepository;
    private final TrabajoRepository trabajoRepository;
    private final ColaboradorTrabajoRepository colaboradorTrabajoRepository;
    private final Sololiderstrategy sololiderstrategy;

    // ── Lista de observers para eventos de trabajo ──
    private final List<TrabajoObserver> observers = new ArrayList<>();

    public CustomTrabajoDetailsService(TrabajoRepository trabajoRepository,
                                       UserRepository userRepository,
                                       ColaboradorTrabajoRepository colaboradorTrabajoRepository,
                                       Sololiderstrategy sololiderstrategy,
                                       List<TrabajoObserver> observers) {
        this.userRepository = userRepository;
        this.trabajoRepository = trabajoRepository;
        this.colaboradorTrabajoRepository = colaboradorTrabajoRepository;
        this.sololiderstrategy = sololiderstrategy;
        this.observers.addAll(observers);  // Spring inyecta todos los TrabajoObserver
    }

    // ── Gestión de observers ──

    public void registrarObserver(TrabajoObserver observer) {
        observers.add(observer);
    }

    public void eliminarObserver(TrabajoObserver observer) {
        observers.remove(observer);
    }

    /** Notifica a todos los observers registrados */
    private void notificarObservers(TrabajoEventoDTO evento) {
        for (TrabajoObserver observer : observers) {
            observer.actualizar(evento);
        }
    }

    // ── Métodos existentes (sin cambios importantes) ──

    public Trabajo crearTrabajo(Trabajo trabajo, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        trabajo.agregarColaborador(user, ColaboradorTrabajo.Rol.LIDER);
        return trabajoRepository.save(trabajo);
    }

    public List<Trabajo> listarPorUsuario(String username) {
        return trabajoRepository.findByColaboradoresUsername(username);
    }

    public Trabajo obtenerPorId(Long id) {
        return trabajoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));
    }

    /**
     * HU-28: Agregar un colaborador a un trabajo.
     * Dispara evento TrabajoEventoDTO con tipoEvento = INGRESO
     */
    @Transactional
    public Trabajo agregarColaborador(Long trabajoId, String username) {
        Trabajo trabajo = obtenerPorId(trabajoId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        trabajo.agregarColaborador(user);
        Trabajo trabajoGuardado = trabajoRepository.save(trabajo);

        // HU-28: Disparar evento de ingreso
        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                trabajoId,
                trabajo.getNombreTrabajo(),
                TrabajoEventoDTO.TipoEvento.INGRESO,
                username,
                username,  // El usuario que se unió es quien ejecuta la acción
                LocalDateTime.now()
        );
        notificarObservers(evento);

        return trabajoGuardado;
    }

    @Transactional
    public void cambiarRol(Long trabajoId, Long userId, ColaboradorTrabajo.Rol nuevoRol) {
        if (!trabajoRepository.existsById(trabajoId)) {
            throw new IllegalArgumentException("El trabajo no existe");
        }
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }
        ColaboradorTrabajoId id = new ColaboradorTrabajoId(trabajoId, userId);
        ColaboradorTrabajo colaborador = colaboradorTrabajoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no pertenece a este trabajo"));
        colaborador.setRol(nuevoRol);
        colaboradorTrabajoRepository.save(colaborador);
    }

    public List<MiembroRolDTO> consultarMiembros(Long trabajoId) {
        if (!trabajoRepository.existsById(trabajoId)) {
            throw new RuntimeException("Trabajo no encontrado");
        }
        return colaboradorTrabajoRepository.findMiembrosConRol(trabajoId)
                .stream().map(ct -> new MiembroRolDTO(
                        ct.getUser().getId(),
                        ct.getUser().getUsername(),
                        ct.getRol()))
                .toList();
    }

    public boolean verificarPermiso(Long trabajoId, String username, Permisostrategy strategy) {
        return trabajoRepository.findById(trabajoId)
                .map(trabajo -> trabajo.getColaboradores().stream()
                        .filter(c -> c.getUser().getUsername().equals(username))
                        .anyMatch(strategy::tienePermiso))
                .orElse(false);
    }

    public void eliminar(long Id) {
        trabajoRepository.deleteById(Id);
    }

    /**
     * HU-28: Elimina un colaborador y dispara evento TrabajoEventoDTO con tipoEvento = SALIDA
     */
    @Transactional
    public void eliminarColaborador(Long trabajoId, Long userId, String usernameQuienEjecuta) {
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new IllegalArgumentException("El trabajo no existe"));

        if (!verificarPermiso(trabajoId, usernameQuienEjecuta, sololiderstrategy))
            throw new IllegalArgumentException("Solo un lider puede eliminar un colaborador");

        User ejecutor = userRepository.findByUsername(usernameQuienEjecuta)
                .orElseThrow(() -> new IllegalArgumentException("Usuario ejecutor no encontrado"));

        if (ejecutor.getId().equals(userId)) {
            throw new IllegalArgumentException("No puedes eliminarte a ti mismo del trabajo");
        }

        ColaboradorTrabajoId targetId = new ColaboradorTrabajoId(trabajoId, userId);
        ColaboradorTrabajo target = colaboradorTrabajoRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("El colaborador no pertenece a este trabajo"));

        if (target.getRol() == ColaboradorTrabajo.Rol.LIDER) {
            long cantidadLideres = trabajo.getColaboradores().stream()
                    .filter(c -> c.getRol() == ColaboradorTrabajo.Rol.LIDER)
                    .count();
            if (cantidadLideres <= 1) {
                throw new IllegalArgumentException("No se puede eliminar al único líder del trabajo");
            }
        }

        // Obtener username del usuario a eliminar
        String usernameEliminado = target.getUser().getUsername();

        colaboradorTrabajoRepository.delete(target);

        // HU-28: Disparar evento de salida
        TrabajoEventoDTO evento = new TrabajoEventoDTO(
                trabajoId,
                trabajo.getNombreTrabajo(),
                TrabajoEventoDTO.TipoEvento.SALIDA,
                usernameEliminado,
                usernameQuienEjecuta,
                LocalDateTime.now()
        );
        notificarObservers(evento);
    }
    /**
     * @deprecated
     *
     * Este metodo quedo obsoleto usar verificarPermiso(id, username, new SoloLiderStrategy()) en su luagr
     *
    */
    @Deprecated
    public boolean puedeEditarTarea(Long trabajoId, String username) {
        return verificarPermiso(trabajoId,username,
                colaborador -> colaborador.getRol() == ColaboradorTrabajo.Rol.EDITOR
                        || colaborador.getRol() == ColaboradorTrabajo.Rol.LIDER);
    }

    /**
     * @deprecated
     *
     *  Este metodo quedo obsoleto usar verificarPermiso(id, username, new SoloLiderStrategy()) en su luagr
     */
    @Deprecated
    public boolean esLider(Long trabajoId, String username) {
        return verificarPermiso(trabajoId,username,sololiderstrategy);
    }

    // HU-15: Actualizar un trabajo existente (solo nombre, descripción y fechas)
    /**
     * Actualiza los datos editables de un trabajo existente.
     * Solo se pueden editar: nombre, descripción, fechaInicio y fechaEntrega.
     * No se modifican los colaboradores ni las tareas.
     * 
     * @param id ID del trabajo a actualizar
     * @param trabajoEditado Objeto con los nuevos valores
     * @throws IllegalArgumentException si el trabajo no existe
     * @throws IllegalArgumentException si el nombre está vacío
     * @throws IllegalArgumentException si el nombre ya existe en otro trabajo
     */
    @Transactional
    public void actualizarTrabajo(Long id, Trabajo trabajoEditado) {
        // Obtener el trabajo actual desde la BD
        Trabajo trabajoExistente = trabajoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El trabajo no existe"));
        
        // Validación 1: El nombre no puede estar vacío
        if (trabajoEditado.getNombreTrabajo() == null || trabajoEditado.getNombreTrabajo().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del trabajo no puede estar vacío");
        }
        
        // Validación 2: El nombre no puede estar duplicado (excepto si no cambió)
        String nuevoNombre = trabajoEditado.getNombreTrabajo().trim();
        if (!trabajoExistente.getNombreTrabajo().equals(nuevoNombre)) {
            // Solo validar si el nombre cambió
            boolean nombreExiste = trabajoRepository.existsByNombreTrabajo(nuevoNombre);
            if (nombreExiste) {
                throw new IllegalArgumentException("Ya existe un trabajo con ese nombre");
            }
        }
        
        // Actualizar solo los campos editables (no tocar colaboradores ni tareas)
        trabajoExistente.setNombreTrabajo(nuevoNombre);
        trabajoExistente.setDescripcion(trabajoEditado.getDescripcion());
        trabajoExistente.setFechaInicio(trabajoEditado.getFechaInicio());
        trabajoExistente.setFechaEntrega(trabajoEditado.getFechaEntrega());
        
        // Guardar cambios en la BD
        trabajoRepository.save(trabajoExistente);
    }


}