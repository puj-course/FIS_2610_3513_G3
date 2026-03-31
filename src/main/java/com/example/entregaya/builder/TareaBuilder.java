package com.example.entregaya.builder;

import com.example.entregaya.model.Tarea;
import com.example.entregaya.model.Trabajo;
import com.example.entregaya.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Builder para construir objetos Tarea de forma encadenada.
 * Patron GoF Builder — HU-21
**/

public class TareaBuilder {

    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private Tarea.Dificultad dificultad = Tarea.Dificultad.MEDIA;
    private Trabajo trabajo;
    private Set<User> responsables = new HashSet<>();

    public TareaBuilder nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public TareaBuilder descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    public TareaBuilder fechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
        return this;
    }

    public TareaBuilder fechaFinal(LocalDateTime fechaFinal) {
        this.fechaFinal = fechaFinal;
        return this;
    }

    public TareaBuilder dificultad(Tarea.Dificultad dificultad) {
        this.dificultad = dificultad;
        return this;
    }

    public TareaBuilder trabajo(Trabajo trabajo) {
        this.trabajo = trabajo;
        return this;
    }

    public TareaBuilder responsables(Set<User> responsables) {
        this.responsables = responsables != null ? responsables : new HashSet<>();
        return this;
    }

    public Tarea build() {
        Tarea tarea = new Tarea();
        tarea.setNombre(nombre);
        tarea.setDescripcion(descripcion);
        tarea.setFechaInicio(fechaInicio);
        tarea.setFechaFinal(fechaFinal);
        tarea.setDificultad(dificultad != null ? dificultad : Tarea.Dificultad.MEDIA);
        tarea.setTrabajo(trabajo);
        tarea.setResponsables(responsables);
        return tarea;
    }
}