package com.example.auth.dto.response;

public record OauthGoogleCallbackResponse(
        String name,
        String role,
        String access_token,
        String refresh_token,
        Number expires_in
) {
}
