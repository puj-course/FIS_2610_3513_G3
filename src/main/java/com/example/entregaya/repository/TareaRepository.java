package com.example.entregaya.repository;
import com.example.entregaya.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    List<Tarea> findBytrabajoId(Long trabajoId);
}
