package com.cfp.mapa.security.jwt;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import com.cfp.mapa.security.user.UsuarioDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class JwtProvider {

  @Value("${app.jwt.secret}")
  private String jwtSecret;

  @Value("${app.jwt.expiration-ms}")
  private Long jwtExpirationMs;

  private SecretKey secretKey;

  @PostConstruct
  private void init() {
    this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(jwtSecret));
  }

  public String generarToken(Authentication authentication) {

    UsuarioDetails usuarioPrincipal = (UsuarioDetails) authentication.getPrincipal();

    List<String> roles = usuarioPrincipal.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList();

    Date fechaActual = new Date();
    Date fechaExpiracion = new Date(fechaActual.getTime() + jwtExpirationMs);

    return Jwts.builder()
        .subject(usuarioPrincipal.getUsername())
        .claim("id", usuarioPrincipal.getId())
        .claim("roles", roles)
        .issuedAt(fechaActual)
        .expiration(fechaExpiracion)
        .signWith(secretKey)
        .compact();
  }

  public boolean validarToken(String token) {

    try {
      Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token);

      return true;
    } catch (Exception e) {
      logJwtError(e);
    }

    return false;
  }

  // Extraer ID + DNI + Roles
  public UsuarioAutenticadoDTO obtenerUsuarioDesdeToken(String token) {

    try {

      Claims claims = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload();

      // Extraer ID
      Long idUsuario = claims.get("id", Long.class);

      // Extraer DNI
      String dniUsuario = claims.getSubject();

      // Extraer roles
      List<?> rolesRaw = claims.get("roles", List.class);
      List<GrantedAuthority> authorities = List.of();

      if (rolesRaw != null) {
        authorities = rolesRaw.stream()
            .map(role -> new SimpleGrantedAuthority(role.toString()))
            .collect(Collectors.toList());
      }

      return new UsuarioAutenticadoDTO(idUsuario, dniUsuario, authorities);
    } catch (Exception e) {
      logJwtError(e);
      return null;
    }
  }

  private void logJwtError(Exception e) {
    String errorType = e.getClass().getSimpleName();
    String message = e.getMessage();

    switch (e) {
      case SignatureException sE ->
          log.error("Firma JWT inválida ({}): {}", errorType, message);
      case MalformedJwtException mE ->
          log.error("Token JWT malformado ({}): {}", errorType, message);
      case ExpiredJwtException eE ->
          log.error("Token JWT expirado ({}): {}", errorType, message);
      case UnsupportedJwtException uE ->
          log.error("JWT no soportado ({}): {}", errorType, message);
      case IllegalArgumentException iAE ->
          log.error("Cadena JWT vacía o nula ({}): {}", errorType, message);
      default -> log.error("Error JWT desconocido ({}): {}", errorType, message);
    }
  }
}
