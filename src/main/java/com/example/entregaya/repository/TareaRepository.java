package com.example.entregaya.repository;
import com.example.entregaya.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findBytrabajoId(Long trabajoId);

    @Query(value = "SELECT * FROM tarea WHERE trabajo_id = :id", nativeQuery = true)
    List<Tarea> buscarPorTrabajoNativo(@Param("id") Long id);

    @Query("SELECT t FROM Tarea t WHERE t.fechaFinal BETWEEN :ahora AND :limite AND t.completada = false")
    List<Tarea> findTareasProximasAVencer(
            @Param("ahora") LocalDateTime ahora,
            @Param("limite") LocalDateTime limite
    );
}


