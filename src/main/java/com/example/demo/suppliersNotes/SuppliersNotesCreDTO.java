package com.example.demo.suppliersNotes;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.Positive;

@Data 
public class SuppliersNotesCreDTO {

    @NotBlank(message = "備註不可為空")
    @Size(max = 200, message = "備註不可超過200字")
    private String remark;

    // 【新增：關聯採購單】null 代表一般供應商備註，不指定採購單。
    @Positive(message = "採購單 ID 必須大於 0")
    private Long purchaseOrderId;


    
}
