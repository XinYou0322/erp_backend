package com.example.demo.purchase;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data 
public class PurchaseOrderDetailDTO {
    
    private Long id;
    
    private Long supplierId;
    
    private String supplierName;
    
    private String status;
    
    private String createdBy;
    
    private String approvedBy;
    
    private BigDecimal total;
    
    private LocalDateTime createdAt;
    
    private LocalDate expectedDeliveryDate;
}