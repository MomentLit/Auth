package com.example.auth.service;

import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.SignInRequest;
import com.example.auth.dto.request.SignOutRequest;
import com.example.auth.dto.response.RefreshResponse;
import com.example.auth.dto.response.SignInResponse;
import com.example.auth.global.client.UserServiceClient;
import com.example.auth.global.client.dto.UserAuthResponse;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserServiceClient userServiceClient;
    private final AuthValidator authValidator;
    private final RoleProcessor roleProcessor;
    private final TokenService tokenService;

    public AuthService(
            UserServiceClient userServiceClient,
            AuthValidator authValidator,
            RoleProcessor roleProcessor,
            TokenService tokenService
    ) {
        this.userServiceClient = userServiceClient;
        this.authValidator = authValidator;
        this.roleProcessor = roleProcessor;
        this.tokenService = tokenService;
    }

    // 로그인
    public SignInResponse login(SignInRequest request) {

        authValidator.validateLoginRequest(request);

        UserAuthResponse user =
                userServiceClient.authenticate(request);

        authValidator.validateAuthenticatedUser(user);

        List<String> roles =
                roleProcessor.normalizeRoles(
                        user.roles(),
                        user.role()
                );

        TokenService.TokenPair tokenPair =
                tokenService.issueTokens(
                        user.userId().toString(),
                        roles
                );

        return new SignInResponse(
                user.name(),
                roleProcessor.firstRole(roles),
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                String.valueOf(
                        tokenService.accessTokenExpiresInSeconds()
                )
        );
    }

    // 토큰 재발급
    public RefreshResponse refresh(RefreshRequest request) {

        authValidator.validateRefreshRequest(request);

        String subject =
                tokenService.getValidRefreshTokenSubject(
                        request.refreshToken()
                );

        List<String> roles =
                tokenService.getRoles(request.refreshToken());

        TokenService.TokenPair tokenPair =
                tokenService.issueTokens(subject, roles);

        return new RefreshResponse(
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                tokenService.accessTokenExpiresInSeconds()
        );
    }

    // 로그아웃
    public void logout(SignOutRequest request) {

        authValidator.validateSignOutRequest(request);

        tokenService.deleteRefreshToken(request.refresh_token());
    }
}
