package com.example.entregaya.service;

import com.example.entregaya.model.User;
import com.example.entregaya.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomUserDetailsService - actualizarPhoneNumber (Twilio SMS)")
class CustomUserDetailsServicePhoneTest {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userDetailsService.register("testuser", "password123", "testuser@test.com");
    }

    // CP01 - NORMAL: número E.164 válido se guarda correctamente
    @Test
    @DisplayName("CP01: actualizarPhoneNumber con número E.164 válido lo guarda")
    void CP01_actualizarPhoneNumber_NumeroValido_GuardaCorrectamente() {
        userDetailsService.actualizarPhoneNumber("testuser", "+573001234567");

        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertEquals("+573001234567", user.getPhoneNumber());
    }

    // CP02 - NORMAL: número null limpia el campo (permite desactivar SMS)
    @Test
    @DisplayName("CP02: actualizarPhoneNumber con null limpia el campo phoneNumber")
    void CP02_actualizarPhoneNumber_ConNull_LimpiaCampo() {
        // Primero asignar uno
        userDetailsService.actualizarPhoneNumber("testuser", "+573001234567");
        // Luego limpiar
        userDetailsService.actualizarPhoneNumber("testuser", null);

        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertNull(user.getPhoneNumber());
    }

    // CP03 - BORDE: string en blanco también limpia el campo
    @Test
    @DisplayName("CP03: actualizarPhoneNumber con string en blanco limpia el campo")
    void CP03_actualizarPhoneNumber_EnBlanco_LimpiaCampo() {
        userDetailsService.actualizarPhoneNumber("testuser", "+573001234567");
        userDetailsService.actualizarPhoneNumber("testuser", "   ");

        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertNull(user.getPhoneNumber());
    }

    // CP04 - NEGATIVA: número sin '+' lanza excepción
    @Test
    @DisplayName("CP04: actualizarPhoneNumber sin '+' lanza IllegalArgumentException")
    void CP04_actualizarPhoneNumber_SinMas_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarPhoneNumber("testuser", "573001234567")
        );
    }

    // CP05 - NEGATIVA: número con letras lanza excepción
    @Test
    @DisplayName("CP05: actualizarPhoneNumber con letras lanza IllegalArgumentException")
    void CP05_actualizarPhoneNumber_ConLetras_LanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarPhoneNumber("testuser", "+57ABC1234567")
        );
        assertTrue(ex.getMessage().contains("E.164"));
    }

    // CP06 - NEGATIVA: número demasiado corto lanza excepción
    @Test
    @DisplayName("CP06: actualizarPhoneNumber con número muy corto lanza IllegalArgumentException")
    void CP06_actualizarPhoneNumber_NumeroCorto_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarPhoneNumber("testuser", "+123")
        );
    }

    // CP07 - NEGATIVA: usuario inexistente lanza excepción
    @Test
    @DisplayName("CP07: actualizarPhoneNumber con usuario inexistente lanza IllegalArgumentException")
    void CP07_actualizarPhoneNumber_UsuarioInexistente_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarPhoneNumber("noexiste", "+573001234567")
        );
    }

    // CP08 - BORDE: número con exactamente 8 dígitos (límite inferior válido E.164)
    @Test
    @DisplayName("CP08: actualizarPhoneNumber con número de 8 dígitos es válido")
    void CP08_actualizarPhoneNumber_OchoDigitos_EsValido() {
        assertDoesNotThrow(() ->
            userDetailsService.actualizarPhoneNumber("testuser", "+12345678")
        );
        User user = userRepository.findByUsername("testuser").orElseThrow();
        assertEquals("+12345678", user.getPhoneNumber());
    }
}
