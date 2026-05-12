package com.example.entregaya.service;

import com.example.entregaya.model.*;
import com.example.entregaya.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomInvitacionDetailsService - Tests de integración")
class CustomInvitacionDetailsServiceTest {

    @Autowired
    private CustomInvitacionDetailsService invitacionService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomTrabajoDetailsService trabajoService;

    @Autowired
    private UserRepository userRepository;

    private Trabajo trabajo;
    private User lider;
    private User invitado;

    @BeforeEach
    void setUp() {
        userDetailsService.register("lider_inv", "pass123456", "lider_inv@test.com");
        userDetailsService.register("invitado_inv", "pass123456", "invitado_inv@test.com");

        lider = userRepository.findByUsername("lider_inv").orElseThrow();
        invitado = userRepository.findByUsername("invitado_inv").orElseThrow();

        trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Trabajo Invitaciones " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        trabajo = trabajoService.crearTrabajo(trabajo, "lider_inv");
    }

    // CP01 - NORMAL: Enviar invitación válida
    @Test
    @DisplayName("CP01: enviarInvitacion con datos válidos crea la invitación")
    void CP01_enviarInvitacion_ConDatosValidos_CreaInvitacion() {
        Invitacion resultado = invitacionService.enviarInvitacion(
            trabajo.getId(), "lider_inv", "invitado_inv"
        );

        assertNotNull(resultado.getId());
        assertEquals(Invitacion.Estado.PENDIENTE, resultado.getEstado());
        assertEquals("invitado_inv", resultado.getDestinatario().getUsername());
    }

    // CP02 - NEGATIVA: Enviar invitación a usuario que ya está en el trabajo
    @Test
    @DisplayName("CP02: enviarInvitacion a colaborador existente lanza RuntimeException")
    void CP02_enviarInvitacion_AColaboradorExistente_LanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "lider_inv")
        );
    }

    // CP03 - NEGATIVA: Enviar invitación duplicada lanza excepción
    @Test
    @DisplayName("CP03: enviarInvitacion duplicada lanza IllegalArgumentException")
    void CP03_enviarInvitacion_Duplicada_LanzaExcepcion() {
        invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv");

        assertThrows(IllegalArgumentException.class, () ->
            invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv")
        );
    }

    // CP04 - NEGATIVA: Enviar invitación a usuario inexistente lanza excepción
    @Test
    @DisplayName("CP04: enviarInvitacion a usuario inexistente lanza IllegalArgumentException")
    void CP04_enviarInvitacion_AUsuarioInexistente_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "usuariofantasma")
        );
    }

    // CP05 - NORMAL: Listar invitaciones pendientes para un usuario
    @Test
    @DisplayName("CP05: pendientesParaUsuario retorna las invitaciones pendientes")
    void CP05_pendientesParaUsuario_RetornaInvitacionesPendientes() {
        invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv");

        List<Invitacion> pendientes = invitacionService.pendientesParaUsuario("invitado_inv");

        assertEquals(1, pendientes.size());
        assertEquals(Invitacion.Estado.PENDIENTE, pendientes.get(0).getEstado());
    }

    // CP06 - NORMAL: Aceptar invitación agrega al usuario al trabajo
    @Test
    @DisplayName("CP06: aceptar invitación agrega al usuario al trabajo")
    void CP06_aceptar_AgregaUsuarioAlTrabajo() {
        Invitacion inv = invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv");

        invitacionService.aceptar(inv.getId(), "invitado_inv");

        List<Trabajo> trabajos = trabajoService.listarPorUsuario("invitado_inv");
        assertEquals(1, trabajos.size());
    }

    // CP07 - NEGATIVA: Aceptar invitación de otro usuario lanza excepción
    @Test
    @DisplayName("CP07: aceptar invitación con usuario incorrecto lanza RuntimeException")
    void CP07_aceptar_ConUsuarioIncorrecto_LanzaExcepcion() {
        Invitacion inv = invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv");

        assertThrows(RuntimeException.class, () ->
            invitacionService.aceptar(inv.getId(), "lider_inv")
        );
    }

    // CP08 - NORMAL: Rechazar invitación cambia estado a RECHAZADA
    @Test
    @DisplayName("CP08: rechazar invitación cambia estado a RECHAZADA")
    void CP08_rechazar_CambiaEstadoARechazada() {
        Invitacion inv = invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv");

        invitacionService.rechazar(inv.getId(), "invitado_inv");

        // Verificar que no aparece más en pendientes
        List<Invitacion> pendientes = invitacionService.pendientesParaUsuario("invitado_inv");
        assertEquals(0, pendientes.size());
    }

    // CP09 - NORMAL: Cancelar invitación pendiente funciona
    @Test
    @DisplayName("CP09: cancelar invitación pendiente retorna trabajoId")
    void CP09_cancelar_InvitacionPendiente_RetornaTrabajoid() {
        Invitacion inv = invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv");

        Long trabajoId = invitacionService.cancelar(inv.getId(), "lider_inv");

        assertEquals(trabajo.getId(), trabajoId);
    }

    // CP10 - NORMAL: porTrabajo retorna todas las invitaciones del trabajo
    @Test
    @DisplayName("CP10: porTrabajo retorna las invitaciones asociadas al trabajo")
    void CP10_porTrabajo_RetornaInvitacionesDelTrabajo() {
        invitacionService.enviarInvitacion(trabajo.getId(), "lider_inv", "invitado_inv");

        List<Invitacion> invitaciones = invitacionService.porTrabajo(trabajo.getId());

        assertEquals(1, invitaciones.size());
    }
}
