package com.example.entregaya.repository;

import com.example.entregaya.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("UserRepository - Tests con H2 y JUnit 5")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    // Identificadores únicos por test class para evitar choque con otros tests
    private static final String UNAME = "repo-user-test";
    private static final String EMAIL = "repo-user-test@example.com";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User u = new User();
        u.setUsername(UNAME);
        u.setPassword("password123");
        u.setEmail(EMAIL);
        userRepository.save(u);
    }

    @Test
    @DisplayName("CP01: findByUsername con usuario existente retorna Optional con valor")
    void CP01_findByUsername_Existente_RetornaUsuario() {
        Optional<User> resultado = userRepository.findByUsername(UNAME);

        assertTrue(resultado.isPresent());
        assertEquals(EMAIL, resultado.get().getEmail());
    }

    @Test
    @DisplayName("CP02: findByUsername con usuario inexistente retorna Optional vacío")
    void CP02_findByUsername_Inexistente_RetornaVacio() {
        Optional<User> resultado = userRepository.findByUsername("noexiste-jamas");

        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("CP03: findByEmail con email existente retorna Optional con valor")
    void CP03_findByEmail_Existente_RetornaUsuario() {
        Optional<User> resultado = userRepository.findByEmail(EMAIL);

        assertTrue(resultado.isPresent());
        assertEquals(UNAME, resultado.get().getUsername());
    }

    @Test
    @DisplayName("CP04: findByEmail con email inexistente retorna Optional vacío")
    void CP04_findByEmail_Inexistente_RetornaVacio() {
        Optional<User> resultado = userRepository.findByEmail("noexiste@example.com");

        assertTrue(resultado.isEmpty());
    }
}
