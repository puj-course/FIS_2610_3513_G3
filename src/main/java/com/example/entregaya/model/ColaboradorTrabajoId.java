package com.example.entregaya.model;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

// Clave primaria compuesta para ColaboradorTrabajo.
// Mapea la PK (trabajoid, usersid) de la tabla colaboradores.

@Embeddable
public class ColaboradorTrabajoId implements Serializable {

    private Long trabajoId;
    private Long userId;

    public ColaboradorTrabajoId() {}

    public ColaboradorTrabajoId(Long trabajoId, Long userId) {
        this.trabajoId = trabajoId;
        this.userId = userId;
    }

    public Long getTrabajoId() {
        return trabajoId;
    }
    public void setTrabajoId(Long trabajoId) {
        this.trabajoId = trabajoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}

