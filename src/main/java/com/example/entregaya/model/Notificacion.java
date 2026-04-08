package com.example.entregaya.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación: muchas notificaciones para un usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_id", nullable = false)
    private User destinatario;

    @Column(name = "mensaje", nullable = false, length = 500)
    private String mensaje;

    @Column(name = "leida", nullable = false)
    private boolean leida = false;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    // ─── CONSTRUCTORES ───
    public Notificacion() {
    }

    public Notificacion(User destinatario, String mensaje) {
        this.destinatario = destinatario;
        this.mensaje = mensaje;
        this.leida = false;
        this.fechaCreacion = LocalDateTime.now();
    }

    // ─── GETTERS Y SETTERS ───

    public Long getId() {
        return id;
    }

    public User getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(User destinatario) {
        this.destinatario = destinatario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public boolean isLeida() {
        return leida;
    }

    public void setLeida(boolean leida) {
        this.leida = leida;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}