package com.example.demo.suppliersNotes;

import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class SuppliersNotesRespoDTO {
	
    private Long id;
	
	private String remark;
    
    private String createdBy;
    
    private LocalDateTime createdAt;

    public static SuppliersNotesRespoDTO fromEntity(SupplierNotes SupplierNote) {
    	
    	SuppliersNotesRespoDTO dto = new SuppliersNotesRespoDTO();
    	dto.setId(SupplierNote.getId());
    	dto.setRemark(SupplierNote.getRemark());
    	dto.setCreatedBy(SupplierNote.getCreatedBy().getName());
    	dto.setCreatedAt(SupplierNote.getCreatedAt());
  
    	return dto;
    			
    }
}
