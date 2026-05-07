package com.example.auth.dto.response;

import com.example.auth.global.client.dto.UserAuthResponse;

public record SignInResponse(
        String name,
        String role,
        String access_token,
        String refresh_token,
        String expires_in
) {
    public static SignInResponse from(
            UserAuthResponse user,
            String role,
            String accessToken,
            String refreshToken,
            long expiresInSeconds
    ) {
        return new SignInResponse(
                user.name(),
                role,
                accessToken,
                refreshToken,
                String.valueOf(expiresInSeconds)
        );
    }
}
