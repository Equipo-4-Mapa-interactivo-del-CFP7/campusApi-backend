package com.cfp.mapa.model;

import com.cfp.mapa.model.enums.TipoEspacio;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "espacios")
public class Espacio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoEspacio tipo;

    @Column//(nullable = false)
    private Double coordenadaX;

    @Column//(nullable = false)
    private Double coordenadaY;

    @Column(nullable = false)
    private Boolean accesible;

    @Column(nullable = false)
    private Boolean activo;

    @OneToMany(mappedBy = "espacio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Imagen> imagenes;

    @OneToMany(mappedBy = "origen", fetch = FetchType.LAZY)
    private List<Conexion> conexionesSalida = new ArrayList<>();
}
