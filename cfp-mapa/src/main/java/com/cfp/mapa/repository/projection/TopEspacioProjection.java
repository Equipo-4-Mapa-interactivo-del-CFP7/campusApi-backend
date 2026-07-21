package com.cfp.mapa.repository.projection;

import com.cfp.mapa.model.enums.TipoReporte;

public interface TopEspacioProjection {
  TipoReporte getTipo();
  Long getEspacioId();
  Long getCantidad();
}
