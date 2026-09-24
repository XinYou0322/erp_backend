package com.example.demo.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class RoleAuthorityMapperTest {

    @Test
    void shouldMapRoleLevelToDynamicAuthority() {
        // 1. 獲取動態等級權限
        Collection<? extends GrantedAuthority> authorities = RoleAuthorityMapper.fromRoleLevel(2);

        // 2. 確保使用 .stream() 轉換後再呼叫 .anyMatch()
        boolean hasAuthority = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_LEVEL_2"));

        assertTrue(hasAuthority);
    }
}
