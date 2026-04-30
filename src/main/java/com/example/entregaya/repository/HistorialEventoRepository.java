package com.example.entregaya.repository;

import com.example.entregaya.model.HistorialEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialEventoRepository extends JpaRepository<HistorialEvento, Long> {

    /**
     * Obtiene todos los eventos de un trabajo ordenados cronológicamente
     * (más recientes primero)
     */
    @Query("SELECT h FROM HistorialEvento h WHERE h.trabajo.id = :trabajoId ORDER BY h.fechaEvento DESC")
    List<HistorialEvento> findByTrabajoIdOrderByFechaDesc(@Param("trabajoId") Long trabajoId);

    /**
     * Obtiene eventos de un trabajo en un rango de fechas
     */
    @Query("SELECT h FROM HistorialEvento h WHERE h.trabajo.id = :trabajoId AND h.fechaEvento BETWEEN :fechaInicio AND :fechaFin ORDER BY h.fechaEvento DESC")
    List<HistorialEvento> findByTrabajoIdAndFechaRange(@Param("trabajoId") Long trabajoId,
                                                       @Param("fechaInicio") LocalDateTime fechaInicio,
                                                       @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Obtiene eventos de un trabajo de un tipo específico
     */
    @Query("SELECT h FROM HistorialEvento h WHERE h.trabajo.id = :trabajoId AND h.tipoEvento = :tipoEvento ORDER BY h.fechaEvento DESC")
    List<HistorialEvento> findByTrabajoIdAndTipoEvento(@Param("trabajoId") Long trabajoId,
                                                       @Param("tipoEvento") HistorialEvento.TipoEvento tipoEvento);

    /**
     * Obtiene eventos realizados por un usuario específico en un trabajo
     */
    @Query("SELECT h FROM HistorialEvento h WHERE h.trabajo.id = :trabajoId AND h.usuarioAccion = :usuarioAccion ORDER BY h.fechaEvento DESC")
    List<HistorialEvento> findByTrabajoIdAndUsuarioAccion(@Param("trabajoId") Long trabajoId,
                                                          @Param("usuarioAccion") String usuarioAccion);
}
