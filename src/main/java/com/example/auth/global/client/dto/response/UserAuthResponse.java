package com.example.auth.global.client.dto.response;

import java.util.UUID;

public record UserAuthResponse(
        UUID userId,
        String name,
        String role
) {}
