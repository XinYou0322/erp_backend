package com.example.demo.suppliersNotes;

import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class SuppliersNotesRespoDTO {
	
    private Long id;
	
	private String remark;
    
    private String createdBy;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;


    public static SuppliersNotesRespoDTO fromEntity(SupplierNotes supplierNote) {
        SuppliersNotesRespoDTO dto = new SuppliersNotesRespoDTO();
        dto.setId(supplierNote.getId());
        dto.setRemark(supplierNote.getRemark());
        dto.setCreatedBy(supplierNote.getCreatedBy().getName());
        dto.setCreatedAt(supplierNote.getCreatedAt());
        dto.setUpdatedAt(supplierNote.getUpdatedAt());
        return dto;
    }
}
