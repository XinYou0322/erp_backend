package com.example.demo.salesOrder;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesOrderItemRespoDTO {
	
	private Long id;
	
	private Long productId;
	
	private String productSku;

    private String productName;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal subtotal;
}
