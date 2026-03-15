package com.example.entregaya.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.Objects;

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

    @OneToMany(mappedBy = "user")
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
        if (this == o) return true;
        if(!(o instanceof User)) return false;
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
}
