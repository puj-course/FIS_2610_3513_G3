package com.example.entregaya.service;

import com.example.entregaya.model.User;
import com.example.entregaya.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Service encargado de cargar los usuarios desde la base de datos
 * para el proceso de autenticacion de Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repositorio para consultar usuarios en Postgres
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Metodo que Spring Security ejecuta automáticamente
     * al intentar autenticar un usuario.
     */

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        // Buscar usuario en la base de datos
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Crear objeto que Spring Security entiende
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) //contraseña encriptada por Bcrypt
                .build();
    }
}
