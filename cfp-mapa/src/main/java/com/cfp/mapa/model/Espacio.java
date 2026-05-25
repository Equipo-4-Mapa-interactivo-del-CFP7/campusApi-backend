package com.cfp.mapa.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "espacios")
public class Espacio {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEspacio tipo;

    private String piso;
    private Double coordenadaX;
    private Double coordenadaY;
    private Double coordenadaZ;
    private Boolean accesible;

    @ElementCollection
    @CollectionTable(name = "espacio_fotos", joinColumns = @JoinColumn(name = "espacio_id"))
    @Column(name = "url_foto")
    private List<String> fotos;

    @Column(nullable = false)
    private Boolean activo = true;

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL)
    private List<Reporte> reportes;

    public enum TipoEspacio {
        AULA, OFICINA, BANIO, ACCESO, ESPACIO_COMUN
    }

    public Espacio() {}

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public TipoEspacio getTipo() {
        return tipo;
    }

    public String getPiso() {
        return piso;
    }

    public Double getCoordenadaX() {
        return coordenadaX;
    }

    public Double getCoordenadaY() {
        return coordenadaY;
    }

    public Double getCoordenadaZ() {
        return coordenadaZ;
    }

    public Boolean getAccesible() {
        return accesible;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public Boolean getActivo() {
        return activo;
    }

    public List<Reporte> getReportes() {
        return reportes;
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

    public void setTipo(TipoEspacio tipo) {
        this.tipo = tipo;
    }

    public void setPiso(String piso) {
        this.piso = piso;
    }

    public void setCoordenadaX(Double coordenadaX) {
        this.coordenadaX = coordenadaX;
    }

    public void setCoordenadaY(Double coordenadaY) {
        this.coordenadaY = coordenadaY;
    }

    public void setCoordenadaZ(Double coordenadaZ) {
        this.coordenadaZ = coordenadaZ;
    }

    public void setAccesible(Boolean accesible) {
        this.accesible = accesible;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setReportes(List<Reporte> reportes) {
        this.reportes = reportes;
    }
}
