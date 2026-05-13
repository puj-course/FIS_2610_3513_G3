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
@DisplayName("CustomTrabajoDetailsService - Tests adicionales de cobertura")
class CustomTrabajoDetailsServiceExtraTest {

    @Autowired
    private CustomTrabajoDetailsService trabajoService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    private User lider;
    private User colaborador;

    @BeforeEach
    void setUp() {
        userDetailsService.register("lider_xt", "pass123456", "lider_xt@test.com");
        userDetailsService.register("colab_xt", "pass123456", "colab_xt@test.com");
        lider = userRepository.findByUsername("lider_xt").orElseThrow();
        colaborador = userRepository.findByUsername("colab_xt").orElseThrow();
    }

    // CP01 - NEGATIVA: cambiarRol en trabajo inexistente lanza excepción
    @Test
    @DisplayName("CP01: cambiarRol en trabajo inexistente lanza IllegalArgumentException")
    void CP01_cambiarRol_TrabajoInexistente_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            trabajoService.cambiarRol(99999L, lider.getId(), ColaboradorTrabajo.Rol.EDITOR)
        );
    }

    // CP02 - NEGATIVA: eliminarColaborador por no-lider lanza excepción
    @Test
    @DisplayName("CP02: eliminarColaborador por no-lider lanza IllegalArgumentException")
    void CP02_eliminarColaborador_PorNoLider_LanzaExcepcion() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Trabajo Permisos " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, "lider_xt");
        trabajoService.agregarColaborador(guardado.getId(), "colab_xt");

        assertThrows(IllegalArgumentException.class, () ->
            trabajoService.eliminarColaborador(guardado.getId(), lider.getId(), "colab_xt")
        );
    }

    // CP03 - NEGATIVA: actualizarTrabajo con nombre vacío lanza excepción
    @Test
    @DisplayName("CP03: actualizarTrabajo con nombre vacío lanza IllegalArgumentException")
    void CP03_actualizarTrabajo_ConNombreVacio_LanzaExcepcion() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Original " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, "lider_xt");

        Trabajo editado = new Trabajo();
        editado.setNombreTrabajo("   ");

        assertThrows(IllegalArgumentException.class, () ->
            trabajoService.actualizarTrabajo(guardado.getId(), editado)
        );
    }

    // CP04 - NEGATIVA: actualizarTrabajo con id inexistente lanza excepción
    @Test
    @DisplayName("CP04: actualizarTrabajo con id inexistente lanza IllegalArgumentException")
    void CP04_actualizarTrabajo_ConIdInexistente_LanzaExcepcion() {
        Trabajo editado = new Trabajo();
        editado.setNombreTrabajo("Nuevo nombre");

        assertThrows(IllegalArgumentException.class, () ->
            trabajoService.actualizarTrabajo(99999L, editado)
        );
    }

    // CP05 - NORMAL: obtenerHistorial retorna lista (vacía si no hay eventos visibles en la transacción)
    @Test
    @DisplayName("CP05: obtenerHistorial con trabajo existente no lanza excepción")
    void CP05_obtenerHistorial_ConTrabajoExistente_NoLanzaExcepcion() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Historial Test " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, "lider_xt");

        assertDoesNotThrow(() -> trabajoService.obtenerHistorial(guardado.getId()));
    }

    // CP06 - NEGATIVA: obtenerHistorial con trabajo inexistente lanza excepción
    @Test
    @DisplayName("CP06: obtenerHistorial con trabajo inexistente lanza IllegalArgumentException")
    void CP06_obtenerHistorial_ConTrabajoInexistente_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            trabajoService.obtenerHistorial(99999L)
        );
    }

    // CP07 - NORMAL: consultarMiembros retorna los miembros correctamente
    @Test
    @DisplayName("CP07: consultarMiembros retorna lista de miembros con roles")
    void CP07_consultarMiembros_RetornaMiembrosConRoles() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Miembros Test " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, "lider_xt");
        trabajoService.agregarColaborador(guardado.getId(), "colab_xt");

        List<com.example.entregaya.dto.MiembroRolDTO> miembros =
            trabajoService.consultarMiembros(guardado.getId());

        assertEquals(2, miembros.size());
    }

    // CP08 - NEGATIVA: consultarMiembros con trabajo inexistente lanza excepción
    @Test
    @DisplayName("CP08: consultarMiembros con trabajo inexistente lanza RuntimeException")
    void CP08_consultarMiembros_ConTrabajoInexistente_LanzaExcepcion() {
        assertThrows(RuntimeException.class, () ->
            trabajoService.consultarMiembros(99999L)
        );
    }

    // CP09 - NORMAL: verificarPermiso con trabajo inexistente retorna false
    @Test
    @DisplayName("CP09: verificarPermiso con trabajo inexistente retorna false")
    void CP09_verificarPermiso_ConTrabajoInexistente_RetornaFalse() {
        boolean resultado = trabajoService.verificarPermiso(
            99999L, "lider_xt", c -> true
        );
        assertFalse(resultado);
    }

    // CP10 - NORMAL: puedeEditarTarea (deprecated) funciona para LIDER
    @Test
    @DisplayName("CP10: puedeEditarTarea retorna true para LIDER")
    void CP10_puedeEditarTarea_ParaLider_RetornaTrue() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Tarea Edit " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, "lider_xt");

        assertTrue(trabajoService.puedeEditarTarea(guardado.getId(), "lider_xt"));
    }

    // CP11 - NORMAL: esLider (deprecated) retorna true para lider
    @Test
    @DisplayName("CP11: esLider retorna true para usuario LIDER")
    void CP11_esLider_ParaLider_RetornaTrue() {
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Lider Test " + System.nanoTime());
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, "lider_xt");

        assertTrue(trabajoService.esLider(guardado.getId(), "lider_xt"));
        assertFalse(trabajoService.esLider(guardado.getId(), "colab_xt"));
    }

    // CP12 - NORMAL: clonar trabajo con nombre duplicado agrega sufijo numérico
    @Test
    @DisplayName("CP12: clonarTrabajo con nombre ya existente agrega sufijo numérico")
    void CP12_clonarTrabajo_ConNombreDuplicado_AgregaSufijo() {
        long ts = System.nanoTime();
        Trabajo trabajo = new Trabajo();
        trabajo.setNombreTrabajo("Duplicado " + ts);
        trabajo.setFechaInicio(LocalDateTime.now());
        trabajo.setFechaEntrega(LocalDateTime.now().plusDays(30));
        Trabajo guardado = trabajoService.crearTrabajo(trabajo, "lider_xt");

        // Primer clon
        Trabajo clon1 = trabajoService.clonarTrabajo(guardado.getId(), "lider_xt");
        // Segundo clon (el nombre del primero ya existe, debe agregar sufijo)
        Trabajo clon2 = trabajoService.clonarTrabajo(guardado.getId(), "lider_xt");

        assertNotEquals(clon1.getNombreTrabajo(), clon2.getNombreTrabajo());
    }
}
