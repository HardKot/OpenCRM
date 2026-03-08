package com.open.crm.security;

import org.springframework.security.oauth2.jwt.Jwt;

public record TokenData(
        String accessToken,
        String refreshToken) {

    public TokenData(Jwt accessToken, Jwt refreshToken) {
        this(accessToken.getTokenValue(), refreshToken.getTokenValue());
    }
}
