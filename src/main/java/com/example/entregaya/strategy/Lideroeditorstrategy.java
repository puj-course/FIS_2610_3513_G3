package com.example.entregaya.strategy;

import com.example.entregaya.model.ColaboradorTrabajo;
import org.springframework.stereotype.Component;

@Component
public class Lideroeditorstrategy implements Permisostrategy {
    @Override
    public boolean tienePermiso(ColaboradorTrabajo colaborador) {
        if (colaborador == null || colaborador.getRol() == null) {
            return false;  // Fail-safe: denegar si es null
        }
        return colaborador.getRol() == ColaboradorTrabajo.Rol.LIDER
                || colaborador.getRol() == ColaboradorTrabajo.Rol.EDITOR;
    }
}
