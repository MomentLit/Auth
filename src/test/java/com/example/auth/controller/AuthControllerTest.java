package com.example.auth.controller;

import com.example.auth.dto.request.RefreshRequest;
import com.example.auth.dto.request.SignInRequest;
import com.example.auth.dto.request.SignOutRequest;
import com.example.auth.dto.response.OauthGoogleCallbackResponse;
import com.example.auth.dto.response.RefreshResponse;
import com.example.auth.dto.response.SignInResponse;
import com.example.auth.service.AuthService;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        mockMvc = standaloneSetup(new AuthController(authService)).build();
    }

    @Test
    void signin() throws Exception {
        when(authService.login(any(SignInRequest.class)))
                .thenReturn(new SignInResponse("Test User", "ROLE_USER", "access-token", "refresh-token", "1800"));

        mockMvc.perform(post("/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "test@example.com",
                                  "password": "password123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.data.access_token").value("access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("refresh-token"))
                .andExpect(jsonPath("$.data.expires_in").value("1800"));

        ArgumentCaptor<SignInRequest> captor = ArgumentCaptor.forClass(SignInRequest.class);
        verify(authService).login(captor.capture());
        assertThat(captor.getValue().email()).isEqualTo("test@example.com");
        assertThat(captor.getValue().password()).isEqualTo("password123");
    }

    @Test
    void googleOauth() throws Exception {
        URI authorizationUri = URI.create("https://accounts.google.com/o/oauth2/v2/auth?state=test-state");
        when(authService.createGoogleAuthorizationUri("test-state")).thenReturn(authorizationUri);

        mockMvc.perform(get("/auth/oauth/google")
                        .param("state", "test-state"))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", authorizationUri.toString()));

        verify(authService).createGoogleAuthorizationUri("test-state");
    }

    @Test
    void googleOauthCallback() throws Exception {
        when(authService.loginWithGoogle("auth-code", "test-state"))
                .thenReturn(new OauthGoogleCallbackResponse(
                        "Google User",
                        "ROLE_USER",
                        "google-access-token",
                        "google-refresh-token",
                        1800
                ));

        mockMvc.perform(get("/auth/oauth/google/callback")
                        .param("code", "auth-code")
                        .param("state", "test-state"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Google User"))
                .andExpect(jsonPath("$.data.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.data.access_token").value("google-access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("google-refresh-token"))
                .andExpect(jsonPath("$.data.expires_in").value(1800));

        verify(authService).loginWithGoogle("auth-code", "test-state");
    }

    @Test
    void refresh() throws Exception {
        when(authService.refresh(any(RefreshRequest.class)))
                .thenReturn(new RefreshResponse("new-access-token", "new-refresh-token", 1800));

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.access_token").value("new-access-token"))
                .andExpect(jsonPath("$.data.refresh_token").value("new-refresh-token"))
                .andExpect(jsonPath("$.data.expires_in").value(1800));

        ArgumentCaptor<RefreshRequest> captor = ArgumentCaptor.forClass(RefreshRequest.class);
        verify(authService).refresh(captor.capture());
        assertThat(captor.getValue().refreshToken()).isEqualTo("refresh-token");
    }

    @Test
    void signout() throws Exception {
        mockMvc.perform(delete("/auth/signout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refresh_token": "refresh-token"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());

        ArgumentCaptor<SignOutRequest> captor = ArgumentCaptor.forClass(SignOutRequest.class);
        verify(authService).logout(captor.capture());
        assertThat(captor.getValue().refresh_token()).isEqualTo("refresh-token");
    }
}
