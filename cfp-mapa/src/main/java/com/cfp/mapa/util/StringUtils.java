package com.cfp.mapa.util;

public final class StringUtils {

  private StringUtils() {
    throw new IllegalStateException("Utility class");
  }

  public static String normalizarNombre(String nombre){

    if (nombre == null || nombre.isBlank()) {
      return nombre;
    }

    String textoLimpio = nombre.trim().replaceAll("\\s+", " ")  ;

    String[] palabras = textoLimpio.split(" ");
    StringBuilder resultado = new StringBuilder();

    for (String palabra : palabras) {
      if (!palabra.isEmpty()) {
        resultado.append(Character.toUpperCase(palabra.charAt(0)))
            .append(palabra.substring(1).toLowerCase())
            .append(" ");
      }
    }

    return resultado.toString().trim();
  }

  public static String normalizarDni(String dni){

    if (dni == null || dni.isBlank()) {
      return dni;
    }

    dni = dni.trim();

    if (dni.length() == 7) {
      dni = "0"  + dni;
    }

    return dni;
  }
}
