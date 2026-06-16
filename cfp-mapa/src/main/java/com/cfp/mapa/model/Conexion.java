package com.cfp.mapa.model;

import com.cfp.mapa.model.enums.TipoTransito;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "conexiones")
public class Conexion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "espacio_origen_id", nullable = false)
  private Espacio origen;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "espacio_destino_id", nullable = false)
  private Espacio destino;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TipoTransito tipoTransito;

  @Column(nullable = false)
  private Double distancia;

  @Column(nullable = false)
  private Boolean accesible;

  @Column(nullable = false)
  private Boolean activa = true;
}
