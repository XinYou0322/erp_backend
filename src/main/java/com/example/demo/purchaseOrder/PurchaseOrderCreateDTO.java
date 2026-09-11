package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class PurchaseOrderCreateDTO {
   
   @NotBlank(message = "供應商不可為空")
   private Long supplierId;
   
   private String status;

   @NotBlank(message = "建立人不可為空")
   private String createdBy;
   
   @NotBlank(message = "審核人不可為空")
   private String approvedBy;

   @NotNull (message = "到貨日期不可為空")
   private LocalDate expectedDeliveryDate;

   @NotNull (message = "總金額不可為空")
   private BigDecimal total;

   
}
