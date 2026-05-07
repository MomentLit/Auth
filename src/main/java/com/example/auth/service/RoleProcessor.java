package com.example.auth.service;

import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class RoleProcessor {

    public List<String> normalizeRoles(
            List<String> roles,
            String role
    ) {

        if (roles == null || roles.isEmpty()) {

            if (StringUtils.hasText(role)) {
                return List.of(normalizeRole(role));
            }

            return List.of("ROLE_USER");
        }

        List<String> normalizedRoles =
                roles.stream()
                        .filter(StringUtils::hasText)
                        .map(this::normalizeRole)
                        .distinct()
                        .toList();

        if (normalizedRoles.isEmpty()) {
            return List.of("ROLE_USER");
        }

        return normalizedRoles;
    }

    public String firstRole(List<String> roles) {

        return roles.isEmpty()
                ? "ROLE_USER"
                : roles.getFirst();
    }

    private String normalizeRole(String role) {

        return role.startsWith("ROLE_")
                ? role
                : "ROLE_" + role;
    }
}