package com.cfp.mapa.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "recorridos")
@Data
@NoArgsConstructor
@AllArgsConstructor
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

}
