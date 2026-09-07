package com.example.demo.suppliers;

import jakarta.validation.constraints.Email;
import lombok.Data;

@Data 
public class SuppliersUpdateDTO {
    private String name;

    private String phone;

    private String address;

    @Email(message = "Email 格式錯誤")
    private String email;
}