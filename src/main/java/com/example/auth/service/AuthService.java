package com.example.auth.service;

import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.SignInRequest;
import com.example.auth.dto.request.SignOutRequest;
import com.example.auth.dto.response.OauthGoogleCallbackResponse;
import com.example.auth.dto.response.RefreshResponse;
import com.example.auth.dto.response.SignInResponse;
import com.example.auth.global.client.GoogleOauthClient;
import com.example.auth.global.client.UserServiceClient;
import com.example.auth.global.client.dto.response.GoogleTokenResponse;
import com.example.auth.global.client.dto.response.GoogleUserInfoResponse;
import com.example.auth.global.client.dto.response.UserAuthResponse;
import java.net.URI;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private final GoogleOauthClient googleOauthClient;
    private final UserServiceClient userServiceClient;
    private final AuthValidator authValidator;
    private final RoleProcessor roleProcessor;
    private final TokenService tokenService;

    public AuthService(
            GoogleOauthClient googleOauthClient,
            UserServiceClient userServiceClient,
            AuthValidator authValidator,
            RoleProcessor roleProcessor,
            TokenService tokenService
    ) {
        this.googleOauthClient = googleOauthClient;
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

        String role =
                roleProcessor.normalizeRole(user.role());

        TokenService.TokenPair tokenPair =
                tokenService.issueTokens(
                        user.userId().toString(),
                        role
                );

        return new SignInResponse(
                user.name(),
                role,
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                String.valueOf(tokenService.accessTokenExpiresInSeconds())
        );
    }

    // Google OAuth 로그인 페이지로 이동할 URL 생성
    public URI createGoogleAuthorizationUri(String state) {
        return googleOauthClient.createAuthorizationUri(state);
    }

    // Google OAuth callback code로 사용자 인증 후 JWT 발급
    public OauthGoogleCallbackResponse loginWithGoogle(String code, String state) {

        authValidator.validateGoogleAuthorizationCode(code);

        GoogleTokenResponse googleToken =
                googleOauthClient.requestToken(code);

        if (googleToken == null || !StringUtils.hasText(googleToken.accessToken())) {
            throw new IllegalArgumentException("Google Access Token을 발급받을 수 없습니다.");
        }

        GoogleUserInfoResponse googleUser =
                googleOauthClient.requestUserInfo(googleToken.accessToken());

        UserAuthResponse user =
                userServiceClient.authenticateGoogle(googleUser);

        authValidator.validateAuthenticatedUser(user);

        String role =
                roleProcessor.normalizeRole(user.role());

        TokenService.TokenPair tokenPair =
                tokenService.issueTokens(
                        user.userId().toString(),
                        role
                );

        return new OauthGoogleCallbackResponse(
                user.name(),
                role,
                tokenPair.accessToken(),
                tokenPair.refreshToken(),
                tokenService.accessTokenExpiresInSeconds()
        );
    }

    // 토큰 재발급
    public RefreshResponse refresh(RefreshRequest request) {

        authValidator.validateRefreshRequest(request);

        String subject =
                tokenService.getValidRefreshTokenSubject(
                        request.refreshToken()
                );

        String role =
                tokenService.getRole(request.refreshToken());

        TokenService.TokenPair tokenPair =
                tokenService.issueTokens(subject, role);

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
