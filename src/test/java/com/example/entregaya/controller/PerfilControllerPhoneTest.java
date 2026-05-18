package com.example.entregaya.controller;

import com.example.entregaya.model.User;
import com.example.entregaya.service.CustomUserDetailsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import java.util.Optional;

/**
 * Tests unitarios para los endpoints nuevos de PerfilController:
 * - GET /perfil (atributo phoneNumberActual)
 * - POST /perfil/actualizar-telefono
 *
 * Complementa PerfilControllerTest que ya cubre username, password y email.
 */
@DisplayName("PerfilController - Teléfono SMS (Twilio)")
class PerfilControllerPhoneTest {

    private PerfilController perfilController;
    private CustomUserDetailsService userDetailsService;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        userDetailsService = Mockito.mock(CustomUserDetailsService.class);
        perfilController   = new PerfilController(userDetailsService);

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("pass")
                .roles("USER")
                .build();
    }

    // CP01 - NORMAL: perfil() carga phoneNumberActual en el modelo
    @Test
    @DisplayName("CP01: perfil() agrega phoneNumberActual al modelo cuando el usuario tiene teléfono")
    void CP01_perfil_ConPhone_AgregaPhoneActualAlModelo() {
        Model model = new ExtendedModelMap();
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPhoneNumber("+573001234567");

        Mockito.when(userDetailsService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        String vista = perfilController.perfil(userDetails, model);

        Assertions.assertEquals("perfil", vista);
        Assertions.assertEquals("+573001234567", model.asMap().get("phoneNumberActual"),
                "El modelo debe contener el phoneNumber del usuario");
    }

    // CP02 - BORDE: perfil() con usuario sin teléfono, phoneNumberActual es null
    @Test
    @DisplayName("CP02: perfil() con usuario sin teléfono phoneNumberActual es null")
    void CP02_perfil_SinPhone_PhoneActualEsNull() {
        Model model = new ExtendedModelMap();
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@test.com");
        user.setPhoneNumber(null);

        Mockito.when(userDetailsService.findByUsername("testuser"))
                .thenReturn(Optional.of(user));

        perfilController.perfil(userDetails, model);

        Assertions.assertNull(model.asMap().get("phoneNumberActual"),
                "phoneNumberActual debe ser null si el usuario no tiene teléfono");
    }

    // CP03 - NORMAL: actualizarTelefono() exitoso redirige a /perfil con successPhone
    @Test
    @DisplayName("CP03: actualizarTelefono() exitoso redirige a /perfil con flash successPhone")
    void CP03_actualizarTelefono_Exitoso_RedireccionaConSuccessPhone() {
        RedirectAttributes ra = new RedirectAttributesModelMap();

        String resultado = perfilController.actualizarTelefono("+573001234567", userDetails, ra);

        Assertions.assertEquals("redirect:/perfil", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("successPhone"),
                "Debe existir el atributo flash 'successPhone'");
        Mockito.verify(userDetailsService)
                .actualizarPhoneNumber("testuser", "+573001234567");
    }

    // CP04 - NEGATIVA: actualizarTelefono() con formato inválido redirige con errorPhone
    @Test
    @DisplayName("CP04: actualizarTelefono() con formato inválido agrega flash errorPhone")
    void CP04_actualizarTelefono_FormatoInvalido_RedireccionaConErrorPhone() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("El número debe estar en formato E.164"))
                .when(userDetailsService).actualizarPhoneNumber("testuser", "numeroinvalido");

        String resultado = perfilController.actualizarTelefono("numeroinvalido", userDetails, ra);

        Assertions.assertEquals("redirect:/perfil", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorPhone"),
                "Debe existir el atributo flash 'errorPhone'");
    }

    // CP05 - NORMAL: actualizarTelefono() con null limpia el teléfono sin error
    @Test
    @DisplayName("CP05: actualizarTelefono() con string vacío redirige con successPhone")
    void CP05_actualizarTelefono_Vacio_RedireccionaConSuccess() {
        RedirectAttributes ra = new RedirectAttributesModelMap();

        String resultado = perfilController.actualizarTelefono("", userDetails, ra);

        Assertions.assertEquals("redirect:/perfil", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("successPhone"),
                "Limpiar el teléfono también debe producir flash 'successPhone'");
    }

    // CP06 - NEGATIVA: actualizarTelefono() con usuario inexistente redirige con errorPhone
    @Test
    @DisplayName("CP06: actualizarTelefono() con usuario inexistente agrega flash errorPhone")
    void CP06_actualizarTelefono_UsuarioInexistente_RedireccionaConError() {
        RedirectAttributes ra = new RedirectAttributesModelMap();
        Mockito.doThrow(new IllegalArgumentException("Usuario no encontrado"))
                .when(userDetailsService).actualizarPhoneNumber("testuser", "+573001234567");

        String resultado = perfilController.actualizarTelefono("+573001234567", userDetails, ra);

        Assertions.assertEquals("redirect:/perfil", resultado);
        Assertions.assertNotNull(ra.getFlashAttributes().get("errorPhone"));
    }
}
