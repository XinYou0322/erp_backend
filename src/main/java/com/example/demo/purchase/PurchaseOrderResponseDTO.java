package com.example.demo.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class PurchaseOrderResponseDTO {
    private Long id;

    private Long supplierId;

    private String supplierName;

    private String status;

    private String createdBy;

    private String approvedBy;

    private BigDecimal total;

    private LocalDateTime createdAt;

    private LocalDate expectedDeliveryDate;

    public static PurchaseOrderResponseDTO toResponseDTO(PurchaseOrders purchaseOrder) {
        PurchaseOrderResponseDTO responseDTO = new PurchaseOrderResponseDTO();
        responseDTO.setId(purchaseOrder.getId());
        responseDTO.setSupplierId(purchaseOrder.getSupplier().getId());
        responseDTO.setSupplierName(purchaseOrder.getSupplier().getName());
        responseDTO.setStatus(purchaseOrder.getStatus());
        responseDTO.setCreatedBy(purchaseOrder.getCreatedBy());
        responseDTO.setApprovedBy(purchaseOrder.getApprovedBy());
        responseDTO.setTotal(purchaseOrder.getTotal());
        responseDTO.setCreatedAt(purchaseOrder.getCreatedAt());
        responseDTO.setExpectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate());
        return responseDTO;
    }
}
