package com.cfp.mapa.model;

import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.model.enums.TipoReporte;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(
    name = "auditorias_usuarios",
    indexes = {
        @Index(name = "idx_auditoria_fecha", columnList = "fecha_accion"),
        @Index(name = "idx_auditoria_fecha_accion", columnList = "fecha_accion, accion"),
        @Index(name = "idx_auditoria_fecha_espacio", columnList = "fecha_accion, reporte_espacio_id"),
        @Index(name = "idx_auditoria_operador", columnList = "operador_id"),
        @Index(name = "idx_auditoria_afectado", columnList = "usuario_afectado_id"),
        @Index(name = "idx_auditoria_reporte", columnList = "reporte_id"),
        @Index(name = "idx_auditoria_reporte_accion_viejo", columnList = "reporte_id, accion"),
        @Index(name = "idx_auditoria_reporte_accion_fecha", columnList = "reporte_id, accion, fecha_accion")
    }
)
public class AuditoriaUsuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fecha_accion", nullable = false, updatable = false)
  private LocalDateTime fechaAccion;

  @Column(name = "operador_id", nullable = false, updatable = false)
  private Long operadorId;

  @Column(name = "operador_nombre_completo", nullable = false, updatable = false)
  private String operadorNombreCompleto;

  @Column(name = "operador_dni", nullable = false, updatable = false)
  private String operadorDni;

  @Column(name = "operador_rol", nullable = false, updatable = false)
  private String operadorRol;

  @Column(name = "usuario_afectado_id", updatable = false)
  private Long usuarioAfectadoId;

  @Column(name = "usuario_afectado_nombre_completo", updatable = false)
  private String usuarioAfectadoNombreCompleto;

  @Column(name = "usuario_afectado_dni", updatable = false)
  private String usuarioAfectadoDni;

  @Column(name = "reporte_id", updatable = false)
  private Long reporteId;

  @Column(name = "reporte_espacio_id", updatable = false)
  private Long reporteEspacioId;

  @Enumerated(EnumType.STRING)
  @Column(name = "reporte_tipo", updatable = false)
  private TipoReporte reporteTipo;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, updatable = false)
  private TipoAccionAuditoria accion;

  @Column(updatable = false)
  private String detalles;

  // Constructor optimizado para capturar los datos del momento
  public AuditoriaUsuario(
      Usuario operador,
      Usuario usuarioAfectado,
      Long reporteId,
      Long reporteEspacioId,
      TipoReporte reporteTipo,
      TipoAccionAuditoria accion,
      String detales
  ) {

    this.fechaAccion = LocalDateTime.now();
    this.operadorId = operador.getId();
    this.operadorNombreCompleto = operador.getNombre() + " " + operador.getApellido();
    this.operadorDni = operador.getDni();
    this.operadorRol = operador.getRol().name();
    this.reporteId = reporteId;
    this.reporteEspacioId = reporteEspacioId;
    this.reporteTipo = reporteTipo;
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
