package com.open.crm.security;

import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfigurationSource;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.open.crm.config.JwtProperties;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CorsConfigurationSource corsConfigurationSource)
            throws Exception {
        return http
            .authorizeHttpRequests(request -> request.requestMatchers("/api/auth/**", "/login", "/error")
                .permitAll()
                .requestMatchers("/api-docs/**", "/swagger-ui/**")
                .permitAll()
                .requestMatchers("/api/**")
                .authenticated()
                .anyRequest()
                .authenticated())
            .formLogin(AbstractHttpConfigurer::disable)
            .logout(LogoutConfigurer::permitAll)
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            // .ignoringRequestMatchers("/api/**", "/auth/**")
            // .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // )
            .build();
    }

    @Bean
    public JwtDecoder jwtAccessDecoder(JwtProperties jwtProperties) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
        return NimbusJwtDecoder.withSecretKey(new SecretKeySpec(keyBytes, "HmacSHA256"))
            .macAlgorithm(MacAlgorithm.HS256)
            .build();
    }

    @Bean
    public JwtEncoder jwtEncoder(JwtProperties jwtProperties) {
        byte[] keyBytes = Base64.getDecoder().decode(jwtProperties.getSecret());
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(keyBytes).keyUse(KeyUse.SIGNATURE)
            .algorithm(JWSAlgorithm.HS256)
            .keyID("hmac-main")
            .build();
        return new NimbusJwtEncoder(new ImmutableJWKSet<>(new JWKSet(jwk)));
    }

    @Bean
    public JwsHeader jwtHeaders() {
        return JwsHeader.with(MacAlgorithm.HS256).build();
    }

}
