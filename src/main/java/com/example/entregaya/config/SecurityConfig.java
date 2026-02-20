package com.example.entregaya.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuracion principal de Spring Security.
 * - Que rutas requieren autenticacion
 * - Configuracion del login
 * - Configuracion del logout
 * - Encriptacion de contraseñas
 */
@Configuration
public class SecurityConfig {

    /**
     * Configuracion del filtro de seguridad.
     * Se definen las reglas de acceso.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // Configuracion de autorizacion de rutas
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login","/register", "/css/**").permitAll() //permitAll para rutas publicas, redireccione siempre a la pagina de login
                        .anyRequest().authenticated() //las que no sean /login necesita login primero
                )

                // Configuracion del formulario de login
                .formLogin(form -> form
                        .loginPage("/login") // pagina de login
                        .defaultSuccessUrl("/dashboard", true) //la ruta que redirecciona despues de login
                        .permitAll()
                )

                // Configuracion del logout
                .logout(logout -> logout
                        .logoutSuccessUrl("/logout") //redireccion despues de cerrar sesion
                        .permitAll()
                );

        return http.build();
    }

    /**
     * Bean para encriptar contraseñas usando BCrypt.
     * Es obligatorio para validar contraseñas almacenadas en la base de datos.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
