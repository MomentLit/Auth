package com.example.auth.global.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationMillis;

    public JwtProvider(
            @Value("${jwt.secret:change-this-secret-key-for-local-development-only}") String secret,
            @Value("${jwt.access-token-expiration:30m}") Duration accessTokenExpiration,
            @Value("${jwt.refresh-token-expiration:14d}") Duration refreshTokenExpiration
    ) {
        this.secretKey = createSecretKey(secret);
        this.accessTokenExpirationMillis = accessTokenExpiration.toMillis();
        this.refreshTokenExpirationMillis = refreshTokenExpiration.toMillis();
    }

    public String createAccessToken(String subject, Collection<String> roles) {
        return createToken(subject, roles, accessTokenExpirationMillis);
    }

    public String createRefreshToken(String subject, Collection<String> roles) {
        return createToken(subject, roles, refreshTokenExpirationMillis);
    }

    public long getAccessTokenExpirationMillis() {
        return accessTokenExpirationMillis;
    }

    public long getRefreshTokenExpirationMillis() {
        return refreshTokenExpirationMillis;
    }

    // JWT 안의 subject와 roles를 Spring Security가 이해하는 Authentication 객체로 바꿉니다.
    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String subject = claims.getSubject();
        List<String> roles = claims.get("roles", List.class);

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .toList();

        return new UsernamePasswordAuthenticationToken(subject, null, authorities);
    }

    // 토큰 서명과 만료 시간을 검증합니다. 문제가 있으면 jjwt 예외가 발생합니다.
    public boolean validateToken(String token) {
        parseClaims(token);
        return true;
    }

    // 로그아웃이나 재발급에서 토큰 주인을 찾을 때 사용합니다.
    public String getSubject(String token) {
        return parseClaims(token).getSubject();
    }

    private String createToken(String subject, Collection<String> roles, long expirationMillis) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMillis);

        return Jwts.builder()
                .subject(subject)
                .claim("roles", roles)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey createSecretKey(String secret) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 32) {
            keyBytes = sha256(keyBytes);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    private byte[] sha256(byte[] value) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(value);
        } catch (Exception exception) {
            throw new IllegalStateException("JWT secret key를 생성할 수 없습니다.", exception);
        }
    }
}
