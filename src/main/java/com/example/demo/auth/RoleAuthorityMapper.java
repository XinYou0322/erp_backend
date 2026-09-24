package com.example.demo.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class RoleAuthorityMapper {

    // ✨ 新增此方法：根據數字等級給予對應的 Spring Security 權限
    public static Collection<? extends GrantedAuthority> fromRoleLevel(Integer roleLevel) {
        if (roleLevel == null) {
            return List.of();
        }

        // 依據您當初 SQL 的設定進行對應：
        // 1 -> 店長 (ADMIN), 2 -> 經理 (MANAGER), 3 -> 正職 (EMPLOYEE), 4 -> 訪客 (GUEST)
        return switch (roleLevel) {
            case 1 -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
            case 2 -> List.of(new SimpleGrantedAuthority("ROLE_MANAGER"));
            case 3 -> List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
            case 4 -> List.of(new SimpleGrantedAuthority("ROLE_GUEST"));
            default -> List.of(); // 未知等級不給予任何權限
        };
    }

    private RoleAuthorityMapper() {
    }

}
