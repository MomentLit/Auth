package com.example.auth.global.client;

import com.example.auth.dto.request.SignInRequest;
import com.example.auth.global.client.dto.request.UserOauthRequest;
import com.example.auth.global.client.dto.response.OauthUserProfile;
import com.example.auth.global.client.dto.response.UserAuthResponse;
import com.example.auth.global.exception.DownstreamServiceException;
import com.example.auth.global.exception.UnauthorizedException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

@Slf4j
@Component
public class UserServiceClient {

    private static final String SERVICE_NAME = "USER";

    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserServiceClient(
            @Value("${user-service.base-url:http://localhost:8081}") String userServiceBaseUrl
    ) {
        this.restClient = RestClient.builder()
                .baseUrl(userServiceBaseUrl)
                .build();
    }

    public UserAuthResponse authenticate(SignInRequest request) {
        try {
            return restClient.post()
                    .uri("/internal/users/authenticate")
                    .body(request)
                    .retrieve()
                    .body(UserAuthResponse.class);

        } catch (RestClientResponseException e) {
            if (e.getStatusCode().isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
                throw new UnauthorizedException("이메일 또는 비밀번호가 일치하지 않습니다.");
            }

            throw convertToDownstreamException(e);

        } catch (RestClientException e) {
            throw convertToConnectionException(e);
        }
    }

    public UserAuthResponse authenticateOauth(OauthUserProfile profile) {
        UserOauthRequest userRequest = UserOauthRequest.from(profile);

        try {
            return restClient.post()
                    .uri("/internal/users/oauth")
                    .body(userRequest)
                    .retrieve()
                    .body(UserAuthResponse.class);

        } catch (RestClientResponseException e) {
            throw convertToDownstreamException(e);

        } catch (RestClientException e) {
            throw convertToConnectionException(e);
        }
    }

    private DownstreamServiceException convertToDownstreamException(RestClientResponseException e) {
        String responseBody = e.getResponseBodyAsString();
        String message = extractMessage(responseBody);
        HttpStatusCode statusCode = e.getStatusCode();

        if (statusCode.is4xxClientError()) {
            log.warn(
                    "{} service client error. status={}, body={}",
                    SERVICE_NAME,
                    statusCode,
                    responseBody
            );
        } else {
            log.error(
                    "{} service server error. status={}, body={}",
                    SERVICE_NAME,
                    statusCode,
                    responseBody
            );
        }

        return new DownstreamServiceException(
                SERVICE_NAME,
                statusCode,
                message,
                responseBody
        );
    }

    private DownstreamServiceException convertToConnectionException(RestClientException e) {
        log.error("{} service connection failed", SERVICE_NAME, e);

        return new DownstreamServiceException(
                SERVICE_NAME,
                HttpStatus.SERVICE_UNAVAILABLE,
                "USER 서비스에 연결할 수 없습니다.",
                null
        );
    }

    private String extractMessage(String responseBody) {
        try {
            JsonNode jsonNode = objectMapper.readTree(responseBody);

            if (jsonNode.has("message")) {
                return jsonNode.get("message").asText();
            }

            return "USER 서비스 호출 중 오류가 발생했습니다.";
        } catch (Exception e) {
            return "USER 서비스 호출 중 오류가 발생했습니다.";
        }
    }
}
