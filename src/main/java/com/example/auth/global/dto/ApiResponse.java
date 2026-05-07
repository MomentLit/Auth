package com.example.auth.global.dto;

public record ApiResponse<T> (
    String message,
    T data
){}
