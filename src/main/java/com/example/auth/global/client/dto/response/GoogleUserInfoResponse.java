package com.example.auth.global.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GoogleUserInfoResponse(
        @JsonProperty("sub")
        String providerId,

        String email,

        @JsonProperty("email_verified")
        Boolean emailVerified,

        String name,

        @JsonProperty("picture")
        String imageUrl
) {
}
