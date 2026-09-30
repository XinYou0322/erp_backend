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

    @NotNull(message = "角色等級不能為空")
    private Integer roleLevel;

    private String avatar;

    // Codex 修改：接收編輯表單的電話、部門、薪資與選填的新密碼。
    @Size(max = 50)
    private String phone;
    @Size(max = 100)
    private String department;
    // Codex 修改：後端同步阻擋負數、超過兩位小數及超出資料庫範圍的薪資。
    @jakarta.validation.constraints.DecimalMin(value = "0", message = "薪資不能為負數")
    @jakarta.validation.constraints.DecimalMax(value = "99999999.99", message = "薪資不可超過 99,999,999.99 元")
    @jakarta.validation.constraints.Digits(integer = 8, fraction = 2, message = "薪資最多八位整數及兩位小數")
    private java.math.BigDecimal salary;
    private String password;

    // @NotNull(message = "部門 ID 不能為空")
    // private Long departmentId;

    // Codex 修改：編輯個人資料未傳狀態時，保留原帳號狀態。
    private UserStatus status;

}
