package com.example.entregaya.repository;

import com.example.entregaya.model.Notificacion;
import com.example.entregaya.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Obtener notificaciones de un usuario (más recientes primero)
    List<Notificacion> findByDestinatarioOrderByFechaCreacionDesc(User destinatario);

    // Solo no leídas
    List<Notificacion> findByDestinatarioAndLeidaFalseOrderByFechaCreacionDesc(User destinatario);
}