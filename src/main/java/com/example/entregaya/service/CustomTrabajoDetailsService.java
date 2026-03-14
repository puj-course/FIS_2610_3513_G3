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

    public void cambiarRol(Long trabajoId, Long userId, ColaboradorTrabajo.Rol nuevoRol){

        ColaboradorTrabajoId id=new ColaboradorTrabajoId(trabajoId,userId );
        ColaboradorTrabajo colb = colaboradorTrabajoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colaborador no encontrado"));
        colb.setRol(nuevoRol);
        colaboradorTrabajoRepository.save(colb);
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

    public void eliminar( long Id){
        trabajoRepository.deleteById(Id);
    }

}
