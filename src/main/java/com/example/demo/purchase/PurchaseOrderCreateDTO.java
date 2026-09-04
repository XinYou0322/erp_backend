package com.example.demo.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;

@Data 
public class PurchaseOrderCreateDTO {
     private Long supplierId;

    private String createdBy;

    private LocalDate expectedDeliveryDate;

    private BigDecimal total;
}
