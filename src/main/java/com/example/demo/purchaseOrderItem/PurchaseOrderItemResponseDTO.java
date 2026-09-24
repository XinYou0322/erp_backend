package com.example.demo.purchaseOrderItem;

import java.math.BigDecimal;

import lombok.Data;


@Data
public class PurchaseOrderItemResponseDTO {

    private Long id;
    private Long materialId;
    private String materialCode;
    private String materialName;
    private String unit;
    private BigDecimal quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public static PurchaseOrderItemResponseDTO fromEntity(PurchaseOrderItems item) {
        PurchaseOrderItemResponseDTO dto = new PurchaseOrderItemResponseDTO();
        dto.setId(item.getId());
        dto.setMaterialId(item.getMaterial().getId());
        dto.setMaterialCode(item.getMaterial().getCode());
        dto.setMaterialName(item.getMaterial().getName());
        dto.setUnit(item.getMaterial().getUnit());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        return dto;
    }
}
