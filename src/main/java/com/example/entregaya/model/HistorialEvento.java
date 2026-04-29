package com.example.entregaya.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entidad que registra cronológicamente todos los eventos ocurridos en un trabajo.
 */
@Entity
@Table(name = "historial_evento")
public class HistorialEvento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trabajo_id", nullable = false)
    private Trabajo trabajo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEvento tipoEvento;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(length = 1000)
    private String detalles;

    @Column(nullable = false)
    private String usuarioAccion;

    @Column(nullable = false)
    private LocalDateTime fechaEvento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tarea_id")
    private Tarea tarea;

    public enum TipoEvento {
        CREACION_TAREA("Tarea creada"),
        CAMBIO_ESTADO_TAREA("Estado de tarea cambiado"),
        INGRESO_MIEMBRO("Miembro se unió"),
        SALIDA_MIEMBRO("Miembro abandonó"),
        CAMBIO_ROL("Rol de miembro cambió"),
        ASIGNACION_RESPONSABLE("Responsable asignado"),
        EDICION_TRABAJO("Trabajo editado"),
        COMENTARIO_EDITADO("Comentario editado"),       // HU-45 (#448)
        COMENTARIO_ELIMINADO("Comentario eliminado");   // HU-45 (#448)

        private final String descripcionDefault;

        TipoEvento(String descripcionDefault) {
            this.descripcionDefault = descripcionDefault;
        }

        public String getDescripcionDefault() {
            return descripcionDefault;
        }
    }

    public HistorialEvento() {}

    public HistorialEvento(Trabajo trabajo, TipoEvento tipoEvento, String descripcion,
                           String usuarioAccion, LocalDateTime fechaEvento) {
        this.trabajo = trabajo;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.usuarioAccion = usuarioAccion;
        this.fechaEvento = fechaEvento;
    }

    public HistorialEvento(Trabajo trabajo, TipoEvento tipoEvento, String descripcion,
                           String detalles, String usuarioAccion, LocalDateTime fechaEvento, Tarea tarea) {
        this.trabajo = trabajo;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.detalles = detalles;
        this.usuarioAccion = usuarioAccion;
        this.fechaEvento = fechaEvento;
        this.tarea = tarea;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trabajo getTrabajo() { return trabajo; }
    public void setTrabajo(Trabajo trabajo) { this.trabajo = trabajo; }

    public TipoEvento getTipoEvento() { return tipoEvento; }
    public void setTipoEvento(TipoEvento tipoEvento) { this.tipoEvento = tipoEvento; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getDetalles() { return detalles; }
    public void setDetalles(String detalles) { this.detalles = detalles; }

    public String getUsuarioAccion() { return usuarioAccion; }
    public void setUsuarioAccion(String usuarioAccion) { this.usuarioAccion = usuarioAccion; }

    public LocalDateTime getFechaEvento() { return fechaEvento; }
    public void setFechaEvento(LocalDateTime fechaEvento) { this.fechaEvento = fechaEvento; }

    public Tarea getTarea() { return tarea; }
    public void setTarea(Tarea tarea) { this.tarea = tarea; }
}