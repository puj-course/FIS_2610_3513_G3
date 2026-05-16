package com.example.entregaya.service;

import com.example.entregaya.model.User;
import com.example.entregaya.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests adicionales para cerrar huecos de cobertura en CustomUserDetailsService:
 *  - actualizarTelegramChatId (todas las ramas: valor válido, blank, null, usuario no existente)
 *  - findByUsername (wrapper público)
 *  - Ramas null/empty no exercitadas en actualizarUsername y actualizarEmail
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomUserDetailsService - Cobertura adicional (Telegram + ramas null/empty)")
class CustomUserDetailsServiceTelegramTest {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("CP01: actualizarTelegramChatId con valor válido lo guarda recortado")
    void CP01_actualizarTelegramChatId_ConValor_GuardaRecortado() {
        userDetailsService.register("tg-user-1", "password123", "tg1@test.com");

        userDetailsService.actualizarTelegramChatId("tg-user-1", "  123456789  ");

        User user = userRepository.findByUsername("tg-user-1").orElseThrow();
        assertEquals("123456789", user.getTelegramChatId());
    }

    @Test
    @DisplayName("CP02: actualizarTelegramChatId con null deja el campo en null")
    void CP02_actualizarTelegramChatId_ConNull_DejaNull() {
        userDetailsService.register("tg-user-2", "password123", "tg2@test.com");

        userDetailsService.actualizarTelegramChatId("tg-user-2", null);

        User user = userRepository.findByUsername("tg-user-2").orElseThrow();
        assertNull(user.getTelegramChatId());
    }

    @Test
    @DisplayName("CP03: actualizarTelegramChatId con string en blanco deja el campo en null")
    void CP03_actualizarTelegramChatId_ConBlanco_DejaNull() {
        userDetailsService.register("tg-user-3", "password123", "tg3@test.com");
        userDetailsService.actualizarTelegramChatId("tg-user-3", "555");

        // Ahora limpio con blank
        userDetailsService.actualizarTelegramChatId("tg-user-3", "   ");

        User user = userRepository.findByUsername("tg-user-3").orElseThrow();
        assertNull(user.getTelegramChatId());
    }

    @Test
    @DisplayName("CP04: actualizarTelegramChatId con usuario inexistente lanza IllegalArgumentException")
    void CP04_actualizarTelegramChatId_UsuarioInexistente_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
                userDetailsService.actualizarTelegramChatId("noexiste-jamas", "12345"));
    }

    @Test
    @DisplayName("CP05: findByUsername wrapper retorna Optional con valor cuando existe")
    void CP05_findByUsername_Existente_RetornaOptionalConValor() {
        userDetailsService.register("tg-user-5", "password123", "tg5@test.com");

        Optional<User> resultado = userDetailsService.findByUsername("tg-user-5");

        assertTrue(resultado.isPresent());
        assertEquals("tg5@test.com", resultado.get().getEmail());
    }

    @Test
    @DisplayName("CP06: findByUsername wrapper retorna Optional vacío cuando no existe")
    void CP06_findByUsername_Inexistente_RetornaOptionalVacio() {
        Optional<User> resultado = userDetailsService.findByUsername("noexiste-jamas-2");

        assertTrue(resultado.isEmpty());
    }

    // ---- Ramas null/empty en actualizarUsername (no cubiertas por test existente) ----

    @Test
    @DisplayName("CP07: actualizarUsername con null lanza IllegalArgumentException")
    void CP07_actualizarUsername_ConNull_LanzaExcepcion() {
        userDetailsService.register("tg-user-7", "password123", "tg7@test.com");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userDetailsService.actualizarUsername("tg-user-7", null));
        assertTrue(ex.getMessage().toLowerCase().contains("vac"));
    }

    @Test
    @DisplayName("CP08: actualizarUsername con string en blanco lanza IllegalArgumentException")
    void CP08_actualizarUsername_ConBlanco_LanzaExcepcion() {
        userDetailsService.register("tg-user-8", "password123", "tg8@test.com");

        assertThrows(IllegalArgumentException.class, () ->
                userDetailsService.actualizarUsername("tg-user-8", "   "));
    }

    // ---- Ramas null/empty/formato en actualizarEmail (no cubiertas) ----

    @Test
    @DisplayName("CP09: actualizarEmail con null lanza IllegalArgumentException")
    void CP09_actualizarEmail_ConNull_LanzaExcepcion() {
        userDetailsService.register("tg-user-9", "password123", "tg9@test.com");

        assertThrows(IllegalArgumentException.class, () ->
                userDetailsService.actualizarEmail("tg-user-9", null));
    }

    @Test
    @DisplayName("CP10: actualizarEmail con formato inválido lanza IllegalArgumentException")
    void CP10_actualizarEmail_FormatoInvalido_LanzaExcepcion() {
        userDetailsService.register("tg-user-10", "password123", "tg10@test.com");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                userDetailsService.actualizarEmail("tg-user-10", "no-es-email"));
        assertTrue(ex.getMessage().toLowerCase().contains("formato"));
    }
}
