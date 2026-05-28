package com.cfp.mapa.security.jwt;

import com.cfp.mapa.dto.usuario.UsuarioAutenticadoDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

  private final JwtProvider tokenProvider;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request,
      HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {

    try {

      String token = obtenerTokenDeRequest(request);

      if (StringUtils.hasText(token) && tokenProvider.validarToken(token)) {

        Long idUsuario = tokenProvider.getIdDelToken(token);
        String dniUsuario = tokenProvider.getDniDelToken(token);
        List<GrantedAuthority> authorities = tokenProvider.getRolesDelToken(token);

        UsuarioAutenticadoDTO usuarioPrincipal = new UsuarioAutenticadoDTO(
            idUsuario, dniUsuario, authorities
        );

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            usuarioPrincipal,
            null,
            authorities
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      log.error("No se pudo establecer la autenticación del usuario en el filtro: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {

    String path = request.getServletPath();
    return path.startsWith("/auth/");
  }

  private String obtenerTokenDeRequest(HttpServletRequest request) {

    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}
