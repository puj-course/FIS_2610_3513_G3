package com.example.entregaya.model;

import jakarta.persistence.*;

// Entidad de unión entre Trabajo y User.
// Reemplaza el @ManyToMany directo
// Mapear la tabla colaboradores

@Entity
@Table(name = "colaboradores")
public class ColaboradorTrabajo {

    public enum Rol {
        LIDER,
        EDITOR,
        COLABORADOR
    }

    @EmbeddedId
    private ColaboradorTrabajoId id = new ColaboradorTrabajoId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("trabajoId")
    @JoinColumn(name = "trabajoid")
    private Trabajo trabajo;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "usersid")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.COLABORADOR;

    public ColaboradorTrabajo() {}

    public ColaboradorTrabajo(Trabajo trabajo, User user, Rol rol) {
        this.trabajo = trabajo;
        this.user = user;
        this.rol = rol;
        this.id = new ColaboradorTrabajoId(trabajo.getId(), user.getId());
    }

    public ColaboradorTrabajoId getId() {
        return id;
    }

    public void setId(ColaboradorTrabajoId id) {
        this.id = id;
    }

    public Trabajo getTrabajo() {
        return trabajo;
    }

    public void setTrabajo(Trabajo trabajo) {
        this.trabajo = trabajo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }
}
