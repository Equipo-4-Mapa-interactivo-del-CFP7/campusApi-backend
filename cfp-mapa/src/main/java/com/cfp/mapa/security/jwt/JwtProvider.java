package com.cfp.mapa.security.jwt;

import com.cfp.mapa.security.user.UsuarioDetails;
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

  public String getDniDelToken(String token) {

    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .getSubject();
    } catch (Exception e) {
      logJwtError(e);
    }

    return null;
  }

  public Long getIdDelToken(String token) {

    try {
      return Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("id", Long.class);
    } catch (Exception e) {
      logJwtError(e);
    }

    return null;
  }

  public List<GrantedAuthority> getRolesDelToken(String token) {

    try {

      List<?> rolesRaw = Jwts.parser()
          .verifyWith(secretKey)
          .build()
          .parseSignedClaims(token)
          .getPayload()
          .get("roles", List.class);

      if (rolesRaw == null) {
        return List.of();
      }

      return rolesRaw.stream()
          .map(role -> new SimpleGrantedAuthority(role.toString()))
          .collect(Collectors.toList());
    }  catch (Exception e) {
      logJwtError(e);
    }

    return List.of();
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
