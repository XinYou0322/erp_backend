package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class PurchaseOrderResponseDTO {
    private Long id;

    private String orderNumber;
    
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
    
    private Long receivedByUserId;
    private String receivedByName;

    private String receiptUrl;
    
    private String decisionRemark;
    
    public static PurchaseOrderResponseDTO fromEntity(PurchaseOrders purchaseOrder) {
        PurchaseOrderResponseDTO dto = new PurchaseOrderResponseDTO();
        dto.setId(purchaseOrder.getId());
        dto.setOrderNumber(purchaseOrder.getOrderNumber());
        dto.setSupplierId(purchaseOrder.getSupplier().getId());
        dto.setSupplierName(purchaseOrder.getSupplier().getName());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setCreatedByUserId(purchaseOrder.getCreatedBy().getId());
        dto.setCreatedByName(purchaseOrder.getCreatedBy().getName());
        if (purchaseOrder.getApprovedBy() != null) {
            dto.setApprovedByUserId(purchaseOrder.getApprovedBy().getId());
            dto.setApprovedByName(purchaseOrder.getApprovedBy().getName());
        }
        dto.setTotal(purchaseOrder.getTotal());
        dto.setCreatedAt(purchaseOrder.getCreatedAt());
        dto.setUpdatedAt(purchaseOrder.getUpdatedAt());
        dto.setExpectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate());
        dto.setReceivedAt(purchaseOrder.getReceivedAt());
        dto.setReceiptUrl(purchaseOrder.getReceiptUrl());
        dto.setDecisionRemark(purchaseOrder.getDecisionRemark());
        
        //有收貨人才set
        if(purchaseOrder.getReceivedBy()!=null) {
        	dto.setReceivedByUserId( purchaseOrder.getReceivedBy().getId());
        	dto.setReceivedByName(purchaseOrder.getReceivedBy().getName() );
	  
        }
        return dto;
    }
}
