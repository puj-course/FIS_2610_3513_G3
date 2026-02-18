package com.example.entregaya.repository;

import com.example.entregaya.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad User.
 * Permite realizar operaciones CRUD sobre la tabla users.
 */

public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Busca un usuario por su username.
     * Utilizado por el servicio de autenticacion.
     */
    Optional<User> findByUsername(String username);
}
