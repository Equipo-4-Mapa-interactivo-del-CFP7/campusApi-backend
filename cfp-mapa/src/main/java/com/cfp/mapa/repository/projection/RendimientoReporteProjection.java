package com.cfp.mapa.repository.projection;

import com.cfp.mapa.model.enums.TipoReporte;

public interface RendimientoReporteProjection {
  TipoReporte getTipo();
  Long getCreados();
  Long getCerrados();
  Double getPromedioMinutos();
  Long getMaxMinutos();
}
