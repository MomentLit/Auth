package com.example.auth.global.client.dto;

import java.util.List;

public record UserAuthResponse(
        Long userId,
        String name,
        String role,
        List<String> roles
) {}
