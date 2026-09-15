package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class PurchaseOrderResponseDTO {
    private Long id;

    private Long supplierId;

    private String supplierName;

    private PurchaseOrdersStatus status;

    private Long createdByUserId;
    private String createdByName;

    private Long approvedByUserId;
    private String approvedByName;

    private BigDecimal total;

    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;

    private LocalDate expectedDeliveryDate;
    
    private LocalDateTime receivedAt;

    private String receiptUrl;
    
    private String decisionRemark;
    
    public static PurchaseOrderResponseDTO toResponseDTO(PurchaseOrders purchaseOrder) {
        PurchaseOrderResponseDTO responseDTO = new PurchaseOrderResponseDTO();
        responseDTO.setId(purchaseOrder.getId());
        responseDTO.setSupplierId(purchaseOrder.getSupplier().getId());
        responseDTO.setSupplierName(purchaseOrder.getSupplier().getName());
        //responseDTO.setStatus(purchaseOrder.getStatus());
        //responseDTO.setCreatedBy(purchaseOrder.getCreatedBy());
        //responseDTO.setApprovedBy(purchaseOrder.getApprovedBy());
        responseDTO.setTotal(purchaseOrder.getTotal());
        responseDTO.setCreatedAt(purchaseOrder.getCreatedAt());
        responseDTO.setExpectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate());
        return responseDTO;
    }
}
