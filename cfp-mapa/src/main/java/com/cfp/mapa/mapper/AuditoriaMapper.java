package com.cfp.mapa.mapper;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.dto.metricas.AuditoriaAnaliticaResponseDTO;
import com.cfp.mapa.dto.metricas.CuentasDTO;
import com.cfp.mapa.dto.metricas.DatosDTO;
import com.cfp.mapa.dto.metricas.EdicionDTO;
import com.cfp.mapa.dto.metricas.EspacioDTO;
import com.cfp.mapa.dto.metricas.RendimientoDTO;
import com.cfp.mapa.dto.metricas.ReportesDTO;
import com.cfp.mapa.dto.metricas.TopEspaciosDTO;
import com.cfp.mapa.model.AuditoriaUsuario;
import com.cfp.mapa.model.enums.TipoReporte;
import com.cfp.mapa.repository.EspacioRepository;
import com.cfp.mapa.repository.projection.EspacioNombreProjection;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@RequiredArgsConstructor
@Component
public class AuditoriaMapper {

  private final ObjectMapper objectMapper;
  private final EspacioRepository espacioRepository;

  // AuditoriaUsuario -> AuditoriaResponseDTO
  public AuditoriaResponseDTO toDTO(AuditoriaUsuario auditoria) {

    Object detallesMapped = null;
    String detallesRaw = auditoria.getDetalles();

    if (detallesRaw != null && detallesRaw.trim().startsWith("{")) {
      try {
        detallesMapped = objectMapper.readTree(detallesRaw);
      } catch (Exception _) {
      }
    } else { // Si es texto plano, guardarlo
      detallesMapped = detallesRaw;
    }

    return new AuditoriaResponseDTO(
        auditoria.getId(),
        auditoria.getFechaAccion(),
        auditoria.getOperadorId(),
        auditoria.getOperadorNombreCompleto(),
        auditoria.getOperadorDni(),
        auditoria.getOperadorRol(),
        auditoria.getUsuarioAfectadoId(),
        auditoria.getUsuarioAfectadoNombreCompleto(),
        auditoria.getUsuarioAfectadoDni(),
        auditoria.getReporteId(),
        auditoria.getReporteEspacioId(),
        auditoria.getReporteTipo(),
        auditoria.getAccion(),
        detallesMapped
    );
  }

  // List<AuditoriaUsuario> -> AuditoriaAnaliticaResponseDTO
  public AuditoriaAnaliticaResponseDTO toAnaliticaResponseDTO(
      LocalDate desde,
      LocalDate hasta,
      List<AuditoriaUsuario> auditorias,
      Long topZonasCriticas
  ) {

    long topZonas = topZonasCriticas == null ? 5 : topZonasCriticas;

    CuentasAcumulador cuentas = new CuentasAcumulador();
    EdicionAcumulador ediciones = new EdicionAcumulador();
    ReporteContador reportes = new ReporteContador();
    Rendimiento rendimiento = new Rendimiento();
    ZonaImpactoContador zonasImpacto = new ZonaImpactoContador(espacioRepository);

    // Lectura unica a las auditorias
    for (AuditoriaUsuario auditoria : auditorias) {

      // Filtrar por el tipo de accion auditoria
      switch (auditoria.getAccion()) {
        // ==================
        // USUARIOS
        // ==================
        case USUARIO_CREADO -> cuentas.creados++;
        case PASSWORD_RESTABLECIDA -> cuentas.passwordRestablecidas++;
        case ESTADO_ACTIVO_MODIFICADO -> cuentas.estadosModificados++;
        case PASSWORD_CAMBIADA -> cuentas.passwordCambiadas++;
        case ROL_MODIFICADO -> cuentas.rolesModificados++;
        case USUARIO_ELIMINADO -> cuentas.eliminados++;
        case PASSWORD_OWNER_RECUPERADA -> cuentas.ownerRecuperaciones++;
        case OWNER_TRANSFERIDO -> cuentas.ownerTransferencias++;
        case DNI_EDITADO -> ediciones.registrarDni(auditoria.getUsuarioAfectadoId());
        case NOMBRE_APELLIDO_EDITADO -> ediciones.registrarNombreApellido(auditoria.getUsuarioAfectadoId());
        // ==================
        // REPORTES
        // ==================
        case REPORTE_CREADO -> {
          reportes.creados++;

          rendimiento.registrarCreacion(
              auditoria.getReporteTipo(),
              auditoria.getReporteId(),
              auditoria.getFechaAccion()
          );

          zonasImpacto.registrarImpacto(
              auditoria.getReporteTipo(),
              auditoria.getReporteEspacioId()
          );
        }
        case REPORTE_ATENDIDO -> reportes.atendidos++;
        case REPORTE_MODIFICADO -> reportes.modificados++;
        case REPORTE_TIEMPO_ELIMINADO -> reportes.modificados++;
        case REPORTE_CERRADO, REPORTE_CERRADO_AUTOMATICO -> {

          switch (auditoria.getAccion()) {
            case REPORTE_CERRADO -> reportes.cerradosManual++;
            case REPORTE_CERRADO_AUTOMATICO -> reportes.cerradosAutomatico++;
          }

          rendimiento.registrarCierre(
              auditoria.getReporteTipo(),
              auditoria.getReporteId(),
              auditoria.getFechaAccion()
          );
        }
      }

    }

    // Armar el DTO de respuesta completo
    return new AuditoriaAnaliticaResponseDTO(
        desde,
        hasta,
        cuentas.toCuentasDTO(),
        ediciones.toDatosDTO(),
        reportes.toReporteDTO(
            rendimiento.toListRendimientoDTO(),
            zonasImpacto.toListTopEspaciosDTO(topZonas)
        )
    );
  }

