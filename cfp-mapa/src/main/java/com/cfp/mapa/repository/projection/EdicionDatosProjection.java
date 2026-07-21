package com.cfp.mapa.repository.projection;

import com.cfp.mapa.model.enums.TipoAccionAuditoria;

public interface EdicionDatosProjection {
  TipoAccionAuditoria getAccion();
  Long getConteoSiMismo();
  Long getConteoOtros();
}
