package com.example.entregaya.controller;

import com.example.entregaya.service.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

/**
 * Tests adicionales de PerfilController para cubrir el endpoint /perfil/actualizar-telegram
 * (no cubierto por PerfilControllerTest, ~40% de cobertura en código nuevo).
 */
@DisplayName("PerfilController - Cobertura adicional (endpoint Telegram)")
class PerfilControllerTelegramTest {

    private PerfilController perfilController;
    private CustomUserDetailsService userDetailsService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        perfilController = new PerfilController(userDetailsService);
        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser").password("pass").roles("USER").build();
    }

    @Test
    @DisplayName("CP01: actualizarTelegram exitoso redirige a /perfil con successTelegram")
    void CP01_actualizarTelegram_Exitoso_RedireccionaAPerfil_ConFlashSuccess() {
        RedirectAttributes ra = new RedirectAttributesModelMap();

        String resultado = perfilController.actualizarTelegram("123456789", userDetails, ra);

        Assertions.assertEquals("redirect:/perfil", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("successTelegram"));
        Mockito.verify(userDetailsService).actualizarTelegramChatId("testuser", "123456789");
    }

    @Test
    @DisplayName("CP02: actualizarTelegram con IllegalArgumentException redirige con errorTelegram")
    void CP02_actualizarTelegram_ConIllegalArgument_RedireccionaConErrorFlash() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("Usuario no encontrado"))
                .when(userDetailsService).actualizarTelegramChatId("testuser", "999");

        String resultado = perfilController.actualizarTelegram("999", userDetails, ra);

        Assertions.assertEquals("redirect:/perfil", resultado);
        Assertions.assertEquals("Usuario no encontrado",
                ra.getFlashAttributes().get("errorTelegram"));
    }

    @Test
    @DisplayName("CP03: actualizarTelegram con string vacío delega al servicio (que lo limpia)")
    void CP03_actualizarTelegram_ConStringVacio_DelegaAlServicio() {
        RedirectAttributes ra = new RedirectAttributesModelMap();

        String resultado = perfilController.actualizarTelegram("", userDetails, ra);

        Assertions.assertEquals("redirect:/perfil", resultado);
        Mockito.verify(userDetailsService).actualizarTelegramChatId("testuser", "");
    }
}
