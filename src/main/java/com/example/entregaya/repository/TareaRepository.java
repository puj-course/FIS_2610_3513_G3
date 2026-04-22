package com.example.entregaya.repository;

import com.example.entregaya.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {

    List<Tarea> findBytrabajoId(Long trabajoId);

    @Query(value = "SELECT * FROM tarea WHERE trabajo_id = :id", nativeQuery = true)
    List<Tarea> buscarPorTrabajoNativo(@Param("id") Long id);


    @Query("SELECT DISTINCT t FROM Tarea t JOIN t.etiquetas e " +
           "WHERE t.trabajo.id = :trabajoId AND LOWER(e) = LOWER(:etiqueta)")
    List<Tarea> findByTrabajoIdAndEtiqueta(@Param("trabajoId") Long trabajoId,
                                           @Param("etiqueta")  String etiqueta);
}

