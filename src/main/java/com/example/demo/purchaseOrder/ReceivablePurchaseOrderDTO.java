package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.purchaseOrderItem.PurchaseOrderItems;

import lombok.Data;

@Data
public class ReceivablePurchaseOrderDTO {
    private Long id;
    private String orderNumber;
    private Long supplierId;
    private String supplierName;
    private PurchaseOrdersStatus status;
    private LocalDate expectedDeliveryDate;
    private List<Item> items;

    @Data
    public static class Item {
        private Long purchaseOrderItemId;
        private Long materialId;
        private String materialCode;
        private String materialName;
        private BigDecimal quantity;
        private String costMode;
        private String purchaseUnit;
        private String stockUnit;
        private BigDecimal conversionQuantity;
    }

    public static ReceivablePurchaseOrderDTO fromEntity(PurchaseOrders purchaseOrder) {
        ReceivablePurchaseOrderDTO dto = new ReceivablePurchaseOrderDTO();
        dto.setId(purchaseOrder.getId());
        dto.setOrderNumber(purchaseOrder.getOrderNumber());
        dto.setSupplierId(purchaseOrder.getSupplier().getId());
        dto.setSupplierName(purchaseOrder.getSupplier().getName());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setExpectedDeliveryDate(purchaseOrder.getExpectedDeliveryDate());
        dto.setItems(purchaseOrder.getItems().stream()
                .map(ReceivablePurchaseOrderDTO::toItem)
                .toList());
        return dto;
    }

    private static Item toItem(PurchaseOrderItems source) {
        Item item = new Item();
        item.setPurchaseOrderItemId(source.getId());
        item.setMaterialId(source.getMaterial().getId());
        item.setMaterialCode(source.getMaterial().getCode());
        item.setMaterialName(source.getMaterial().getName());
        item.setQuantity(source.getQuantity());
        item.setCostMode(source.getMaterial().getCostMode());
        item.setPurchaseUnit(source.getMaterial().getPurchaseUnit());
        item.setStockUnit(source.getMaterial().getUnit());
        item.setConversionQuantity(source.getMaterial().getConversionQuantity());
        return item;
    }
}
