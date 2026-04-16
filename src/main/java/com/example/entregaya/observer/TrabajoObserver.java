package com.example.entregaya.observer;

import com.example.entregaya.dto.TrabajoEventoDTO;

/**
 * Interfaz Observer para cambios en la composición de trabajos.
 * Implementada por clases que deseen reaccionar a ingresos/salidas de miembros.
 */
public interface TrabajoObserver {
    void actualizar(TrabajoEventoDTO evento);
}
