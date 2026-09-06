package com.example.demo.users;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequestDTO {

    @NotBlank(message = "角色名稱不能為空")
    @Size(max = 50, message = "角色名稱長度不能超過 50 個字元")
    private String roleName;

    @Size(max = 255, message = "角色描述長度不能超過 255 個字元")
    private String description;

}
