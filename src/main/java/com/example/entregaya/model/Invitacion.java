package com.example.entregaya.model;

import jakarta.persistence.*;

@Entity
@Table(name= "invitacion")
public class Invitacion {
    public enum Estado{PENDIENTE, ACEPTADA,RECHAZADA }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "trabajo_id")
    private Trabajo trabajo;

    @ManyToOne
    @JoinColumn(name="destinatario_id")
    private User destinatario;

    @ManyToOne
    @JoinColumn(name="remitente_id")
    private User remitente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;

    public Invitacion() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Trabajo getTrabajo() { return trabajo; }
    public void setTrabajo(Trabajo trabajo) { this.trabajo = trabajo; }

    public User getDestinatario() { return destinatario; }
    public void setDestinatario(User destinatario) { this.destinatario = destinatario; }

    public User getRemitente() { return remitente; }
    public void setRemitente(User remitente) { this.remitente = remitente; }

    public Estado getEstado() { return estado; }
    public void setEstado(Estado estado) { this.estado = estado; }
}
