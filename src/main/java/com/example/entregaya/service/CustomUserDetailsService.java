package com.example.entregaya.service;

import com.example.entregaya.model.User;
import com.example.entregaya.repository.UserRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service encargado de cargar los usuarios desde la base de datos
 * para el proceso de autenticacion de Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    // Repositorio para consultar usuarios en Postgres
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomUserDetailsService(UserRepository userRepository,  PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                .roles("USER")
                .build();
    }

    /**
     * Registra un nuevo usuario en la base de datos.
     * Encripta la contraseña antes de guardarla.
     */
    public User register(String username, String password) {
        if(userRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("El usuario: " +username+ " ya existe\n");
        }
        if(password==null || password.length()<6){
            throw new IllegalArgumentException("la contraseña debe tener mas de 6 carcateres");
        }
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        return userRepository.save(user);
    }
}
