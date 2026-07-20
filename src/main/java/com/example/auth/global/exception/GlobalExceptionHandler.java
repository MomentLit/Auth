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

    @ExceptionHandler(NaverOauthException.class)
    public ResponseEntity<ApiResponse<String>> naverOauthHandleException(NaverOauthException e) {
        log.error("NaverOauthException", e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail("[ERROR: Auth/Oauth/Naver] " + e.getMessage()));
    }

    @ExceptionHandler(KakaoOauthException.class)
    public ResponseEntity<ApiResponse<String>> kakaoOauthHandleException(KakaoOauthException e) {
        log.error("KakaoOauthException", e);

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail("[ERROR: Auth/Oauth/Kakao] " + e.getMessage()));
    }

    @ExceptionHandler(DownstreamServiceException.class)
    public ResponseEntity<ApiResponse<String>> downstreamServiceHandleException(
            DownstreamServiceException e
    ) {
        if (e.getStatusCode().is4xxClientError()) {
            log.warn(
                    "Downstream service client error. service={}, status={}, body={}",
                    e.getServiceName(),
                    e.getStatusCode(),
                    e.getResponseBody()
            );

            return ResponseEntity.status(e.getStatusCode())
                    .body(ApiResponse.fail(e.getMessage()));
        }

        if (e.getStatusCode().isSameCodeAs(HttpStatus.SERVICE_UNAVAILABLE)) {
            log.error(
                    "Downstream service unavailable. service={}, body={}",
                    e.getServiceName(),
                    e.getResponseBody()
            );

            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ApiResponse.fail("[ERROR: Auth/Downstream/" + e.getServiceName()
                            + "] " + e.getServiceName() + " 서비스에 연결할 수 없습니다."));
        }

        log.error(
                "Downstream service server error. service={}, status={}, body={}",
                e.getServiceName(),
                e.getStatusCode(),
                e.getResponseBody()
        );

        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail("[ERROR: Auth/Downstream/" + e.getServiceName()
                        + "] " + e.getServiceName() + " 서비스 호출 중 오류가 발생했습니다."));
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
