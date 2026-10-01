package com.example.demo.suppliersNotes;

import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class SuppliersNotesRespoDTO {
	
    private Long id;
	
	private String remark;

    // 【新增】讓供應商備註列表清楚顯示所對應的採購單。
    private Long purchaseOrderId;
    private String purchaseOrderNumber;
    private String purchaseOrderStatus;
    
    private String createdBy;

    // 前端以穩定的帳號 ID 判斷是否為備註建立人，不使用可能重複的姓名。
    private Long createdByUserId;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;


    public static SuppliersNotesRespoDTO fromEntity(SupplierNotes supplierNote) {
        SuppliersNotesRespoDTO dto = new SuppliersNotesRespoDTO();
        dto.setId(supplierNote.getId());
        dto.setRemark(supplierNote.getRemark());
        if (supplierNote.getPurchaseOrder() != null) {
            dto.setPurchaseOrderId(supplierNote.getPurchaseOrder().getId());
            dto.setPurchaseOrderNumber(supplierNote.getPurchaseOrder().getOrderNumber());
            dto.setPurchaseOrderStatus(supplierNote.getPurchaseOrder().getStatus().name());
        }
        dto.setCreatedBy(supplierNote.getCreatedBy().getName());
        dto.setCreatedByUserId(supplierNote.getCreatedBy().getId());
        dto.setCreatedAt(supplierNote.getCreatedAt());
        dto.setUpdatedAt(supplierNote.getUpdatedAt());
        return dto;
    }
}
