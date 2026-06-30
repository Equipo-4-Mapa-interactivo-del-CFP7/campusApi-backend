package com.cfp.mapa.dto.recorrido;

public record RutaRequestDTO(

        Long origenId,
        Long destinoId,
        Boolean soloAccesible
){ }
