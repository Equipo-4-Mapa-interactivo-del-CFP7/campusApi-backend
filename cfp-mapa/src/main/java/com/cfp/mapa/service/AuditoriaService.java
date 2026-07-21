package com.cfp.mapa.service;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.model.Usuario;
import com.cfp.mapa.model.enums.TipoAccionAuditoria;
import com.cfp.mapa.model.enums.TipoReporte;
import java.util.List;
import org.springframework.data.domain.Page;

public interface AuditoriaService {

  Page<AuditoriaResponseDTO> listarHistorialPaginado(
      Long usuarioId,
      List<TipoAccionAuditoria> accion,
      Long reporteId,
      int page,
      int size,
      String order
  );

  void registrarAccion(
      Usuario operador,
      Usuario afectado,
      TipoAccionAuditoria accion
  );

  void registrarAccion(
      Usuario operador,
      Usuario afectado,
      TipoAccionAuditoria accion,
      Object detalles
  );

  void registrarAccion(
      Usuario operador,
      Usuario afectado,
      Long reporteId,
      Long reporteEspacioId,
      TipoReporte reporteTipo,
      TipoAccionAuditoria accion,
      Object detalles
  );

  void anonimizarUsuario(Long usuarioId, String textoAnonimo, String dniAnonimo);

}
