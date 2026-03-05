package com.example.entregaya.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "trabajo")
public class Trabajo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombretrabajo", unique = true, nullable = false)
    private String nombreTrabajo;

    @Column(name = "descripcion")
    private String descripcion;

    @Column(name = "fechainicio")
    private LocalDateTime fechaInicio;

    @Column(name = "fechaentrega")
    private LocalDateTime fechaEntrega;

    // Relacion muchos a muchos con User
    @ManyToMany
    @JoinTable(
            name = "colaboradores",
            joinColumns = @JoinColumn(name = "trabajoid"),
            inverseJoinColumns = @JoinColumn(name = "usersid")
    )
    private Set<User> colaboradores = new HashSet<>();

    // Relacion uno a muchos con tareas
    @OneToMany(mappedBy = "trabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Tarea> tareas = new HashSet<>();

    // Constructores
    public Trabajo() {
    }

    // completo
    public Trabajo(Long id, String nombreTrabajo, String descripcion, LocalDateTime fechaInicio, LocalDateTime fechaEntrega) {
        this.id = id;
        this.nombreTrabajo = nombreTrabajo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaEntrega = fechaEntrega;
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreTrabajo() {
        return nombreTrabajo;
    }

    public void setNombreTrabajo(String nombreTrabajo) {
        this.nombreTrabajo = nombreTrabajo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Set<User> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(Set<User> colaboradores) {
        this.colaboradores = colaboradores;
    }

    // Metodo para agregar colaborador
    public void agregarColaborador(User user) {
        this.colaboradores.add(user);
    }

    public Set<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
    }
}