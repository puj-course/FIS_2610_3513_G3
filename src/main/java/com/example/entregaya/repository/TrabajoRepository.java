package com.example.entregaya.repository;

import com.example.entregaya.model.Trabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TrabajoRepository extends JpaRepository<Trabajo,Long> {

    // Reemplaza findByColaboradoresUsername, que funcionaba con @ManyToMany.
    // Pasar por ColaboradorTrabajo para el username.
    @Query("SELECT t FROM Trabajo t " + "JOIN t.colaboradores c " + "WHERE c.user.username = :username")
    List<Trabajo> findByColaboradoresUsername(@Param("username") String username);
}
