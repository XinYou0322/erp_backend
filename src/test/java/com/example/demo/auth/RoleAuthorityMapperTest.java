package com.example.demo.auth;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

class RoleAuthorityMapperTest {

    @Test
    void shouldMapManagerRoleToRoleManagerAuthority() {
        List<GrantedAuthority> authorities = RoleAuthorityMapper.fromRoleName("經理");
        assertTrue(authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER")));
    }
}
