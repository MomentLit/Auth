package com.example.auth.global.client.dto;

import java.util.UUID;

public record UserAuthResponse(
        UUID userId,
        String name,
        String role,
) {}
