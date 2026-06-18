package com.cfp.mapa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auditorias_usuarios", indexes = {
    @Index(name = "idx_auditoria_fecha", columnList = "fechaAccion")
})
@Getter
@NoArgsConstructor
public class AuditoriaUsuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private LocalDateTime fechaAccion;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "operador_id", nullable = false, updatable = false)
  private Usuario operador;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "usuario_afectado_id", nullable = false, updatable = false)
  private Usuario usuarioAfectado;

  @Column(nullable = false, updatable = false)
  private String accion;

  public AuditoriaUsuario(Usuario operador, Usuario usuarioAfectado, String accion) {
    this.fechaAccion = LocalDateTime.now();
    this.operador = operador;
    this.usuarioAfectado = usuarioAfectado;
    this.accion = accion;
  }
}
