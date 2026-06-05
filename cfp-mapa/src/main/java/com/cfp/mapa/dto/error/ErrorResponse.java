package com.cfp.mapa.dto.error;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;

public record ErrorResponse (

    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {

  public ErrorResponse (HttpStatus status, String message) {
    this (status.value(), status.getReasonPhrase(), message, LocalDateTime.now());
  }
}
