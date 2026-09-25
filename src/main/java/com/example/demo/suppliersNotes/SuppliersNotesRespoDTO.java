package com.example.demo.suppliersNotes;

import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class SuppliersNotesRespoDTO {
	
    private Long id;
	
	private String remark;
    
    private String createdBy;

    // 前端以穩定的帳號 ID 判斷是否為備註建立人，不使用可能重複的姓名。
    private Long createdByUserId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;


    public static SuppliersNotesRespoDTO fromEntity(SupplierNotes supplierNote) {
        SuppliersNotesRespoDTO dto = new SuppliersNotesRespoDTO();
        dto.setId(supplierNote.getId());
        dto.setRemark(supplierNote.getRemark());
        dto.setCreatedBy(supplierNote.getCreatedBy().getName());
        dto.setCreatedByUserId(supplierNote.getCreatedBy().getId());
        dto.setCreatedAt(supplierNote.getCreatedAt());
        dto.setUpdatedAt(supplierNote.getUpdatedAt());
        return dto;
    }
}
