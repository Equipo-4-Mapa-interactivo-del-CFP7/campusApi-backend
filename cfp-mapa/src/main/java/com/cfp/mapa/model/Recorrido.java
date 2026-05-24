package com.cfp.mapa.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "recorridos")
public class Recorrido {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @ElementCollection
    @CollectionTable(name = "recorrido_espacios",
            joinColumns = @JoinColumn(name = "recorrido_id"))
    @OrderColumn(name = "orden")
    @Column(name = "espacio_id")
    private List<Long> espacioIds;

    @Column(nullable = false)
    private Boolean accesible = false;

    public Recorrido() {}

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public List<Long> getEspacioIds() {
        return espacioIds;
    }

    public Boolean getAccesible() {
        return accesible;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setEspacioIds(List<Long> espacioIds) {
        this.espacioIds = espacioIds;
    }

    public void setAccesible(Boolean accesible) {
        this.accesible = accesible;
    }

}
