package com.example.entregaya.strategy;

import com.example.entregaya.model.ColaboradorTrabajo;
import org.springframework.stereotype.Component;

/**
 * Estrategia: solo el rol LIDER tiene permiso.
 *
 * Usada en acciones críticas:
 *  - Eliminar un colaborador del trabajo
 *  - Cambiar el rol de un miembro
 *  - Eliminar el trabajo completo
 */

@Component
public class Sololiderstrategy implements Permisostrategy{
    @Override
    public boolean tienePermiso(ColaboradorTrabajo colaborador) {
        return colaborador.getRol() == ColaboradorTrabajo.Rol.LIDER;
    }
}
