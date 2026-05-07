package com.example.auth.dto.response;

public record RefreshResponse(
        String access_token,
        String refresh_token,
        Number expires_in
) {
    public static RefreshResponse from(
            String accessToken,
            String refreshToken,
            long expiresInSeconds
    ) {
        return new RefreshResponse(
                accessToken,
                refreshToken,
                expiresInSeconds
        );
    }
}
