package com.example.entregaya.dto;

import com.example.entregaya.model.Tarea;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

public class TareaCrearDTO {

    private String nombre;
    private String descripcion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime fechaFinal;

    private Tarea.Dificultad dificultad = Tarea.Dificultad.MEDIA;

    public TareaCrearDTO() {}

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public LocalDateTime getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDateTime fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDateTime getFechaFinal() { return fechaFinal; }
    public void setFechaFinal(LocalDateTime fechaFinal) { this.fechaFinal = fechaFinal; }

    public Tarea.Dificultad getDificultad() { return dificultad; }
    public void setDificultad(Tarea.Dificultad dificultad) { this.dificultad = dificultad; }
}