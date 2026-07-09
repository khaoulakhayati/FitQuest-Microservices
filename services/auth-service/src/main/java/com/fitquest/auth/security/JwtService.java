package com.fitquest.auth.security;

import com.fitquest.auth.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtService {

    @Value("${fitquest.jwt.secret}")
    private String secret;

    @Value("${fitquest.jwt.expiration-ms}")
    private long expirationMs;

    @Value("${fitquest.jwt.refresh-expiration-ms}")
    private long refreshExpirationMs;

    public String generateAccessToken(User user) {
        return generateAccessToken(user, "local");
    }

    public String generateAccessToken(User user, String identityProvider) {
        return buildToken(user, expirationMs, identityProvider);
    }

    public String generateRefreshToken(User user) {
        return buildToken(user, refreshExpirationMs, "local");
    }

    public String extractUserId(String token) {
        return parseClaims(token).getSubject();
    }

    public boolean isTokenValid(String token, User user) {
        String userId = extractUserId(token);
        return userId.equals(String.valueOf(user.getId())) && !isExpired(token);
    }

    private String buildToken(User user, long expiry, String identityProvider) {
        String roles = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.joining(","));
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("username", user.getUsername())
                .claim("roles", roles)
                .claim("identity_provider", identityProvider)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(getKey())
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser().verifyWith(getKey()).build().parseSignedClaims(token).getPayload();
    }

    private boolean isExpired(String token) {
        return parseClaims(token).getExpiration().before(new Date());
    }

    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
