package com.example.entregaya.dto;

import com.example.entregaya.model.ColaboradorTrabajo;

// DTO que representa un miembro de un trabajo con su rol
public class MiembroRolDTO {

    private Long userId;
    private String username;
    private ColaboradorTrabajo.Rol rol;

    public MiembroRolDTO() {}

    public MiembroRolDTO(Long userId, String username, ColaboradorTrabajo.Rol rol) {
        this.userId = userId;
        this.username = username;
        this.rol = rol;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ColaboradorTrabajo.Rol getRol() {
        return rol;
    }

    public void setRol(ColaboradorTrabajo.Rol rol) {
        this.rol = rol;
    }
}
