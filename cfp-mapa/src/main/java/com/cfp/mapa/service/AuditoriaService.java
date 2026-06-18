package com.cfp.mapa.service;

import com.cfp.mapa.dto.auditoria.AuditoriaResponseDTO;
import com.cfp.mapa.model.Usuario;
import org.springframework.data.domain.Page;

public interface AuditoriaService {

  Page<AuditoriaResponseDTO> listarHistorialPaginado(int pagina, int tamaño, String direccion);

  void registrarAccion(Usuario operador, Usuario afectado, String accion);
}
