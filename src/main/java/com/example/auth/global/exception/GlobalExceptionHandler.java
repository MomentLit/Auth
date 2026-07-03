package com.example.auth.global.exception;

import com.example.auth.global.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> tokenNotFoundHandleException(TokenNotFoundException e) {
        log.warn("TokenNotFoundException: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("[ERROR: Auth/Token/NotFound] " + e.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse<String>> unauthorizedHandleException(UnauthorizedException e) {
        log.warn("UnauthorizedException: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail("[ERROR: Auth/Unauthorized] " + e.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<String>> badRequestHandleException(BadRequestException e) {
        log.warn("BadRequestException: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail("[ERROR: Request/BadRequest] " + e.getMessage()));
    }

    @ExceptionHandler(GoogleOauthException.class)
    public ResponseEntity<ApiResponse<String>> googleOauthHandleException(GoogleOauthException e) {
        log.error("GoogleOauthException", e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail("[ERROR: Auth/Oauth/Google] " + e.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<String>> authHandleException(AuthException e) {
        log.error("AuthException", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("[ERROR: Auth/?] " + e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<String>> globalHandleException(Exception e) {
        log.error("Unhandled exception", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("[ERROR: ?/?] 서버 내부 오류가 발생했습니다."));
    }
}
