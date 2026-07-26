package com.example.auth.global.client.dto.request;

import com.example.auth.global.client.dto.response.OauthUserProfile;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UserOauthRequest(
        String provider,

        @JsonProperty("provider_id")
        String providerId,

        String email,

        @JsonProperty("email_verified")
        Boolean emailVerified,

        String name,

        @JsonProperty("image_url")
        String imageUrl
) {
    public static UserOauthRequest from(OauthUserProfile profile) {
        return new UserOauthRequest(
                profile.provider(),
                profile.providerId(),
                profile.email(),
                profile.emailVerified(),
                profile.name(),
                profile.imageUrl()
        );
    }
}
