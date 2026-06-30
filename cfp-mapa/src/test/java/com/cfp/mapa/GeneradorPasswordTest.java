package com.cfp.mapa;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class GeneradorPasswordTest {

  @Test
  void generarHash() {
    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    String passwordPlano = "tu_contraseña_aquí";
    String passwordHasheada = encoder.encode(passwordPlano);

    System.out.println("\n========================================");
    System.out.println("CONTRASENA HASHEADA: " + passwordHasheada);
    System.out.println("========================================\n");
  }
}