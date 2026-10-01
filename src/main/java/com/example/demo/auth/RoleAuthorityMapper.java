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
            // Codex 修改：六個職務保留獨立識別，沿用員工基本存取，不授予管理員權限。
            case 5 -> employeeAuthorities("PROCUREMENT");
            case 6 -> employeeAuthorities("WAREHOUSE");
            case 7 -> employeeAuthorities("RESEARCH");
            case 8 -> employeeAuthorities("FINANCE");
            case 9 -> employeeAuthorities("HR");
            case 10 -> employeeAuthorities("SUPERVISOR");
            default -> List.of(); // 未知等級不給予任何權限
        };
    }

    private RoleAuthorityMapper() {
    }

    // Codex 修改：維持既有員工 API 的相容性並保留職務角色。
    private static List<GrantedAuthority> employeeAuthorities(String role) {
        return List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"),
                new SimpleGrantedAuthority("ROLE_" + role));
    }

}
