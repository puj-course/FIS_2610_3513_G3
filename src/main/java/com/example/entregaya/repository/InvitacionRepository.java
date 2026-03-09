package com.example.entregaya.repository;

import com.example.entregaya.model.Invitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface InvitacionRepository extends JpaRepository<Invitacion, Long> {

    @Query("SELECT i FROM Invitacion i LEFT JOIN FETCH i.remitente LEFT JOIN FETCH i.destinatario LEFT JOIN FETCH i.trabajo WHERE i.destinatario.username = :username AND i.estado = :estado")
    List<Invitacion> findPendientesPorDestinatario(@Param("username") String username, @Param("estado") Invitacion.Estado estado);

    @Query("SELECT i FROM Invitacion i LEFT JOIN FETCH i.remitente LEFT JOIN FETCH i.destinatario WHERE i.trabajo.id = :trabajoId")
    List<Invitacion> findPorTrabajo(@Param("trabajoId") Long trabajoId);

    @Query("SELECT i FROM Invitacion i WHERE i.trabajo.id = :trabajoId AND i.destinatario.username = :username AND i.estado = :estado")
    Optional<Invitacion> findPendiente(@Param("trabajoId") Long trabajoId, @Param("username") String username, @Param("estado") Invitacion.Estado estado);
}