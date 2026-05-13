package com.example.entregaya.dto;

import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class TrabajoEditarDTO {

    private String nombreTrabajo;
    private String descripcion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaEntrega;

    public TrabajoEditarDTO() {}

    public String getNombreTrabajo() { return nombreTrabajo; }
    public void setNombreTrabajo(String nombreTrabajo) { this.nombreTrabajo = nombreTrabajo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaEntrega() { return fechaEntrega; }
    public void setFechaEntrega(LocalDateTime fechaEntrega) { this.fechaEntrega = fechaEntrega; }
}