  // ==================
  // CLASES PRIVADAS
  // ==================

  private static class CuentasAcumulador {
    long creados = 0;
    long eliminados = 0;
    long passwordRestablecidas = 0;
    long passwordCambiadas = 0;
    long estadosModificados = 0;
    long rolesModificados = 0;
    long ownerRecuperaciones = 0;
    long ownerTransferencias = 0;

    CuentasDTO toCuentasDTO() {
      return new CuentasDTO(
          creados,
          eliminados,
          passwordRestablecidas,
          passwordCambiadas,
          estadosModificados,
          rolesModificados,
          ownerRecuperaciones,
          ownerTransferencias
      );
    }
  }

  private static class EdicionAcumulador {
    long cambioDniSiMismo = 0;
    long cambioDniOtros = 0;
    long cambioNombreApellidoSiMismo = 0;
    long cambioNombreApellidoOtros = 0;

    void registrarDni(Long usuarioAfectado) {
      if (usuarioAfectado == null) {
        cambioDniSiMismo++;
      } else {
        cambioDniOtros++;
      }
    }

    void registrarNombreApellido(Long  usuarioAfectado) {
      if (usuarioAfectado == null) {
        cambioNombreApellidoSiMismo++;
      }  else {
        cambioNombreApellidoOtros++;
      }
    }

    DatosDTO toDatosDTO() {
      EdicionDTO dniDTO = new EdicionDTO(
          cambioDniSiMismo + cambioDniOtros,
          cambioDniSiMismo,
          cambioDniOtros
      );

      EdicionDTO nombreApellidoDTO = new EdicionDTO(
          cambioNombreApellidoSiMismo + cambioNombreApellidoOtros,
          cambioNombreApellidoSiMismo,
          cambioNombreApellidoOtros
      );

      return new DatosDTO(
          dniDTO,
          nombreApellidoDTO
      );
    }
  }

  private static class ReporteContador {
    long creados = 0;
    long atendidos = 0;
    long modificados = 0;
    long cerradosManual = 0;
    long cerradosAutomatico = 0;

    ReportesDTO toReporteDTO(List<RendimientoDTO> rendimientoDTO, List<TopEspaciosDTO> topEspaciosDTO) {
      return new ReportesDTO(
          creados,
          atendidos,
          modificados,
          cerradosManual,
          cerradosAutomatico,
          rendimientoDTO,
          topEspaciosDTO
      );
    }
  }

  private static class Rendimiento {

    // Contadores por tipo
    final Map<TipoReporte, Long> creados = new HashMap<>();
    final Map<TipoReporte, Long> cerrados= new HashMap<>();

    // Mapa para emparejar tiempo (reporteId -> fechaCreacion)
    final Map<Long, LocalDateTime> tiemposCreacion = new HashMap<>();

    // Acumuladores de tiempo para el promedio
    final Map<TipoReporte, Long> minutosTotales = new HashMap<>();
    final Map<TipoReporte, Long> reportesConTiempo = new HashMap<>();

    void registrarCreacion(TipoReporte tipo, Long reporteId, LocalDateTime fecha) {

      if (tipo == null) {
        return;
      }

      creados.merge(tipo, 1L, Long::sum);

      if (reporteId != null) {
        tiemposCreacion.putIfAbsent(reporteId, fecha);
      }
    }

    void registrarCierre(TipoReporte tipo, Long reporteId, LocalDateTime fechaCierre) {

      if (tipo == null) {
        return;
      }

      cerrados.merge(tipo, 1L, Long::sum);

      if (reporteId != null) {
        LocalDateTime fechaCreacion = tiemposCreacion.get(reporteId);

        if (fechaCreacion != null) {
          long minutos = Duration.between(fechaCreacion, fechaCierre).toMinutes();

          minutosTotales.merge(tipo, minutos, Long::sum);
          reportesConTiempo.merge(tipo, 1L, Long::sum);
        }
      }
    }

    List<RendimientoDTO> toListRendimientoDTO() {

      return Arrays.stream(TipoReporte.values())
          .map(tipo -> {
            long totalCreados = creados.getOrDefault(tipo, 0L);
            long totalCerrados = cerrados.getOrDefault(tipo, 0L);

            long minutos = minutosTotales.getOrDefault(tipo, 0L);
            long divisibles = reportesConTiempo.getOrDefault(tipo, 0L);

            double promedio = divisibles > 0
                ? (double) minutos / divisibles
                : 0.0;

            return new RendimientoDTO(tipo, totalCreados, totalCerrados, promedio);
          })
          .toList();
    }
  }

