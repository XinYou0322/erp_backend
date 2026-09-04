package com.example.demo.users;

import java.time.Instant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDTO {

    private Long id;
    private String username;
    private String name;
    private String email;
    private String status;
    private Instant creatrdAt;

    // 透過DTO只曝露前端需要的關聯資訊，避免直接曝露整個Role/Department
    private RoleInfo role;
    private DepartmentInfo department;

    @Getter
    @Setter
    public static class RoleInfo {
        private Long id;
        private String name;
        private String code;
    }

    @Getter
    @Setter
    public static class DepartmentInfo {
        private Long id;
        private String name;

    }
}
