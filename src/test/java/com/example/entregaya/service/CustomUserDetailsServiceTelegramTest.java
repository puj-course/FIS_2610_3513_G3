package com.example.entregaya.service;

import com.example.entregaya.model.User;
import com.example.entregaya.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

// Pruebas unitarias para actualizarTelegramChatId (HU-43)
class CustomUserDetailsServiceTelegramTest {

    private UserRepository userRepository;
    private CustomUserDetailsService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        service = new CustomUserDetailsService(userRepository, passwordEncoder);
    }

    // Caso 1: chatId válido se guarda correctamente
    @Test
    void actualizarTelegramChatId_debeGuardar_chatIdValido() {
        User user = new User();
        user.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        service.actualizarTelegramChatId("alice", "123456789");

        assertEquals("123456789", user.getTelegramChatId());
        verify(userRepository).save(user);
    }

    // Caso 2: chatId vacío se guarda como null (desactiva notificaciones)
    @Test
    void actualizarTelegramChatId_debeGuardar_nullCuandoVacio() {
        User user = new User();
        user.setUsername("alice");
        user.setTelegramChatId("123456789");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        service.actualizarTelegramChatId("alice", "");

        assertNull(user.getTelegramChatId());
        verify(userRepository).save(user);
    }

    // Caso 3: chatId nulo se guarda como null
    @Test
    void actualizarTelegramChatId_debeGuardar_nullCuandoNulo() {
        User user = new User();
        user.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        service.actualizarTelegramChatId("alice", null);

        assertNull(user.getTelegramChatId());
        verify(userRepository).save(user);
    }

    // Caso 4: chatId de más de 20 caracteres lanza IllegalArgumentException
    @Test
    void actualizarTelegramChatId_debeRechazar_chatIdDemasiadoLargo() {
        User user = new User();
        user.setUsername("alice");
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        String chatIdLargo = "123456789012345678901"; // 21 chars
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> service.actualizarTelegramChatId("alice", chatIdLargo));

        assertTrue(ex.getMessage().contains("20"));
        verify(userRepository, never()).save(any());
    }

    // Caso 5: usuario no encontrado lanza RuntimeException
    @Test
    void actualizarTelegramChatId_debeRechazar_usuarioNoEncontrado() {
        when(userRepository.findByUsername("noexiste")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> service.actualizarTelegramChatId("noexiste", "123"));
    }
}
