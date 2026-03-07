package com.open.crm.security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtEncodingException;
import org.springframework.stereotype.Service;

import com.open.crm.admin.entities.user.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtDecoder jwtDecoder;

    private final JwtEncoder jwtEncoder;

    private final JwsHeader jwtHeaders;

    private final Set<String> blockedToken = HashSet.newHashSet(0);

    public Jwt decodeToken(String token) throws TokenException {
        if (blockedToken.contains(token)) {
            throw new TokenException("Token is blocked");
        }
        return jwtDecoder.decode(token);
    }

    public Jwt generateRefreshToken(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder().subject(user.getEmail()).claim("type", "refresh").build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtHeaders, claims));
    }

    public Jwt generateAccessToken(User user) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .subject(user.getEmail())
            .claim("type", "access")
            .claim("tenant_id", user.getTenant().getId().toString())
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtHeaders, claims));
    }

}
