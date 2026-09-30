package com.example.demo.auth;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

// Codex 修改：新職務具備員工基本權限，但不得意外取得管理員或經理權限。
class ExtendedRoleAuthorityMapperTest {
    @Test
    void mapsSixNewRolesWithoutAdministrativeAccess() {
        String[] roles = {"PROCUREMENT", "WAREHOUSE", "RESEARCH", "FINANCE", "HR", "SUPERVISOR"};
        for (int i = 0; i < roles.length; i++) {
            var actual = RoleAuthorityMapper.fromRoleLevel(i + 5).stream()
                    .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
            assertEquals(Set.of("ROLE_EMPLOYEE", "ROLE_" + roles[i]), actual);
        }
        assertTrue(RoleAuthorityMapper.fromRoleLevel(11).isEmpty());
    }
}
