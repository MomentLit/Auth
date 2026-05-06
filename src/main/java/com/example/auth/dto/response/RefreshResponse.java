package com.example.auth.dto.response;

public record RefreshResponse(
        String access_token,
        String refresh_token,
        Number expires_in
) {
}
