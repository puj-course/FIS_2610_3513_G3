package com.example.entregaya.controller;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador para gestión de notificaciones.
 * GET  /notificaciones        → vista con todas las notificaciones del usuario
 * PATCH /notificaciones/{id}/leer → marca como leída (DoD D7)
 */
@Controller
@RequestMapping("/notificaciones")
public class NotificacionController {

    private final NotificacionRepository notificacionRepository;
    private final UserRepository userRepository;

    public NotificacionController(NotificacionRepository notificacionRepository,
                                  UserRepository userRepository) {
        this.notificacionRepository = notificacionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Muestra todas las notificaciones del usuario autenticado,
     * ordenadas de más reciente a más antigua.
     */
    @GetMapping
    public String verNotificaciones(Model model,
                                    @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        List<Notificacion> todas =
                notificacionRepository.findByDestinatarioOrderByFechaCreacionDesc(user);

        long noLeidas = todas.stream().filter(n -> !n.isLeida()).count();

        model.addAttribute("notificaciones", todas);
        model.addAttribute("noLeidas", noLeidas);

        return "notificaciones";
    }

    /**
     * Marca una notificación como leída (CA4, DoD D7).
     * Solo el destinatario puede marcarla.
     *
     * @return 200 OK, 403 si no es el destinatario, 404 si no existe.
     */
    @PatchMapping("/{id}/leer")
    @ResponseBody
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Notificacion> opcional = notificacionRepository.findById(id);

        if (opcional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Notificacion notificacion = opcional.get();

        if (!notificacion.getDestinatario().getUsername().equals(userDetails.getUsername())) {
            return ResponseEntity.status(403).build();
        }

        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
        return ResponseEntity.ok().build();
    }
}