package com.example.demo.salesOrder;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SalesOrderItemCreDTO {
	
	@NotNull(message = "商品 ID 不可為空")
    private Long productId;

    @NotNull(message = "商品數量不可為空")
    @Min(value = 1, message = "商品數量至少為 1")
    private Integer quantity;
	
    
}
