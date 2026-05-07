package com.example.auth.global.client;

import com.example.auth.dto.request.SignInRequest;
import com.example.auth.global.client.dto.response.GoogleUserInfoResponse;
import com.example.auth.global.client.dto.response.UserAuthResponse;
import com.example.auth.global.client.dto.request.UserGoogleOauthRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(
            @Value("${user-service.base-url:http://localhost:8081}") String userServiceBaseUrl //임시
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    public UserAuthResponse authenticate(SignInRequest request) {
        return restClient.post()
                .uri("/internal/users/authenticate")
                .body(request)
                .retrieve()
                .body(UserAuthResponse.class);
    }

    public UserAuthResponse authenticateGoogle(GoogleUserInfoResponse request) {
        UserGoogleOauthRequest userRequest = new UserGoogleOauthRequest(
                request.providerId(),
                request.email(),
                request.emailVerified(),
                request.name(),
                request.picture()
        );

        return restClient.post()
                .uri("/internal/users/oauth/google")
                .body(userRequest)
                .retrieve()
                .body(UserAuthResponse.class);
    }
}
