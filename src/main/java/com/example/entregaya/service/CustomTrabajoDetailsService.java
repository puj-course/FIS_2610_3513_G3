package com.example.entregaya.service;

import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomTrabajoDetailsService {
    private final  UserRepository userRepository;
    private final TrabajoRepository trabajoRepository;

    public CustomTrabajoDetailsService(TrabajoRepository trabajoRepository, UserRepository userRepository){
        this.userRepository = userRepository;
        this.trabajoRepository = trabajoRepository;
    }
 // Crear un nuevo trabajo y agregar al creador como colaborador
    public Trabajo crearTrabajo(Trabajo trabajo, String username){
        User user= userRepository.findByUsername(username)
                .orElseThrow(()-> new RuntimeException("User not found"));
        trabajo.agregarColaborador(user);
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
    public void eliminar( long Id){
        trabajoRepository.deleteById(Id);
    }

}
