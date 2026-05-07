package com.example.auth.global.client.dto.request;

public record UserGoogleOauthRequest(
        String providerId,
        String email,
        Boolean emailVerified,
        String name,
        String picture
) {
}
