package com.cfp.mapa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "registro_busqueda",
    indexes = {
        @Index(name = "idx_busqueda_fecha_nodos", columnList = "fecha, desde_id, hasta_id"),
        @Index(name = "idx_busqueda_fecha_hasta", columnList = "fecha, hasta_id")
    }
)
public class RegistroBusqueda {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "desde_id")
  private Long desdeId;

  @Column(name = "hasta_id")
  private Long hastaId;

  @CreationTimestamp
  private LocalDateTime fecha;

  public RegistroBusqueda(Long desdeId, Long hastaId) {
    this.desdeId = desdeId;
    this.hastaId = hastaId;
  }
}
