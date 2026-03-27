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
    public User register(String username, String password, String email) {
        if(userRepository.findByUsername(username).isPresent()){
            throw new IllegalArgumentException("El usuario: " +username+ " ya existe\n");
        }
        if(password==null || password.length()<6){
            throw new IllegalArgumentException("la contraseña debe tener mas de 6 carcateres");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico es obligatorio.");
        }
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido.");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El correo '" + email + "' ya está registrado.");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);

        return userRepository.save(user);
    }

    // Actualizar el nombre de usuario
    public void actualizarUsername(String usernameActual, String nuevoUsername) {
        if (nuevoUsername == null || nuevoUsername.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        if (nuevoUsername.equals(usernameActual)) {
            throw new IllegalArgumentException("El nuevo usuario debe ser diferente al actual.");
        }
        if (userRepository.findByUsername(nuevoUsername).isPresent()) {
            throw new IllegalArgumentException("El usuario '" + nuevoUsername + "' ya está en uso.");
        }
        User user = userRepository.findByUsername(usernameActual)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        user.setUsername(nuevoUsername);
        userRepository.save(user);
    }

    // Actualizar contraseña
    public void actualizarPassword(String username, String passwordActual, String passwordNueva, String passwordConfirm) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (!passwordEncoder.matches(passwordActual, user.getPassword())) {
            throw new IllegalArgumentException("La contraseña actual es incorrecta.");
        }
        if (passwordNueva == null || passwordNueva.length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 6 caracteres.");
        }
        if (!passwordNueva.equals(passwordConfirm)) {
            throw new IllegalArgumentException("La confirmación no coincide con la nueva contraseña.");
        }
        if (passwordEncoder.matches(passwordNueva, user.getPassword())) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente a la actual.");
        }
        user.setPassword(passwordEncoder.encode(passwordNueva));
        userRepository.save(user);
    }

    // Actualizar email
    public void actualizarEmail(String username, String nuevoEmail) {
        if (nuevoEmail == null || nuevoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("El correo electrónico no puede estar vacío.");
        }
        if (!nuevoEmail.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("El formato del correo electrónico no es válido.");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        if (nuevoEmail.equals(user.getEmail())) {
            throw new IllegalArgumentException("El nuevo correo debe ser diferente al actual.");
        }
        if (userRepository.findByEmail(nuevoEmail).isPresent()) {
            throw new IllegalArgumentException("El correo '" + nuevoEmail + "' ya está en uso.");
        }
        user.setEmail(nuevoEmail);
        userRepository.save(user);
    }

}
