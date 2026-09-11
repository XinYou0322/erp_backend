package com.example.demo.suppliers;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SuppliersUpdateDTO {

    @Size(max = 50, message = "供應商名稱不可以超過 50 個字")
    private String name;

    @Pattern(
        regexp = "^\\+[1-9][0-9]{0,2}$",
        message = "電話國碼格式錯誤，例如：+886、+81、+1"
    )
    private String callingCode;

    @Pattern(
        regexp = "^[0-9]{6,15}$",
        message = "電話只能包含數字，長度需為 6～15 碼"
    )
    private String phone;

    @Pattern(
        regexp = "^[0-9]{1,10}$",
        message = "分機只能包含數字，最多 10 碼"
    )
    private String extension;

    @Size(max = 200, message = "地址不可以超過 200 個字")
    private String address;

    @Email(message = "Email 格式錯誤")
    private String email;

    private SupplierStatus status;
}