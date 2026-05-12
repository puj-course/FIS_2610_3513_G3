package com.example.entregaya.dto;

import com.example.entregaya.model.Tarea;
import java.util.ArrayList;
import java.util.List;

public class TareaEditarDTO extends TareaCrearDTO {

    private boolean completada;
    private List<String> etiquetas = new ArrayList<>();
    private List<Long> responsablesIds = new ArrayList<>();

    public boolean isCompletada() { return completada; }
    public void setCompletada(boolean completada) { this.completada = completada; }

    public List<String> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(List<String> etiquetas) { this.etiquetas = etiquetas; }

    public List<Long> getResponsablesIds() { return responsablesIds; }
    public void setResponsablesIds(List<Long> responsablesIds) { this.responsablesIds = responsablesIds; }
}