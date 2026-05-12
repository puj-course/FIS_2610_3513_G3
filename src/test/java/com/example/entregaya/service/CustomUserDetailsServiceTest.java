package com.example.entregaya.service;

import com.example.entregaya.model.User;
import com.example.entregaya.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("CustomUserDetailsService - Tests de integración")
class CustomUserDetailsServiceTest {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    // CP01 - NORMAL: Registrar usuario con datos válidos
    @Test
    @DisplayName("CP01: register con datos válidos crea el usuario correctamente")
    void CP01_register_ConDatosValidos_CreaUsuario() {
        User result = userDetailsService.register("alice", "password123", "alice@test.com");

        assertNotNull(result.getId());
        assertEquals("alice", result.getUsername());
        assertNotEquals("password123", result.getPassword()); // debe estar encriptada
    }

    // CP02 - NEGATIVA: Registrar usuario duplicado lanza excepción
    @Test
    @DisplayName("CP02: register con username duplicado lanza IllegalArgumentException")
    void CP02_register_ConUsernameDuplicado_LanzaExcepcion() {
        userDetailsService.register("bob", "password123", "bob@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.register("bob", "password456", "bob2@test.com")
        );
    }

    // CP03 - NEGATIVA: Contraseña menor a 6 caracteres lanza excepción
    @Test
    @DisplayName("CP03: register con contraseña corta lanza IllegalArgumentException")
    void CP03_register_ConPasswordCorta_LanzaExcepcion() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.register("carol", "abc", "carol@test.com")
        );
        assertTrue(ex.getMessage().contains("6"));
    }

    // CP04 - NEGATIVA: Email inválido lanza excepción
    @Test
    @DisplayName("CP04: register con email inválido lanza IllegalArgumentException")
    void CP04_register_ConEmailInvalido_LanzaExcepcion() {
        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.register("david", "password123", "emailinvalido")
        );
    }

    // CP05 - NEGATIVA: Email duplicado lanza excepción
    @Test
    @DisplayName("CP05: register con email duplicado lanza IllegalArgumentException")
    void CP05_register_ConEmailDuplicado_LanzaExcepcion() {
        userDetailsService.register("eve", "password123", "shared@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.register("eve2", "password123", "shared@test.com")
        );
    }

    // CP06 - NORMAL: loadUserByUsername retorna UserDetails
    @Test
    @DisplayName("CP06: loadUserByUsername con usuario existente retorna UserDetails")
    void CP06_loadUserByUsername_ConUsuarioExistente_RetornaUserDetails() {
        userDetailsService.register("frank", "password123", "frank@test.com");

        UserDetails details = userDetailsService.loadUserByUsername("frank");

        assertNotNull(details);
        assertEquals("frank", details.getUsername());
    }

    // CP07 - NEGATIVA: loadUserByUsername con usuario inexistente lanza excepción
    @Test
    @DisplayName("CP07: loadUserByUsername con usuario inexistente lanza UsernameNotFoundException")
    void CP07_loadUserByUsername_ConUsuarioInexistente_LanzaExcepcion() {
        assertThrows(UsernameNotFoundException.class, () ->
            userDetailsService.loadUserByUsername("noexiste")
        );
    }

    // CP08 - NORMAL: actualizarUsername con nuevo nombre válido funciona
    @Test
    @DisplayName("CP08: actualizarUsername con nombre válido actualiza el usuario")
    void CP08_actualizarUsername_ConNombreValido_ActualizaUsuario() {
        userDetailsService.register("grace", "password123", "grace@test.com");

        userDetailsService.actualizarUsername("grace", "grace_nueva");

        assertTrue(userRepository.findByUsername("grace_nueva").isPresent());
        assertFalse(userRepository.findByUsername("grace").isPresent());
    }

    // CP09 - NEGATIVA: actualizarUsername con mismo nombre lanza excepción
    @Test
    @DisplayName("CP09: actualizarUsername con mismo nombre lanza IllegalArgumentException")
    void CP09_actualizarUsername_ConMismoNombre_LanzaExcepcion() {
        userDetailsService.register("henry", "password123", "henry@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarUsername("henry", "henry")
        );
    }

    // CP10 - NEGATIVA: actualizarUsername con nombre ya en uso lanza excepción
    @Test
    @DisplayName("CP10: actualizarUsername con nombre ya en uso lanza IllegalArgumentException")
    void CP10_actualizarUsername_ConNombreEnUso_LanzaExcepcion() {
        userDetailsService.register("ivan", "password123", "ivan@test.com");
        userDetailsService.register("julia", "password123", "julia@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarUsername("ivan", "julia")
        );
    }

    // CP11 - NORMAL: actualizarPassword con datos válidos actualiza la contraseña
    @Test
    @DisplayName("CP11: actualizarPassword con contraseña válida funciona correctamente")
    void CP11_actualizarPassword_ConDatosValidos_ActualizaPassword() {
        userDetailsService.register("kevin", "oldpass123", "kevin@test.com");

        assertDoesNotThrow(() ->
            userDetailsService.actualizarPassword("kevin", "oldpass123", "newpass456", "newpass456")
        );

        // Verificar que la nueva contraseña funciona
        UserDetails details = userDetailsService.loadUserByUsername("kevin");
        assertNotNull(details);
    }

    // CP12 - NEGATIVA: actualizarPassword con contraseña actual incorrecta lanza excepción
    @Test
    @DisplayName("CP12: actualizarPassword con contraseña actual incorrecta lanza excepción")
    void CP12_actualizarPassword_ConPasswordActualIncorrecta_LanzaExcepcion() {
        userDetailsService.register("laura", "password123", "laura@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarPassword("laura", "wrongpass", "newpass456", "newpass456")
        );
    }

    // CP13 - NEGATIVA: actualizarPassword con confirmación que no coincide lanza excepción
    @Test
    @DisplayName("CP13: actualizarPassword con confirmación diferente lanza excepción")
    void CP13_actualizarPassword_ConConfirmacionDiferente_LanzaExcepcion() {
        userDetailsService.register("mike", "password123", "mike@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarPassword("mike", "password123", "newpass456", "different789")
        );
    }

    // CP14 - NEGATIVA: actualizarPassword con nueva igual a la actual lanza excepción
    @Test
    @DisplayName("CP14: actualizarPassword con nueva igual a actual lanza excepción")
    void CP14_actualizarPassword_ConNuevaIgualActual_LanzaExcepcion() {
        userDetailsService.register("nancy", "password123", "nancy@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarPassword("nancy", "password123", "password123", "password123")
        );
    }

    // CP15 - NORMAL: actualizarEmail con email válido actualiza el correo
    @Test
    @DisplayName("CP15: actualizarEmail con email válido actualiza el correo")
    void CP15_actualizarEmail_ConEmailValido_ActualizaEmail() {
        userDetailsService.register("oscar", "password123", "oscar@test.com");

        userDetailsService.actualizarEmail("oscar", "oscar_new@test.com");

        User user = userRepository.findByUsername("oscar").orElseThrow();
        assertEquals("oscar_new@test.com", user.getEmail());
    }

    // CP16 - NEGATIVA: actualizarEmail con email igual al actual lanza excepción
    @Test
    @DisplayName("CP16: actualizarEmail con mismo email lanza IllegalArgumentException")
    void CP16_actualizarEmail_ConMismoEmail_LanzaExcepcion() {
        userDetailsService.register("peter", "password123", "peter@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarEmail("peter", "peter@test.com")
        );
    }

    // CP17 - NEGATIVA: actualizarEmail con email ya registrado lanza excepción
    @Test
    @DisplayName("CP17: actualizarEmail con email ya en uso lanza IllegalArgumentException")
    void CP17_actualizarEmail_ConEmailEnUso_LanzaExcepcion() {
        userDetailsService.register("quinn", "password123", "quinn@test.com");
        userDetailsService.register("rose", "password123", "rose@test.com");

        assertThrows(IllegalArgumentException.class, () ->
            userDetailsService.actualizarEmail("quinn", "rose@test.com")
        );
    }
}
