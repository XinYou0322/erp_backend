package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class SalesOrderListRespoDTO {

    private Long id;
    
    private String orderNumber;

    private SalesOrderStatus status;

    private PaymentMethod paymentMethod;

    private BigDecimal totalAmount;

    private Long createdById;

    private String createdByName;

    private LocalDateTime createdAt;


    public static SalesOrderListRespoDTO fromEntity(SalesOrders order) {

        SalesOrderListRespoDTO dto = new SalesOrderListRespoDTO();

        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedById(order.getCreatedBy().getId());
        dto.setCreatedByName(order.getCreatedBy().getName());
        dto.setCreatedAt(order.getCreatedAt());

        return dto;
    }
}