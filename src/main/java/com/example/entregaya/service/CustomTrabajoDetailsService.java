package com.example.entregaya.service;

import com.example.entregaya.dto.MiembroRolDTO;
import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.ColaboradorTrabajoId;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.ColaboradorTrabajoRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class CustomTrabajoDetailsService {
    private final  UserRepository userRepository;
    private final TrabajoRepository trabajoRepository;
    private final ColaboradorTrabajoRepository colaboradorTrabajoRepository;

    public CustomTrabajoDetailsService(TrabajoRepository trabajoRepository, UserRepository userRepository, ColaboradorTrabajoRepository colaboradorTrabajoRepository){
        this.userRepository = userRepository;
        this.trabajoRepository = trabajoRepository;
        this.colaboradorTrabajoRepository = colaboradorTrabajoRepository;
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

    // Agregar un colaborador a un trabajo existente
    public Trabajo agregarColaborador(Long trabajoId, String username) {
        Trabajo trabajo = obtenerPorId(trabajoId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        trabajo.agregarColaborador(user);
        return trabajoRepository.save(trabajo);
    }

    @Transactional
    public void cambiarRol(Long trabajoId, Long userId, ColaboradorTrabajo.Rol nuevoRol){
        // Validar que el trabajo exista
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

    public void eliminar(long Id){
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

        // Validar que quien ejecuta la acción sea LIDER
        User ejecutor = userRepository.findByUsername(usernameQuienEjecuta)
                .orElseThrow(() -> new IllegalArgumentException("Usuario ejecutor no encontrado"));

        ColaboradorTrabajoId ejecutorId = new ColaboradorTrabajoId(trabajoId, ejecutor.getId());
        ColaboradorTrabajo ejecutorCol = colaboradorTrabajoRepository.findById(ejecutorId)
                .orElseThrow(() -> new IllegalArgumentException("No perteneces a este trabajo"));

        if (ejecutorCol.getRol() != ColaboradorTrabajo.Rol.LIDER) {
            throw new IllegalArgumentException("Solo un líder puede eliminar colaboradores");
        }

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

        colaboradorTrabajoRepository.delete(target);
    }
}