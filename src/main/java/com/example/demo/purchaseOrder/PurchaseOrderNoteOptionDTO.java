package com.example.demo.purchaseOrder;

import java.time.LocalDate;

import lombok.Data;

/** 【新增】供應商備註表單使用的精簡採購單選項。 */
@Data
public class PurchaseOrderNoteOptionDTO {
    private Long id;
    private String orderNumber;
    private PurchaseOrdersStatus status;
    private LocalDate expectedDeliveryDate;

    public static PurchaseOrderNoteOptionDTO fromEntity(PurchaseOrders entity) {
        PurchaseOrderNoteOptionDTO dto = new PurchaseOrderNoteOptionDTO();
        dto.setId(entity.getId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setStatus(entity.getStatus());
        dto.setExpectedDeliveryDate(entity.getExpectedDeliveryDate());
        return dto;
    }
}
