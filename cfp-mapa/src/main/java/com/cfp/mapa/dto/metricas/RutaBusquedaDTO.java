package com.cfp.mapa.dto.metricas;

public record RutaBusquedaDTO(
    Long desdeId,
    String nombreDesde,
    Long hastaId,
    String nombreHasta,
    long cantidad
) {

}