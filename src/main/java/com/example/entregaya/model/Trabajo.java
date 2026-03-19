package com.example.entregaya.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;

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

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fechainicio")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fechaentrega")
    private LocalDateTime fechaEntrega;

    // Cambio del @ManytoMany, usar entidad de ColaboradorTrabajo para almacenar rol
    @OneToMany(mappedBy = "trabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("rol DESC")  // LIDER primero (DESC: LIDER > EDITOR > COLABORADOR)
    private Set<ColaboradorTrabajo> colaboradores = new HashSet<>();

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

    public Set<ColaboradorTrabajo> getColaboradores() {
        return colaboradores;
    }

    public void setColaboradores(Set<ColaboradorTrabajo> colaboradores) {
        this.colaboradores = colaboradores;
    }

    // Agregar un mimebro con un rol
    // si ya esta en el trabajo no duplicar
    public void agregarColaborador(User user, ColaboradorTrabajo.Rol rol) {
        boolean yaExiste = colaboradores.stream().anyMatch(c -> c.getUser().getId().equals(user.getId()));

        if (!yaExiste) {
            this.colaboradores.add(new ColaboradorTrabajo(this, user, rol));
        }
    }

    //sobrecarga para poder seguir usando el mismo metodo, agregar el rol por defecto
    public void agregarColaborador(User user) {
        agregarColaborador(user, ColaboradorTrabajo.Rol.COLABORADOR);
    }

    public Set<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
    }
}
