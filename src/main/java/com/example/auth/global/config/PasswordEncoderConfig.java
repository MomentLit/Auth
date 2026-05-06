package com.example.auth.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 비밀번호는 단방향 해시로 저장해야 하므로 BCrypt 인코더를 공용 Bean으로 등록합니다.
        return new BCryptPasswordEncoder();
    }
}
