package com.example.entregaya.repository;

import com.example.entregaya.model.ColaboradorTrabajo;
import com.example.entregaya.model.ColaboradorTrabajoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ColaboradorTrabajoRepository extends JpaRepository<ColaboradorTrabajo, ColaboradorTrabajoId> {

    // Obtiene todos los miembros de un trabajo con su rol
    @Query("SELECT ct FROM ColaboradorTrabajo ct "
            + "JOIN FETCH ct.user " +
            "WHERE ct.trabajo.id = :trabajoId " +
            "ORDER BY ct.rol ASC, ct.user.username ASC")
    List<ColaboradorTrabajo> findMiembrosConRol(@Param("trabajoId") Long trabajoId);
}
