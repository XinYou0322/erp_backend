package com.example.demo.purchaseOrderItem;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class PurchaseOrderItemUpdateDTO {
	
	 @Positive(message = "採購明細 ID 必須大於 0")
	 private Long id;
	
	 @NotNull(message = "原物料不可為空")
	 @Positive(message = "原物料 ID 必須大於 0")
	 private Long materialId;
	 
	 @NotNull(message = "採購數量不可為空")
	 @Positive(message = "採購數量必須大於 0")
	 private BigDecimal quantity;
	 
	 @NotNull(message = "採購單價不可為空")
	 @Positive(message = "採購單價必須大於 0")
	 private BigDecimal price;
}
