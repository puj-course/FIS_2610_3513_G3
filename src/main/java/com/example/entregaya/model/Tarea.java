package com.example.entregaya.model;

import com.example.entregaya.builder.TareaBuilder;
import com.example.entregaya.prototype.TareaPrototype;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "tarea")
public class Tarea implements TareaPrototype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column
    private String descripcion;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fechainicio")
    private LocalDateTime fechaInicio;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Column(name = "fechafinal")
    private LocalDateTime fechaFinal;

    @Column(name = "recordatorio_enviado", nullable = false)
    private boolean recordatorioEnviado = false;

    // Muchas tareas a un trabajo
    @ManyToOne
    @JoinColumn(name = "trabajo_id", nullable = false)
    private Trabajo trabajo;

    // Responsables de la tarea, muchos pueden ser asignados a la misma tarea
    @ManyToMany
    @JoinTable(
            name = "tarea_responsables",
            joinColumns = @JoinColumn(name = "tarea_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> responsables = new HashSet<>();

    // Comentarios de la tarea
    @OneToMany(mappedBy = "tarea", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaCreacion DESC")
    private List<Comentario> comentarios = new ArrayList<>();


    // Enum de dificultad de tarea
    public enum Dificultad {
        SIMPLE, MEDIA, ALTA;

        // Peso por dificicultad
        public int getPeso() {
            return switch (this) {
                case SIMPLE -> 1;
                case MEDIA  -> 2;
                case ALTA   -> 3;
            };
        }
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Dificultad dificultad = Dificultad.MEDIA;

    // Estado de la tarea
    @Column(nullable = false)
    private boolean completada = false;
    public Tarea() {
    }

    public Tarea(Long id, String nombre, String descripcion, LocalDateTime fechaInicio, LocalDateTime fechaFinal, Dificultad dificultad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFinal = fechaFinal;
        this.dificultad = dificultad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tarea)) return false;
        Tarea tarea = (Tarea) o;
        return id != null && id.equals(tarea.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public boolean isRecordatorioEnviado() { return recordatorioEnviado; }
    public void setRecordatorioEnviado(boolean recordatorioEnviado) {
        this.recordatorioEnviado = recordatorioEnviado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public LocalDateTime getFechaFinal() {
        return fechaFinal;
    }

    public void setFechaFinal(LocalDateTime fechaFinal) {
        this.fechaFinal = fechaFinal;
    }

    public Trabajo getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(Trabajo trabajo) {
        this.trabajo = trabajo;
    }

    public Set<User> getResponsables() {
        return responsables;
    }

    public void setResponsables(Set<User> responsables) {
        this.responsables = responsables;
    }

    public Dificultad getDificultad() {
        return dificultad;
    }

    public void setDificultad(Dificultad dificultad) {
        this.dificultad = dificultad;
    }

    public boolean getIsCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
    }

    /**
     * Copia profunda de esta tarea. Patrón GoF Prototype.
     *
     * Contrato:
     *   - id -> null  (se asigna al persistir)
     *   - nombre -> "[Copia] <nombre original>"
     *   - completada -> false
     *   - fechas -> null  (se reasignan si aplica)
     *   - responsables -> nuevo HashSet con los mismos usuarios
     *   - trabajo -> el que se pasa como argumento
     */
    @Override
    public Tarea clonar(Trabajo trabajo) {
        return new TareaBuilder()
                .nombre("[Copia] " + this.nombre)
                .descripcion(this.descripcion)
                .dificultad(this.dificultad)
                .responsables(new HashSet<>(this.responsables))
                .trabajo(trabajo)
                .build();
    }


}