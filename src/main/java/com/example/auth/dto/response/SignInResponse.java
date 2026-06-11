package com.example.auth.dto.response;

public record SignInResponse(
        String name,
        String role,
        String access_token,
        String refresh_token,
        String expires_in
) {
}
