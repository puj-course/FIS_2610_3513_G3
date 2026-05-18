package com.example.entregaya.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(unique = true)
    private String email;

    /**
     * Número de teléfono del usuario en formato E.164 (ej. +573001234567).
     * Se usa para enviar notificaciones SMS a través de Twilio.
     * Reemplaza el campo telegram_chat_id de la integración anterior.
     */
    @Column(name = "phone_number")
    private String phoneNumber;

    // Relación a través de ColaboradorTrabajo
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ColaboradorTrabajo> trabajos = new HashSet<>();


    //Constructores
    public User( ){

    }

    public User(Long id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if(!(o instanceof User)) {
            return false;
        }
        User user = (User) o;
        return id != null &&  id.equals(user.id);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    //getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<ColaboradorTrabajo> getTrabajos() {
        return trabajos;
    }

    public void setTrabajos(Set<ColaboradorTrabajo> trabajos) {
        this.trabajos = trabajos;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
}
