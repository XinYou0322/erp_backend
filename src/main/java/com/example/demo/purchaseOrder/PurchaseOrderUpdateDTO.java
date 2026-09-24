package com.example.demo.purchaseOrder;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.purchaseOrderItem.PurchaseOrderItemUpdateDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PurchaseOrderUpdateDTO {

	@NotNull(message = "供應商不可為空")
    @Positive(message = "供應商 ID 必須大於 0")
    private Long supplierId;
	
    @NotNull(message = "到貨日期不可為空")
    @FutureOrPresent(message = "預計到貨日不可早於今天")
	private LocalDate expectedDeliveryDate;
	
	 @Size(max = 1000, message = "備註最多 1000 個字")
	 private String decisionRemark;

	 @Valid
	 @NotEmpty(message = "採購明細不可為空")
	 private List<PurchaseOrderItemUpdateDTO> items;

	 
}
