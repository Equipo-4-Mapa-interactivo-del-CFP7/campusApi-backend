package com.cfp.mapa.dto.jwt;

public record JwtAuthResponseDTO(

    String accessToken,

    String tokenType
) {

  public JwtAuthResponseDTO(String accessToken) {
    this(accessToken, "Bearer");
  }
}
