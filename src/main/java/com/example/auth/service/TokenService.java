package com.example.auth.service;

import com.example.auth.global.security.JwtProvider;
import com.example.auth.infra.RefreshTokenRepository;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public TokenService(
            JwtProvider jwtProvider,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public TokenPair issueTokens(
            String subject,
            String role
    ) {

        String accessToken =
                jwtProvider.createAccessToken(subject, role);

        String refreshToken =
                jwtProvider.createRefreshToken(subject, role);

        refreshTokenRepository.save(
                subject,
                refreshToken,
                jwtProvider.getRefreshTokenExpirationMillis()
        );

        return new TokenPair(
                accessToken,
                refreshToken
        );
    }

    public long accessTokenExpiresInSeconds() {
        return jwtProvider.getAccessTokenExpirationMillis() / 1000;
    }

    public String getValidRefreshTokenSubject(String refreshToken) {
        jwtProvider.validateToken(refreshToken);

        String subject = jwtProvider.getSubject(refreshToken);

        if (!refreshTokenRepository.existsBySubjectAndToken(subject, refreshToken)) {
            throw new IllegalArgumentException(
                    "저장된 Refresh Token과 일치하지 않습니다."
            );
        }

        return subject;
    }

    public String getRole(String token) {
        return jwtProvider.getRole(token);
    }

    public void deleteRefreshToken(String refreshToken) {
        String subject =
                getValidRefreshTokenSubject(refreshToken);

        refreshTokenRepository.deleteBySubject(subject);
    }

    public record TokenPair(
            String accessToken,
            String refreshToken
    ) {}
}
