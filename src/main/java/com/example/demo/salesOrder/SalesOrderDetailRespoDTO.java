package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SalesOrderDetailRespoDTO {

    private Long id;

    private String orderNumber;

    private SalesOrderStatus status;

    private PaymentMethod paymentMethod;

    private BigDecimal totalAmount;

    private Long createdById;

    private String createdByName;

    private LocalDateTime createdAt;

    private String note;

    private Long voidedById;

    private String voidedByName;

    private LocalDateTime voidedAt;

    private String voidReason;
    // 【明細】
    private List<SalesOrderItemRespoDTO> items;


    public static SalesOrderDetailRespoDTO fromEntity(
            SalesOrders order) {

        SalesOrderDetailRespoDTO dto = new SalesOrderDetailRespoDTO();

        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedById(order.getCreatedBy().getId());
        dto.setCreatedByName(order.getCreatedBy().getName());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setNote(order.getNote());
        
        //正常銷售單沒有作廢人，因此先判斷 null，避免 NullPointerException
        if (order.getVoidedBy() != null) {
            dto.setVoidedById(order.getVoidedBy().getId());
            dto.setVoidedByName(order.getVoidedBy().getName());
        }
        //未作廢時這兩個欄位維持 null；作廢後則回傳時間與理由。
        dto.setVoidedAt(order.getVoidedAt());
        dto.setVoidReason(order.getVoidReason());

        // SalesOrderItem → SalesOrderItemRespoDTO

        List<SalesOrderItemRespoDTO> itemDTOList = new ArrayList<>();


        for (SalesOrderItem item : order.getItems()) {

            SalesOrderItemRespoDTO itemDTO = new SalesOrderItemRespoDTO();

            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductSku(item.getProductSku());
            itemDTO.setProductName(item.getProductName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setSubtotal(item.getSubtotal());
            itemDTOList.add(itemDTO);
        }

        dto.setItems(itemDTOList);

        return dto;
    }
}