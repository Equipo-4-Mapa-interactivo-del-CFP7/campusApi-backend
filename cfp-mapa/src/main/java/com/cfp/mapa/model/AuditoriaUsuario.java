package com.cfp.mapa.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "auditorias_usuarios", indexes = {
    @Index(name = "idx_auditoria_fecha", columnList = "fechaAccion"),
    @Index(name = "idx_auditoria_operador", columnList = "operadorId"),
    @Index(name = "idx_auditoria_afectado", columnList = "usuarioAfectadoId"),
    @Index(name = "idx_auditoria_reporte", columnList = "reporteId")
})
@Getter
@NoArgsConstructor
public class AuditoriaUsuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, updatable = false)
  private LocalDateTime fechaAccion;

  @Column(nullable = false, updatable = false)
  private Long operadorId;

  @Column(nullable = false)
  private String operadorNombreCompleto;

  @Column(nullable = false)
  private String operadorDni;

  @Column(nullable = false, updatable = false)
  private String operadorRol;

  @Column(updatable = false)
  private Long usuarioAfectadoId;

  @Column
  private String usuarioAfectadoNombreCompleto;

  @Column
  private String usuarioAfectadoDni;

  @Column
  private Long reporteId;

  @Column(nullable = false, updatable = false)
  private String accion;

  @Column
  private String detalles;

  // Constructor optimizado para capturar los datos del momento
  public AuditoriaUsuario(
      Usuario operador,
      Usuario usuarioAfectado,
      Long reporteId,
      String accion,
      String detales
  ) {

    this.fechaAccion = LocalDateTime.now();
    this.operadorId = operador.getId();
    this.operadorNombreCompleto = operador.getNombre() + " " + operador.getApellido();
    this.operadorDni = operador.getDni();
    this.operadorRol = operador.getRol().name();
    this.reporteId = reporteId;
    this.accion = accion;
    this.detalles = detales;

    if (usuarioAfectado != null) {
      this.usuarioAfectadoId = usuarioAfectado.getId();
      this.usuarioAfectadoNombreCompleto = usuarioAfectado.getNombre() + " " + usuarioAfectado.getApellido();
      this.usuarioAfectadoDni = usuarioAfectado.getDni();
    } else {
      this.usuarioAfectadoId = null;
      this.usuarioAfectadoNombreCompleto = "N/A (Auto-acción)";
      this.usuarioAfectadoDni = "N/A";
    }
  }

  // Metodos setters específicos para el proceso de anonimizacion posterior
  public void cambiarDatosOperadorAnonimo(String nuevoNombre, String nuevoDni) {
    this.operadorNombreCompleto = nuevoNombre;
    this.operadorDni = nuevoDni;
  }

  public void cambiarDatosAfectadoAnonimo(String nuevoNombre, String nuevoDni) {
    this.usuarioAfectadoNombreCompleto = nuevoNombre;
    this.usuarioAfectadoDni = nuevoDni;
  }

  public void anonimizarDetalles(String detallesAnonimosJson) {
    this.detalles = detallesAnonimosJson;
  }
}
