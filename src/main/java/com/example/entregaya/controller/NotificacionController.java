package com.example.entregaya.controller;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.NotificacionRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST para gestión de notificaciones.
 * DoD D7: endpoint PATCH /notificaciones/{id}/leer
 */
@RestController
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
     * Marca una notificación como leída.
     * Solo el destinatario puede marcar su propia notificación (CA4).
     *
     * @return 200 OK si se actualizó, 403 si no es el destinatario, 404 si no existe
     */
    @PatchMapping("/{id}/leer")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        return notificacionRepository.findById(id).map(notificacion -> {
            // Verificar que el usuario autenticado sea el destinatario
            if (!notificacion.getDestinatario().getUsername().equals(userDetails.getUsername())) {
                return ResponseEntity.<Void>status(403).build();
            }
            notificacion.setLeida(true);
            notificacionRepository.save(notificacion);
            return ResponseEntity.<Void>ok().build();
        }).orElse(ResponseEntity.<Void>notFound().build());
    }
}