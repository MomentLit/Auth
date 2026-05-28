package com.example.auth.global.client.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserGoogleOauthRequest(
        @JsonProperty("provider_id")
        String providerId,

        String email,

        @JsonProperty("email_verified")
        Boolean emailVerified,

        String name,

        @JsonProperty("image_url")
        String imageUrl
) {
}
