package com.example.demo.purchaseOrder;

import java.util.List;

import lombok.Data;

@Data
public class PurchaseOrderQueryResponseDTO {
	
	 // 有查到的採購單
    private List<PurchaseOrderResponseDTO> purchaseOrders;

    // 找不到的採購單 ID
    private List<Long> notFoundIds;

    // 提示訊息
    private String message;
}
