package com.cfp.mapa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reportes")
public class Reporte {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id", nullable = false)
    private Espacio espacio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoReporte tipo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private EstadoReporte estado = EstadoReporte.PENDIENTE;

    public enum TipoReporte {
        ACCESO_BLOQUEADO, PROBLEMA_SENALETICA, BARRERA_FISICA, DIFICULTAD_ORIENTACION
    }

    public enum EstadoReporte {
        PENDIENTE, EN_REVISION, RESUELTO
    }

    public Reporte() {}

    public Long getId() {
        return id;
    }

    public Espacio getEspacio() {
        return espacio;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public TipoReporte getTipo() {
        return tipo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoReporte getEstado() {
        return estado;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setEspacio(Espacio espacio) {
        this.espacio = espacio;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public void setTipo(TipoReporte tipo) {
        this.tipo = tipo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public void setEstado(EstadoReporte estado) {
        this.estado = estado;
    }

}
