package com.example.entregaya.strategy;
import com.example.entregaya.model.ColaboradorTrabajo;


/**
 * Patrón Strategy — Comportamental
 *
 * Define el contrato para verificar si un colaborador
 * tiene permiso para realizar una acción en un trabajo.
 *
 * Problema que resuelve: la lógica de roles estaba duplicada
 * en CustomTrabajoDetailsService, CustomInvitacionDetailsService
 * y TareaController. Con esta interfaz, cada regla de permiso
 * queda en una clase intercambiable y el código que la usa
 * no necesita conocer los detalles de cada rol.
 */

public interface Permisostrategy {
    /**
     * Evalúa si el colaborador cumple la regla de permiso.
     *
     * @param colaborador relación usuario-trabajo con su rol asignado
     * @return true si tiene permiso, false en caso contrario
     */
    boolean tienePermiso(ColaboradorTrabajo colaborador);
}
