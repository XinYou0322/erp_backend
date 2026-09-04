package com.example.demo.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Data;

@Data 
public class  PurchaseOrderListDTO {
    private Long id;

    private String supplierName;

    private String status;

    private BigDecimal total;

    private LocalDateTime createdAt;

    private LocalDate expectedDeliveryDate;
}
