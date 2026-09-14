package com.example.demo.suppliers;

import com.fasterxml.jackson.annotation.JsonSetter;

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

    // 判斷 前端到底有沒有傳 extension 這個欄位
    private boolean extensionProvided = false;
    
    @JsonSetter("extension")
    public void setExtension(String extension) {

        // 只要 JSON 裡出現 extension，就設為 true
        this.extensionProvided = true;

        // null 或空字串都代表清除分機
        if (extension == null || extension.isBlank()) {
            this.extension = null;
        } else {
            this.extension = extension.trim();
        }
    }

}