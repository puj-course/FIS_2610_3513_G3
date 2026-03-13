package com.example.entregaya.service;

import com.example.entregaya.model.Invitacion;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import com.example.entregaya.repository.InvitacionRepository;
import com.example.entregaya.repository.TrabajoRepository;
import com.example.entregaya.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomInvitacionDetailsService {
    private final InvitacionRepository invitacionRepository;
    private final TrabajoRepository trabajoRepository;
    private final UserRepository userRepository;

    public CustomInvitacionDetailsService(InvitacionRepository invitacionRepository, TrabajoRepository trabajoRepository, UserRepository userRepository) {
        this.invitacionRepository = invitacionRepository;
        this.trabajoRepository = trabajoRepository;
        this.userRepository = userRepository;
    }

    public Invitacion enviarInvitacion(Long trabajoId, String remitenteUsername, String destinatarioUsername) {
        Trabajo trabajo = trabajoRepository.findById(trabajoId)
                .orElseThrow(() -> new RuntimeException("Trabajo no encontrado"));
        User remitente = userRepository.findByUsername(remitenteUsername)
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        User destinatario = userRepository.findByUsername(destinatarioUsername)
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        boolean colabora = trabajo.getColaboradores().stream()
                .anyMatch(c -> c.getUser().equals(destinatarioUsername));
        if (colabora) {
            throw new RuntimeException("El usuario ya colabora en este trabajo");
        }

        invitacionRepository.findPendiente(trabajoId, destinatarioUsername, Invitacion.Estado.PENDIENTE)
                .ifPresent(i -> { throw new IllegalArgumentException("Ya existe una invitacion pendiente para este usuario"); });

        Invitacion invitacion = new Invitacion();
        invitacion.setTrabajo(trabajo);
        invitacion.setRemitente(remitente);
        invitacion.setDestinatario(destinatario);
        invitacion.setEstado(Invitacion.Estado.PENDIENTE);
        return invitacionRepository.save(invitacion);
    }

    public List<Invitacion> pendientesParaUsuario(String username) {
        return invitacionRepository.findPendientesPorDestinatario(username, Invitacion.Estado.PENDIENTE);
    }

    public List<Invitacion> porTrabajo(Long trabajoId) {
        return invitacionRepository.findPorTrabajo(trabajoId);
    }

    public void aceptar(Long invitacionId, String username) {
        Invitacion inv = findAndValidate(invitacionId, username);
        inv.setEstado(Invitacion.Estado.ACEPTADA);
        inv.getTrabajo().agregarColaborador(inv.getDestinatario());
        trabajoRepository.save(inv.getTrabajo());
        invitacionRepository.save(inv);
    }

    public void rechazar(Long invitacionId, String username) {
        Invitacion inv = findAndValidate(invitacionId, username);
        inv.setEstado(Invitacion.Estado.RECHAZADA);
        invitacionRepository.save(inv);
    }

    private Invitacion findAndValidate(Long invitacionId, String username) {
        Invitacion inv = invitacionRepository.findById(invitacionId)
                .orElseThrow(() -> new RuntimeException("Invitacion no encontrada"));
        if (!inv.getDestinatario().getUsername().equals(username)) {
            throw new RuntimeException("No tienes permiso para responder esta invitación");
        }
        if (inv.getEstado() != Invitacion.Estado.PENDIENTE) {
            throw new IllegalArgumentException("Esta invitación ya fue respondida");
        }
        return inv;
    }
}