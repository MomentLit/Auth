package com.example.auth.service;

import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.SignInRequest;
import com.example.auth.dto.request.SignOutRequest;
import com.example.auth.global.client.dto.UserAuthResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AuthValidator {
    public void validateLoginRequest(SignInRequest request) {
        if (request == null || !StringUtils.hasText(request.email()) || !StringUtils.hasText(request.password())) {
            throw new IllegalArgumentException("아이디와 비밀번호를 입력해주세요.");
        }
    }

    public void validateRefreshRequest(RefreshRequest request) {
        if (request == null || !StringUtils.hasText(request.refreshToken())) {
            throw new IllegalArgumentException("Refresh Token을 입력해주세요.");
        }
    }

    public void validateSignOutRequest(SignOutRequest request) {
        if (request == null || !StringUtils.hasText(request.refresh_token())) {
            throw new IllegalArgumentException("Refresh Token을 입력해주세요.");
        }
    }

    public void validateAuthenticatedUser(UserAuthResponse user) {
        if (user == null || user.userId() == null) {
            throw new IllegalArgumentException("사용자 인증 정보를 확인할 수 없습니다.");
        }
    }
}
