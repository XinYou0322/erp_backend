package com.example.demo.users;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.demo.Role.RoleRepository;

// Codex 修改：驗證編輯資料保存、回傳及未指定狀態時不會啟用停用帳號。
class UsersServiceUpdateTest {
    @Test
    void persistsEditedFieldsAndPreservesStatus() {
        var repository = mock(UsersRepository.class);
        var encoder = mock(PasswordEncoder.class);
        var mapper = Mappers.getMapper(UserMapper.class);
        var service = new UsersService(encoder, repository, mock(RoleRepository.class), mapper);
        var user = new User();
        user.setEmail("old@example.com");
        user.setStatus(UserStatus.INACTIVE);
        when(repository.findById(1L)).thenReturn(Optional.of(user));
        when(repository.save(user)).thenReturn(user);
        when(encoder.encode("new-password")).thenReturn("encoded-password");
        var dto = new UserUpdateDTO();
        dto.setName("測試使用者");
        dto.setEmail("new@example.com");
        dto.setRoleLevel(3);
        dto.setPhone("0912345678");
        dto.setDepartment("總管理處");
        dto.setSalary(new BigDecimal("36000"));
        dto.setAvatar("/uploads/avatars/test.png");
        dto.setPassword("new-password");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("admin", null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
        try {
            var result = service.updateUser(1L, dto);
            verify(repository).save(user);
            assertEquals("0912345678", result.getPhone());
            assertEquals("總管理處", result.getDepartment().getName());
            assertEquals(dto.getSalary(), result.getSalary());
            assertEquals(dto.getAvatar(), result.getAvatar());
            assertEquals("INACTIVE", result.getStatus());
            assertEquals("encoded-password", user.getPassword());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
