package com.open.crm.security;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.open.crm.admin.application.interfaces.ITenantRepository;
import com.open.crm.admin.application.interfaces.IUserRepository;
import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.User;
import com.open.crm.config.JwtProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final JwtProperties jwtProperties;

    private final JwtDecoder jwtDecoder;

    private final JwtEncoder jwtEncoder;

    private final JwsHeader jwtHeaders;

    private final IUserRepository userRepository;
    private final ITenantRepository tenantRepository;

    private final Set<String> blockedToken = HashSet.newHashSet(0);

    public Jwt decodeToken(String token) throws TokenException {
        Jwt jwt = jwtDecoder.decode(token);

        if (Objects.isNull(jwt)) {
            throw new TokenException("Invalid token");
        }

        if (Objects.isNull(jwt)
                || !"access".equals(jwt.getClaimAsString("type")) && !"refresh".equals(jwt.getClaimAsString("type"))
                || Objects.isNull(jwt.getId())) {
            throw new TokenException("Invalid token");
        }

        if (blockedToken.contains(jwt.getId())) {
            throw new TokenException("Token is blocked");
        }
        return jwt;
    }

    public Optional<TokenType> getType(Jwt jwt) {
        String typeStr = jwt.getClaimAsString(CLAIM_TYPE);
        if (Objects.isNull(typeStr))
            return Optional.empty();

        try {
            return Optional.of(TokenType.fromValue(typeStr));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<Tenant> getTenantIdFromToken(Jwt jwt) {
        try {
            String tenantIdStr = jwt.getClaimAsString(CLAIM_TENANT_ID);
            UUID tenantId = UUID.fromString(tenantIdStr);

            return tenantRepository.findById(tenantId);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public Optional<User> getUserFromToken(Jwt jwt) {
        String email = jwt.getSubject();
        return userRepository.findByEmail(email);
    }

    public TokenData generateTokenPairs(User user) {
        Jwt refreshToken = generateRefreshToken(user);
        Jwt accessToken = generateAccessToken(user, refreshToken);

        return new TokenData(accessToken, refreshToken);
    }

    public TokenData refreshTokne(String refreshToken) throws TokenException {
        Jwt decodedRefreshToken = decodeToken(refreshToken);

        String email = decodedRefreshToken.getSubject();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new TokenException("User not found"));
        blockedToken.add(decodedRefreshToken.getId());

        return generateTokenPairs(user);
    }

    public void blockToken(Jwt jwt) {
        blockedToken.add(jwt.getId());
    }

    private Jwt generateRefreshToken(User user) {
        UUID tokenId = UUID.randomUUID();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(tokenId.toString())
                .subject(user.getEmail())
                .claim(CLAIM_TYPE, TokenType.REFRESH.getValue())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtProperties.getRefreshExpires()))
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtHeaders, claims));
    }

    private Jwt generateAccessToken(User user, Jwt refreshToken) {
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .id(refreshToken.getId())
                .subject(user.getEmail())
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusMillis(jwtProperties.getAccessExpires()))
                .claim(CLAIM_TYPE, TokenType.ACCESS.getValue())
                .claim(CLAIM_AUTHORITIES,
                        user.getAuthorities().stream().map(a -> a.getAuthority()).toList())
                .claim(CLAIM_TENANT_ID, user.getTenant().getId())
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(jwtHeaders, claims));
    }

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_AUTHORITIES = "authorities";
    private static final String CLAIM_TENANT_ID = "tenantId";
}
