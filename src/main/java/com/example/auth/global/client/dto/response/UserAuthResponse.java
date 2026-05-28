package com.example.auth.global.client.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public record UserAuthResponse(
        @JsonProperty("user_id")
        String userId,

        String name,

        String role
) {}
