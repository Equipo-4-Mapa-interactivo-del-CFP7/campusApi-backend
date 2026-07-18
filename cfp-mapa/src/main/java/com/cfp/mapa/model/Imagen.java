package com.cfp.mapa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "espacio_imagenes")
public class Imagen {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "url_foto", nullable = false)
  private String urlFoto;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "espacio_id", nullable = false)
  private Espacio espacio;
/*
  @ManyToOne
  private Espacio espacio;

  @ManyToOne
  private Reporte reporte;

 */
}