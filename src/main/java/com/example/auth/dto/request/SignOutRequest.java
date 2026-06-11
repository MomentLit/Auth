package com.example.auth.dto.request;

public record SignOutRequest(
        String refresh_token
) {}
