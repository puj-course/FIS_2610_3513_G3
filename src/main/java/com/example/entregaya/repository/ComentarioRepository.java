package com.example.entregaya.repository;

import com.example.entregaya.model.Comentario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {

    @Query("SELECT c FROM Comentario c WHERE c.tarea.id = :tareaId ORDER BY c.fechaCreacion DESC")
    List<Comentario> findByTareaIdOrderByFechaCreacionDesc(@Param("tareaId") Long tareaId);

    List<Comentario> findByAutorId(Long autorId);
}
