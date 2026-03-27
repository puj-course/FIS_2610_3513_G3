package com.example.entregaya.service;

import com.example.entregaya.model.Comentario;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.ComentarioRepository;
import com.example.entregaya.repository.TareaRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomComentarioDetailsService {

    private final ComentarioRepository comentarioRepository;
    private final TareaRepository tareaRepository;
    private final UserRepository userRepository;

    public CustomComentarioDetailsService(ComentarioRepository comentarioRepository,
                                          TareaRepository tareaRepository,
                                          UserRepository userRepository) {
        this.comentarioRepository = comentarioRepository;
        this.tareaRepository = tareaRepository;
        this.userRepository = userRepository;
    }

    // Crear un nuevo comentario
    public Comentario crearComentario(Long tareaId, Long userId, String contenido) {
        Tarea tarea = tareaRepository.findById(tareaId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        User autor = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Comentario comentario = new Comentario();
        comentario.setContenido(contenido);
        comentario.setTarea(tarea);
        comentario.setAutor(autor);
        comentario.setFechaCreacion(LocalDateTime.now());

        return comentarioRepository.save(comentario);
    }

    // Obtener comentarios de una tarea (ordenados por fecha)
    public List<Comentario> obtenerComentariosPorTarea(Long tareaId) {
        return comentarioRepository.findByTareaIdOrderByFechaCreacionDesc(tareaId);
    }

    // Obtener un comentario por ID
    public Comentario obtenerComentarioPorId(Long comentarioId) {
        return comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
    }

    // Eliminar un comentario
    public void eliminarComentario(Long comentarioId) {
        comentarioRepository.deleteById(comentarioId);
    }

    // Editar un comentario
    public Comentario editarComentario(Long comentarioId, String nuevoContenido) {
        Comentario comentario = obtenerComentarioPorId(comentarioId);
        comentario.setContenido(nuevoContenido);
        return comentarioRepository.save(comentario);
    }
}
