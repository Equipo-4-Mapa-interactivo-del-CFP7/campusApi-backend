package com.cfp.mapa.repository.projection;

import com.cfp.mapa.model.enums.TipoReporte;

public interface ReporteConteoProjection {

  TipoReporte getTipo();
  Long getCantidad();
}
