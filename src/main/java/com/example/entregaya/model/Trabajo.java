package com.example.entregaya.model;

import com.example.entregaya.builder.TareaBuilder;
import com.example.entregaya.prototype.TrabajoPrototype;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.format.annotation.DateTimeFormat;
import com.example.entregaya.model.HistorialEvento;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "trabajo")
public class Trabajo implements TrabajoPrototype {

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


    /**
     * Copia profunda de este trabajo.
     * Las tareas se recrean con TareaBuilder para respetar sus invariantes.
     * Los colaboradores NO se copian — el servicio agrega al creador como LIDER.
     */
    @Override
    public Trabajo clonar(){
        Trabajo copia = new Trabajo();
        copia.setNombreTrabajo(this.nombreTrabajo + " (copia) ");
        copia.setDescripcion(this.descripcion);
        copia.setFechaInicio(this.fechaInicio);
        copia.setFechaEntrega(this.fechaEntrega);

        // Clonar cada tarea: nuevo objeto, mismos datos, sin estado completada
        for(Tarea original : this.tareas){
            Tarea tareaClon= new TareaBuilder()
                    .nombre(original.getNombre())
                    .fechaInicio(original.getFechaInicio())
                    .fechaFinal(original.getFechaFinal())
                    .descripcion(original.getDescripcion())
                    .dificultad(original.getDificultad())
                    .trabajo(copia)
                    .responsables(new HashSet<>())
                    .build();
            copia.getTareas().add(tareaClon);
        }
        return copia;
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

    @OneToMany(mappedBy = "trabajo", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("fechaEvento DESC")
    private List<HistorialEvento> historialEventos = new ArrayList<>();

    // Getter y setter
    public List<HistorialEvento> getHistorialEventos() {
        return historialEventos;
    }

    public void setHistorialEventos(List<HistorialEvento> historialEventos) {
        this.historialEventos = historialEventos;
    }
}
