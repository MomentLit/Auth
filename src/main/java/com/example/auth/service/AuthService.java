package com.example.auth.service;

import com.example.auth.dto.request.SignInRequest;
import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.SignOutRequest;
import com.example.auth.dto.response.RefreshResponse;
import com.example.auth.dto.response.SignInResponse;
import com.example.auth.global.client.UserServiceClient;
import com.example.auth.global.client.dto.UserAuthResponse;
import com.example.auth.global.security.JwtProvider;
import com.example.auth.infra.RefreshTokenRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //의존성 주입
    public AuthService(
            UserServiceClient userServiceClient,
            JwtProvider jwtProvider,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.userServiceClient = userServiceClient;
        this.jwtProvider = jwtProvider;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    //로그인
    public SignInResponse login(SignInRequest request) {
        validateLoginRequest(request);

        // 사용자 검증은 User 서비스에 맡기고, 검증된 사용자 정보로 토큰을 발급합니다.
        UserAuthResponse user = userServiceClient.authenticate(request);
        validateAuthenticatedUser(user);

        String subject = String.valueOf(user.userId());
        List<String> roles = normalizeRoles(user.roles(), user.role());
        TokenPair tokenPair = issueTokens(subject, roles);

        return new SignInResponse(
                user.name(),
                firstRole(roles),
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                String.valueOf(toSeconds(jwtProvider.getAccessTokenExpirationMillis()))
        );
    }

    //리프레쉬 토큰
    public RefreshResponse refresh(RefreshRequest request) {
        validateRefreshToken(request.refreshToken());
        jwtProvider.validateToken(request.refreshToken());

        String subject = jwtProvider.getSubject(request.refreshToken());

        if (!refreshTokenRepository.existsBySubjectAndToken(subject, request.refreshToken())) {
            throw new IllegalArgumentException("저장된 Refresh Token과 일치하지 않습니다.");
        }

        // Refresh Token이 정상일 때 Access Token과 Refresh Token을 모두 새로 발급합니다.
        List<String> roles = jwtProvider.getAuthentication(request.refreshToken()).getAuthorities().stream()
                .map(authority -> authority.getAuthority())
                .toList();

        TokenPair tokenPair = issueTokens(subject, roles);
        return new RefreshResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                toSeconds(jwtProvider.getAccessTokenExpirationMillis())
        );
    }

    //로그아웃
    public void logout(SignOutRequest request) {
        validateSignOutRequest(request);
        jwtProvider.validateToken(request.refresh_token());
        String subject = jwtProvider.getSubject(request.refresh_token());

        // 저장된 Refresh Token을 제거하면 이후 재발급이 불가능해집니다.
        refreshTokenRepository.deleteBySubject(subject);
    }

    //토큰 발급
    private TokenPair issueTokens(String subject, List<String> roles) {
        String accessToken = jwtProvider.createAccessToken(subject, roles);
        String refreshToken = jwtProvider.createRefreshToken(subject, roles);

        refreshTokenRepository.save(subject, refreshToken, jwtProvider.getRefreshTokenExpirationMillis());

        return new TokenPair(
                accessToken,
                refreshToken
        );
    }


    /*
     내부 함수
    */
    private void validateLoginRequest(SignInRequest request) {
        if (request == null || !StringUtils.hasText(request.email()) || !StringUtils.hasText(request.password())) {
            throw new IllegalArgumentException("아이디와 비밀번호를 입력해주세요.");
        }
    }

    private void validateRefreshToken(String refreshToken) {
        if (!StringUtils.hasText(refreshToken)) {
            throw new IllegalArgumentException("Refresh Token을 입력해주세요.");
        }
    }

    private void validateSignOutRequest(SignOutRequest request) {
        if (request == null || !StringUtils.hasText(request.refresh_token())) {
            throw new IllegalArgumentException("Refresh Token을 입력해주세요.");
        }
    }

    private void validateAuthenticatedUser(UserAuthResponse user) {
        if (user == null || user.userId() == null) {
            throw new IllegalArgumentException("사용자 인증 정보를 확인할 수 없습니다.");
        }
    }

    private List<String> normalizeRoles(List<String> roles, String role) {
        if (roles == null || roles.isEmpty()) {
            if (StringUtils.hasText(role)) {
                return List.of(normalizeRole(role));
            }
            return List.of("ROLE_USER");
        }

        List<String> normalizedRoles = roles.stream()
                .filter(StringUtils::hasText)
                .map(this::normalizeRole)
                .distinct()
                .toList();

        if (normalizedRoles.isEmpty()) {
            return List.of("ROLE_USER");
        }

        return normalizedRoles;
    }

    private String normalizeRole(String role) {
        return role.startsWith("ROLE_") ? role : "ROLE_" + role;
    }

    private String firstRole(List<String> roles) {
        return roles.isEmpty() ? "ROLE_USER" : roles.getFirst();
    }

    private long toSeconds(long millis) {
        return millis / 1000;
    }

    private record TokenPair(
            String accessToken,
            String refreshToken
    ) {}
}
