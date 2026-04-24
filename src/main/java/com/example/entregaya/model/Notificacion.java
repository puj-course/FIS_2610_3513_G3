package com.example.entregaya.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    public enum TipoNotificacion {
        TAREA,
        MIEMBRO,
        RECORDATORIO_VENCIMIENTO  // ← solo agrega esta línea
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private User destinatario;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoNotificacion tipo = TipoNotificacion.TAREA;  // Valor por defecto

    @Column(name = "leida", nullable = false)
    private boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // Constructores
    public Notificacion() {}

    public Notificacion(User destinatario, String mensaje) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.tipo = TipoNotificacion.TAREA;
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    public Notificacion(User destinatario, String mensaje, TipoNotificacion tipo) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    // Getters y Setters (existentes + nuevo)
    public Long getId() { return id; }
    public User getDestinatario() { return destinatario; }
    public void setDestinatario(User destinatario) { this.destinatario = destinatario; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public TipoNotificacion getTipo() { return tipo; }
    public void setTipo(TipoNotificacion tipo) { this.tipo = tipo; }
    public boolean isLeida() { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
}
