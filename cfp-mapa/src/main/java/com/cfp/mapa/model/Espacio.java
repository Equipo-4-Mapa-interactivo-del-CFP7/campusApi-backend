package com.cfp.mapa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "espacios")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
