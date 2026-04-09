package com.example.entregaya.observer;

import com.example.entregaya.dto.TareaEventoDTO;

/**
 * Interfaz Observer del patrón Observer (GoF).
 *
 * Cualquier clase que quiera reaccionar a cambios de estado
 * de una tarea debe implementar esta interfaz.
 * Agregar o quitar observers no requiere modificar CustomTareaDetailsService
 * (cumple CA5).
 */
public interface TareaObserver {

    /**
     * Llamado por el subject (CustomTareaDetailsService) cada vez que
     * una tarea cambia de estado.
     *
     * @param evento DTO con toda la información del cambio
     */
    void actualizar(TareaEventoDTO evento);
}