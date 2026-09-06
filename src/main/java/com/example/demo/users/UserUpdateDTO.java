package com.example.demo.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDTO {

    @NotBlank(message = "姓名不能為空")
    @Size(max = 50, message = "姓名長度不能超過 50 個字元")
    private String name;

    @NotBlank(message = "Email 不能為空")
    @Email(message = "Email 格式不正確")
    @Size(max = 50, message = "Email 長度不能超過 50 個字元")
    private String email;

    @NotNull(message = "角色 ID 不能為空")
    private Long roleId;

    @NotNull(message = "部門 ID 不能為空")
    private Long departmentId;

    @NotNull(message = "使用者狀態不能為空")
    private UserStatus status;

}
