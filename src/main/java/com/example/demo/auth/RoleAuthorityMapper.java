package com.example.demo.auth;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class RoleAuthorityMapper {

    private RoleAuthorityMapper() {
    }

    public static List<GrantedAuthority> fromRoleName(String roleName) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        String normalized = normalize(roleName);
        if (normalized == null || normalized.isBlank()) {
            return authorities;
        }

        authorities.add(new SimpleGrantedAuthority("ROLE_" + normalized.toUpperCase()));
        return authorities;
    }

    private static String normalize(String roleName) {
        if (roleName == null) {
            return null;
        }
        String cleaned = roleName.trim();
        if (cleaned.isEmpty()) {
            return null;
        }

        if (cleaned.contains("店長") || cleaned.contains("管理員") || cleaned.contains("admin")) {
            return "ADMIN";
        }
        if (cleaned.contains("經理") || cleaned.contains("manager") || cleaned.contains("組長")) {
            return "MANAGER";
        }
        if (cleaned.contains("正職") || cleaned.contains("employee") || cleaned.contains("員工") || cleaned.contains("收銀")
                || cleaned.contains("班長") || cleaned.contains("PT")) {
            return "EMPLOYEE";
        }
        return cleaned.toUpperCase().replace("-", "_").replace(" ", "_");
    }
}
