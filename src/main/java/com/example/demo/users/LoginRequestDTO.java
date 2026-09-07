package com.example.demo.users;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "帳號不能為空")
    private String username;

    @NotBlank(message = "密碼不能為空")
    private String password;

}
