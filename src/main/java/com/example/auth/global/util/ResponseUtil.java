package com.example.auth.global.util;

import com.example.auth.global.dto.ApiResponse;

public class ResponseUtil {
    public static <T> ApiResponse<T> success(String message,T data){
        return new ApiResponse<>(message,data);
    }
    public static ApiResponse<Void> success(String message){
        return new ApiResponse<>(message,null);
    }
}
