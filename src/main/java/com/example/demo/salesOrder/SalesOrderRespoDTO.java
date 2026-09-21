package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class SalesOrderRespoDTO {
	 	
		private Long id;

	    private String orderNumber;

	    private SalesOrderStatus status;

	    private PaymentMethod paymentMethod;

	    private BigDecimal totalAmount;

	    private Long createdById;

	    private String createdByName;

	    private LocalDateTime createdAt;

	    private String note;

	    
	    public static SalesOrderRespoDTO fromEntity(SalesOrders order) {

	        SalesOrderRespoDTO dto = new SalesOrderRespoDTO();

	        dto.setId(order.getId());
	        dto.setOrderNumber(order.getOrderNumber());
	        dto.setStatus(order.getStatus());
	        dto.setPaymentMethod(order.getPaymentMethod());
	        dto.setTotalAmount(order.getTotalAmount());

	        dto.setCreatedById(order.getCreatedBy().getId());
	        dto.setCreatedByName(order.getCreatedBy().getName());

	        dto.setCreatedAt(order.getCreatedAt());
	        dto.setNote(order.getNote());

	        return dto;
	    }
}
