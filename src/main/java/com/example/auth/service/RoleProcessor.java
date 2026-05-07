package com.example.auth.service;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RoleProcessor {

    public String normalizeRole(String role) {
        if (!StringUtils.hasText(role)) {
            return "ROLE_USER";
        }

        return role.startsWith("ROLE_")
                ? role
                : "ROLE_" + role;
    }

    public List<String> toAuthorities(String role) {
        return List.of(normalizeRole(role));
    }
}
