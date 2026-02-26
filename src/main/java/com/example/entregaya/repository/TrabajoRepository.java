package com.example.entregaya.repository;

import com.example.entregaya.model.Trabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TrabajoRepository extends JpaRepository<Trabajo,Long> {
    List<Trabajo> findByColaboradoresUsername(String username);
}
