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
 *
 * Uso:
 *   Tarea t = new TareaBuilder()
 *       .nombre("Diseño UI")
 *       .descripcion("Mockups en Figma")
 *       .fechaInicio(LocalDateTime.now())
 *       .fechaFinal(LocalDateTime.now().plusDays(3))
 *       .dificultad(Tarea.Dificultad.MEDIA)
 *       .trabajo(trabajo)
 *       .responsables(Set.of(user1))
 *       .build();
 */

public class TareaBuilder {

    private String nombre;
    private String descripcion;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFinal;
    private Tarea.Dificultad dificultad = Tarea.Dificultad.MEDIA;
    private Trabajo trabajo;
    private Set<User> responsables = new HashSet<>();

    /** Establece el nombre de la tarea. Obligatorio — no puede ser nulo ni en blanco. */
    public TareaBuilder nombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    /** Establece la descripción opcional de la tarea. */
    public TareaBuilder descripcion(String descripcion) {
        this.descripcion = descripcion;
        return this;
    }

    /** Establece la fecha de inicio. Opcional, pero si se provee junto a fechaFinal,
     *  no puede ser posterior a ella. */
    public TareaBuilder fechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
        return this;
    }

    /** Establece la fecha de cierre. Opcional, pero si se provee junto a fechaInicio,
     *  no puede ser anterior a ella. */
    public TareaBuilder fechaFinal(LocalDateTime fechaFinal) {
        this.fechaFinal = fechaFinal;
        return this;
    }

    /** Establece la dificultad. Si es nula se asigna MEDIA por defecto. */
    public TareaBuilder dificultad(Tarea.Dificultad dificultad) {
        this.dificultad = dificultad;
        return this;
    }

    /** Asocia la tarea a un trabajo existente. */
    public TareaBuilder trabajo(Trabajo trabajo) {
        this.trabajo = trabajo;
        return this;
    }

    /** Asigna el conjunto de responsables. Si es null se inicializa vacío. */
    public TareaBuilder responsables(Set<User> responsables) {
        this.responsables = responsables != null ? responsables : new HashSet<>();
        return this;
    }

    public Tarea build() {

        //validacion de nombre obligatorio
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalStateException( "El nombre de la tarea es obligatorio y no puede estar en blanco.");
        }

        //validacion de fechas
        if (fechaInicio != null && fechaFinal != null && fechaFinal.isBefore(fechaInicio)) {
            throw new IllegalStateException( "La fecha final no puede ser anterior a la fecha de inicio.");
        }

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