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
    private String avatar;
    // Codex 修改：清單及重新開啟編輯視窗需要回傳電話。
    private String phone;
    private String status;
    private Instant createdAt;

    // 薪資資訊
    private java.math.BigDecimal salary;

    // 透過DTO只曝露前端需要的關聯資訊，避免直接曝露整個Role/Department
    private Integer roleLevel;
    private DepartmentInfo department;

    @Getter
    @Setter
    public static class RoleInfo {
        private Long id;
        private String name;
        private String description;
    }

    @Getter
    @Setter
    public static class DepartmentInfo {
        private Long id;
        private String name;

    }
}
