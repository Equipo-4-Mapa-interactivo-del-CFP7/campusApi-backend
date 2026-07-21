package com.cfp.mapa.repository.projection;

import com.cfp.mapa.model.enums.TipoAccionAuditoria;

public interface CantidadPorAccionProjection {
  TipoAccionAuditoria getAccion();
  Long getCantidad();
}