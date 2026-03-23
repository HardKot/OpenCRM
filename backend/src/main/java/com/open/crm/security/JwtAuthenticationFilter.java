package com.open.crm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtDecoder jwtDecoder;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      chain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(7);
    try {
      Jwt jwt = jwtDecoder.decode(token);
      List<SimpleGrantedAuthority> authorities = extractAuthorities(jwt);

      UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(jwt, null, authorities);
      SecurityContextHolder.getContext().setAuthentication(authentication);
    } catch (JwtException e) {
      log.debug("JWT validation failed: {}", e.getMessage());
      // SecurityContext remains empty — protected endpoints will reject the request
    }

    chain.doFilter(request, response);
  }

  private List<SimpleGrantedAuthority> extractAuthorities(Jwt jwt) {
    List<String> authorities = jwt.getClaimAsStringList("authorities");
    if (authorities == null) {
      return Collections.emptyList();
    }
    return authorities.stream().map(SimpleGrantedAuthority::new).toList();
  }
}
