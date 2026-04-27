package com.example.entregaya.service;

import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.repository.HistorialEventoRepository;
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
public class CustomTrabajoDetailsService{
    private final UserRepository userRepository;
    private final TrabajoRepository trabajoRepository;
    private final ColaboradorTrabajoRepository colaboradorTrabajoRepository;
    private final Sololiderstrategy sololiderstrategy;
    // ── Lista de observers para eventos de trabajo ──
    private final List<TrabajoObserver> observers = new ArrayList<>();
    private final HistorialEventoRepository historialEventoRepository;

    public CustomTrabajoDetailsService(TrabajoRepository trabajoRepository,
                                       UserRepository userRepository,
                                       ColaboradorTrabajoRepository colaboradorTrabajoRepository,
                                       Sololiderstrategy sololiderstrategy,
                                       List<TrabajoObserver> observers,
                                       HistorialEventoRepository historialEventoRepository) {
        this.userRepository = userRepository;
        this.trabajoRepository = trabajoRepository;
        this.colaboradorTrabajoRepository = colaboradorTrabajoRepository;
        this.sololiderstrategy = sololiderstrategy;
        this.observers.addAll(observers);
        this.historialEventoRepository = historialEventoRepository;
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

 // Crear un nuevo trabajo y agregar al creador como colaborador
 // El creador del trabajo automaticamente lider
    public Trabajo crearTrabajo(Trabajo trabajo, String username){
        User user= userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("Usuario no encontrado"));
        trabajo.agregarColaborador(user, ColaboradorTrabajo.Rol.LIDER);
        return trabajoRepository.save(trabajo);
    }

    // Listar trabajos donde el usuario es colaborador
    public List<Trabajo> listarPorUsuario(String username) {
        return trabajoRepository.findByColaboradoresUsername(username);
    }

    // Obtener un trabajo por id
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

        // Validar que el rol sea válido (no nulo)
        if (nuevoRol == null) {
            throw new IllegalArgumentException("El rol no puede ser nulo");
        }

        // Validar que el usuario pertenezca al grupo
        ColaboradorTrabajoId id = new ColaboradorTrabajoId(trabajoId, userId);
        ColaboradorTrabajo colaborador = colaboradorTrabajoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no pertenece a este trabajo"));

        // Actualizar el rol en la base de datos
        colaborador.setRol(nuevoRol);
        colaboradorTrabajoRepository.save(colaborador);
    }

    // Consultar los miembros de un trabajo con sus roles, desde el DTO
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

    /**
     * Verifica si el usuario tiene permiso en el trabajo
     * según la estrategia recibida.
     *
     * Reemplaza los métodos duplicados esLider() y puedeEditarTarea().
     *
     * @param trabajoId id del trabajo a verificar
     * @param username  usuario autenticado
     * @param strategy  regla de permiso a aplicar (SoloLider, LiderOEditor, etc.)
     * @return true si el usuario cumple la regla
     */

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
     * Elimina un colaborador de un trabajo.
     * Solo un LIDER puede realizar esta acción.
     * No se puede eliminar al único LIDER del trabajo.
     */
    @Transactional
    public void eliminarColaborador(Long trabajoId, Long userId, String usernameQuienEjecuta) {
        // Validar que el trabajo exista
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new IllegalArgumentException("El trabajo no existe"));

        if(!verificarPermiso(trabajoId,usernameQuienEjecuta,sololiderstrategy))
            throw new IllegalArgumentException("Solo un lider puede elminar un colaborador");

        // Validar que quien ejecuta la acción sea LIDER
        User ejecutor = userRepository.findByUsername(usernameQuienEjecuta)
                .orElseThrow(() -> new IllegalArgumentException("Usuario ejecutor no encontrado"));

        // No permitir que el líder se elimine a sí mismo
        if (ejecutor.getId().equals(userId)) {
            throw new IllegalArgumentException("No puedes eliminarte a ti mismo del trabajo");
        }

        // Validar que el colaborador a eliminar exista en el trabajo
        ColaboradorTrabajoId targetId = new ColaboradorTrabajoId(trabajoId, userId);
        ColaboradorTrabajo target = colaboradorTrabajoRepository.findById(targetId)
                .orElseThrow(() -> new IllegalArgumentException("El colaborador no pertenece a este trabajo"));

        // No permitir eliminar al único LIDER
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
    /**
     *
     * Reglas:
     *  - Solo el LIDER del trabajo original puede clonar.
     *  - Si el nombre "<nombre> (copia)" ya existe, se agrega sufijo numérico
     *    hasta encontrar uno libre: "(copia)", "(copia) 2", "(copia) 3"...
     *  - El creador (username) queda como único miembro con rol LIDER.
     *
     * @param id id del trabajo a clonar
     * @param username  usuario autenticado que solicita la clonación
     * @return el nuevo Trabajo persistido
     * @throws IllegalArgumentException si el trabajo no existe
     * @throws SecurityException        si el usuario no es LIDER
     */
    @Transactional
    public Trabajo clonarTrabajo(Long id, String username) {
        Trabajo original = trabajoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El trabajo no existe"));
        if(!verificarPermiso(id,username,sololiderstrategy)) {
            throw new SecurityException("Solo el lider puede clonar el trabajo");
        }
        Trabajo copia = original.clonar();
        String nombreBase = copia.getNombreTrabajo();
        String nombreFinal = nombreBase;
        int sufijo = 2;
        while(trabajoRepository.existsByNombreTrabajo(nombreFinal)) {
            nombreFinal = nombreBase +" "+ sufijo;
            sufijo++;
        }
        copia.setNombreTrabajo(nombreFinal);
        User creador = userRepository.findByUsername(username)
                .orElseThrow((()-> new RuntimeException("Usuario no encontrado")));
        copia.agregarColaborador(creador,ColaboradorTrabajo.Rol.LIDER);

        return trabajoRepository.save(copia);
    }


    public List<HistorialEvento> obtenerHistorial(Long trabajoId) {
        // Validar que el trabajo exista
        if (!trabajoRepository.existsById(trabajoId)) {
            throw new IllegalArgumentException("El trabajo no existe");
        }
        return historialEventoRepository.findByTrabajoIdOrderByFechaDesc(trabajoId);
    }
}