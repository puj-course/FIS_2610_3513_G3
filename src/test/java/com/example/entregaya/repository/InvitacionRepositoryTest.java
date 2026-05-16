package com.example.entregaya.repository;

import com.example.entregaya.model.Invitacion;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("InvitacionRepository - Tests con H2 y JUnit 5")
class InvitacionRepositoryTest {

    @Autowired private InvitacionRepository invitacionRepository;
    @Autowired private TrabajoRepository trabajoRepository;
    @Autowired private UserRepository userRepository;

    private User remitente;
    private User destinatario;
    private Trabajo trabajo;

    @BeforeEach
    void setUp() {
        invitacionRepository.deleteAll();
        trabajoRepository.deleteAll();
        userRepository.deleteAll();

        remitente = new User();
        remitente.setUsername("repo-invit-remitente");
        remitente.setPassword("password123");
        remitente.setEmail("repo-invit-remitente@example.com");
        remitente = userRepository.save(remitente);

        destinatario = new User();
        destinatario.setUsername("repo-invit-destinatario");
        destinatario.setPassword("password123");
        destinatario.setEmail("repo-invit-destinatario@example.com");
        destinatario = userRepository.save(destinatario);

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Proyecto Invitaciones");
        trabajo.setDescripcion("Tests de invitaciones");
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoRepository.save(trabajo);
    }

    private Invitacion crear(Invitacion.Estado estado) {
        Invitacion inv = new Invitacion();
        inv.setTrabajo(trabajo);
        inv.setRemitente(remitente);
        inv.setDestinatario(destinatario);
        inv.setEstado(estado);
        return invitacionRepository.save(inv);
    }

    @Test
    @DisplayName("CP01: findPendientesPorDestinatario retorna solo las del estado solicitado")
    void CP01_findPendientesPorDestinatario_FiltraPorEstado() {
        crear(Invitacion.Estado.PENDIENTE);
        crear(Invitacion.Estado.ACEPTADA);

        List<Invitacion> resultado = invitacionRepository.findPendientesPorDestinatario(
                "repo-invit-destinatario", Invitacion.Estado.PENDIENTE);

        assertEquals(1, resultado.size());
        assertEquals(Invitacion.Estado.PENDIENTE, resultado.get(0).getEstado());
    }

    @Test
    @DisplayName("CP02: findPorTrabajo retorna todas las invitaciones de un trabajo")
    void CP02_findPorTrabajo_RetornaTodas() {
        crear(Invitacion.Estado.PENDIENTE);
        crear(Invitacion.Estado.ACEPTADA);
        crear(Invitacion.Estado.RECHAZADA);

        List<Invitacion> resultado = invitacionRepository.findPorTrabajo(trabajo.getId());

        assertEquals(3, resultado.size());
    }

    @Test
    @DisplayName("CP03: findPendiente retorna la invitación específica cuando existe")
    void CP03_findPendiente_Existente_RetornaInvitacion() {
        crear(Invitacion.Estado.PENDIENTE);

        Optional<Invitacion> resultado = invitacionRepository.findPendiente(
                trabajo.getId(), "repo-invit-destinatario", Invitacion.Estado.PENDIENTE);

        assertTrue(resultado.isPresent());
        assertEquals(trabajo.getId(), resultado.get().getTrabajo().getId());
    }

    @Test
    @DisplayName("CP04: findPendiente retorna Optional vacío cuando el estado no coincide")
    void CP04_findPendiente_EstadoDiferente_RetornaVacio() {
        crear(Invitacion.Estado.ACEPTADA);

        Optional<Invitacion> resultado = invitacionRepository.findPendiente(
                trabajo.getId(), "repo-invit-destinatario", Invitacion.Estado.PENDIENTE);

        assertTrue(resultado.isEmpty());
    }
}
