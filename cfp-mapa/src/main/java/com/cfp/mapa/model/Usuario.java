package com.cfp.mapa.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Rol rol = Rol.VISITANTE;

    @Column(columnDefinition = "TEXT")
    private String preferenciasAccesibilidad;

    public enum Rol { ESTUDIANTE, DOCENTE, VISITANTE, ADMIN }

    public Usuario() {}

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Rol getRol() { return rol; }

    public String getPreferenciasAccesibilidad() {
        return preferenciasAccesibilidad;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public void setPreferenciasAccesibilidad(String preferenciasAccesibilidad) {
        this.preferenciasAccesibilidad = preferenciasAccesibilidad;
    }
}
