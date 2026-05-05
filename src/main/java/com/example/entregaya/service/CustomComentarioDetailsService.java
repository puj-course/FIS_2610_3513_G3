package com.example.entregaya.service;

import com.example.entregaya.model.Comentario;
import com.example.entregaya.model.HistorialEvento;
import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.ComentarioRepository;
import com.example.entregaya.repository.HistorialEventoRepository;
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
    private final HistorialEventoRepository historialEventoRepository;

    public CustomComentarioDetailsService(ComentarioRepository comentarioRepository, TareaRepository tareaRepository,
                                          UserRepository userRepository,
                                          HistorialEventoRepository historialEventoRepository) {
        this.comentarioRepository = comentarioRepository;
        this.tareaRepository = tareaRepository;
        this.userRepository = userRepository;
        this.historialEventoRepository = historialEventoRepository;
    }

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

    public List<Comentario> obtenerComentariosPorTarea(Long tareaId) {
        return comentarioRepository.findByTareaIdOrderByFechaCreacionDesc(tareaId);
    }

    public Comentario obtenerComentarioPorId(Long comentarioId) {
        return comentarioRepository.findById(comentarioId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));
    }

    public void eliminarComentario(Long comentarioId, String username) {
        Comentario comentario = obtenerComentarioPorId(comentarioId);
        if (!comentario.getAutor().getUsername().equals(username)) {
            throw new IllegalArgumentException("No tienes permiso para eliminar este comentario.");
        }
        Tarea tarea = comentario.getTarea();
        HistorialEvento evento = new HistorialEvento(
                tarea.getTrabajo(),
                HistorialEvento.TipoEvento.COMENTARIO_ELIMINADO,
                "Comentario eliminado en la tarea \"" + tarea.getNombre() + "\"",
                "Contenido eliminado: " + comentario.getContenido(),
                username,
                LocalDateTime.now(),
                tarea
        );
        historialEventoRepository.save(evento);
        comentarioRepository.deleteById(comentarioId);
    }

    public Comentario editarComentario(Long comentarioId, String nuevoContenido, String username) {
        Comentario comentario = obtenerComentarioPorId(comentarioId);
        if (!comentario.getAutor().getUsername().equals(username)) {
            throw new IllegalArgumentException("No tienes permiso para editar este comentario.");
        }

        String contenidoAnterior = comentario.getContenido();
        comentario.setContenido(nuevoContenido);
        Comentario guardado = comentarioRepository.save(comentario);

        Tarea tarea = comentario.getTarea();
        HistorialEvento evento = new HistorialEvento(
                tarea.getTrabajo(),
                HistorialEvento.TipoEvento.COMENTARIO_EDITADO,
                "Comentario editado en la tarea \"" + tarea.getNombre() + "\"",
                "Antes: " + contenidoAnterior + " → Después: " + nuevoContenido,
                username,
                LocalDateTime.now(),
                tarea
        );
        historialEventoRepository.save(evento);

        return guardado;
    }

    /** Sobrecarga de compatibilidad para eliminar sin verificación (uso interno). */
    public void eliminarComentario(Long comentarioId) {
        comentarioRepository.deleteById(comentarioId);
    }

    /** Sobrecarga de compatibilidad para editar sin verificación (uso interno). */
    public Comentario editarComentario(Long comentarioId, String nuevoContenido) {
        Comentario comentario = obtenerComentarioPorId(comentarioId);
        comentario.setContenido(nuevoContenido);
        return comentarioRepository.save(comentario);
    }
}