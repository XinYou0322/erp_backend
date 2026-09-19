package com.example.demo.salesOrder;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SalesOrderCreDTO {
	
	@NotNull(message = "付款方式不可為空")
    private PaymentMethod paymentMethod;

    @Size(max = 1000, message = "訂單備註不可超過 1000 個字")
    private String note;

    @NotEmpty(message = "銷售訂單至少要有一筆商品")
    private List<@Valid SalesOrderItemCreDTO> items;
}
