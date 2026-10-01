package com.example.demo.users;

import java.math.BigDecimal;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
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

    @NotNull(message = "角色等級不能為空")
    private Integer roleLevel;

    @DecimalMin(value = "0.0", inclusive = true, message = "薪資不能為負數")
    // Codex 修改：新增帳號與編輯帳號採用相同薪資上限。
    @jakarta.validation.constraints.DecimalMax(value = "99999999.99", message = "薪資不可超過 99,999,999.99 元")
    @Digits(integer = 8, fraction = 2, message = "薪資格式不正確")
    private BigDecimal salary;

    private String avatar;

    // Codex 修改：申請及開立新帳號接收相同部門欄位。
    @Size(max = 100, message = "部門名稱不能超過 100 個字元")
    private String department;

    // @NotNull(message = "部門 ID 不能為空")
    // private Long departmentId;

}