  private static class ZonaImpactoContador {

    private final EspacioRepository espacioRepository;

    public ZonaImpactoContador(EspacioRepository espacioRepository) {
      this.espacioRepository = espacioRepository;
    }

    // Key: espacioId | Long: contador
    private final Map<Long, Long> tipoAccesoBloqueado = new HashMap<>();
    private final Map<Long, Long> tipoProblemaSenaletica = new HashMap<>();
    private final Map<Long, Long> tipoBarreraFisica = new HashMap<>();
    private final Map<Long, Long> tipoDificultadOrientacion = new HashMap<>();
    private final Map<Long, Long> tipoOtros = new HashMap<>();

    void registrarImpacto(TipoReporte tipo, Long espacioId) {

      if (tipo == null || espacioId == null) {
        return;
      }

      switch (tipo) {
        case ACCESO_BLOQUEADO -> tipoAccesoBloqueado.merge(espacioId, 1L, Long::sum);
        case PROBLEMA_SENALETICA -> tipoProblemaSenaletica.merge(espacioId, 1L, Long::sum);
        case BARRERA_FISICA -> tipoBarreraFisica.merge(espacioId, 1L, Long::sum);
        case DIFICULTAD_ORIENTACION -> tipoDificultadOrientacion.merge(espacioId, 1L, Long::sum);
        case OTROS -> tipoOtros.merge(espacioId, 1L, Long::sum);
      }
    }

    List<TopEspaciosDTO> toListTopEspaciosDTO(long top) {

      List<TopEspaciosDTO> topEspacios = new ArrayList<>();

      // Ordenar la listas por contador y obtener el top solicitado
      Map<Long, Long> topAccesoBloqueado = topOrdenado(top, tipoAccesoBloqueado);
      Map<Long, Long> topProblemaSenaletica = topOrdenado(top, tipoProblemaSenaletica);
      Map<Long, Long> topBarreraFisica = topOrdenado(top, tipoBarreraFisica);
      Map<Long, Long> topDificultadOrientacion = topOrdenado(top, tipoDificultadOrientacion);
      Map<Long, Long> topOtros = topOrdenado(top, tipoOtros);

      // Juntar todos los ID top
      Set<Long> todosLosIdsTop = new HashSet<>();
      todosLosIdsTop.addAll(topAccesoBloqueado.keySet());
      todosLosIdsTop.addAll(topProblemaSenaletica.keySet());
      todosLosIdsTop.addAll(topBarreraFisica.keySet());
      todosLosIdsTop.addAll(topDificultadOrientacion.keySet());
      todosLosIdsTop.addAll(topOtros.keySet());

      if (todosLosIdsTop.isEmpty()) {
        return topEspacios;
      }

      List<EspacioNombreProjection> proyecciones = espacioRepository.findNombresByIds(todosLosIdsTop);

      Map<Long, String> mapaNombres = proyecciones.stream()
          .collect(Collectors.toMap(
             EspacioNombreProjection::getId,
             EspacioNombreProjection::getNombre,
             (existente, nuevo)  -> existente
          ));

      // Armar la lista de cada top
      topEspacios.add(toTopEspaciosDTO(TipoReporte.ACCESO_BLOQUEADO, topAccesoBloqueado, mapaNombres));
      topEspacios.add(toTopEspaciosDTO(TipoReporte.PROBLEMA_SENALETICA, topProblemaSenaletica, mapaNombres));
      topEspacios.add(toTopEspaciosDTO(TipoReporte.BARRERA_FISICA, topBarreraFisica, mapaNombres));
      topEspacios.add(toTopEspaciosDTO(TipoReporte.DIFICULTAD_ORIENTACION, topDificultadOrientacion, mapaNombres));
      topEspacios.add(toTopEspaciosDTO(TipoReporte.OTROS, topOtros, mapaNombres));

      return topEspacios;
    }

    private Map<Long, Long> topOrdenado(long top, Map<Long, Long> original) {

      if (original == null || original.isEmpty()) {
        return Map.of();
      }

      return original.entrySet().stream()
          .sorted(Map.Entry.<Long, Long>comparingByValue().reversed())
          .limit(top)
          .collect(Collectors.toMap(
              Map.Entry::getKey,
              Map.Entry::getValue,
              (a, b) -> a,
              LinkedHashMap::new
          ));
    }

    private TopEspaciosDTO toTopEspaciosDTO(
        TipoReporte tipo,
        Map<Long, Long> espacios,
        Map<Long, String> mapaNombres
    ) {

      List<EspacioDTO> espaciosDTO = new ArrayList<>();

      espacios.forEach((espacioId, cantidad) -> {
        String nombre = mapaNombres.getOrDefault(espacioId, "Espacio no encontrado");

        espaciosDTO.add(new EspacioDTO(
           espacioId,
           nombre,
           cantidad
        ));
      });

      return new TopEspaciosDTO(
          tipo,
          espaciosDTO
      );
    }
  }

}
