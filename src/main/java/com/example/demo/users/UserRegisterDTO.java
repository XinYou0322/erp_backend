package com.example.demo.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterDTO {

    @NotBlank(message = "帳號不能為空")
    @Size(min = 3, max = 50, message = "帳號長度需介於 3 到 50 個字元")
    private String username;

    @NotBlank(message = "密碼不能為空")
    @Size(min = 6, max = 100, message = "密碼長度至少需 6 個字元")
    private String password;

    @NotBlank(message = "姓名不能為空")
    @Size(max = 50, message = "姓名長度不能超過 50 個字元")
    private String name;

    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 50, message = "Email 長度不能超過 50 個字元")
    private String email;

    @NotNull(message = "角色 ID 不能為空")
    private Long roleId;

    // @NotNull(message = "部門 ID 不能為空")
    // private Long departmentId;

}
