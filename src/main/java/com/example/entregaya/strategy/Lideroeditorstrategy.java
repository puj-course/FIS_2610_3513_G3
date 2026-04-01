package com.example.entregaya.strategy;


import com.example.entregaya.model.ColaboradorTrabajo;
import org.springframework.stereotype.Component;

@Component
public class Lideroeditorstrategy implements Permisostrategy {
    @Override
    public boolean tienePermiso(ColaboradorTrabajo colaborador) {
        return colaborador.getRol() == ColaboradorTrabajo.Rol.LIDER
                || colaborador.getRol() == ColaboradorTrabajo.Rol.EDITOR;
    }
}
