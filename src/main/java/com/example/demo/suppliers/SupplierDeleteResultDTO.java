package com.example.demo.suppliers;

import java.util.List;

import lombok.Data;

@Data
public class SupplierDeleteResultDTO {

    // 成功刪除
    private List<String> deletedSuppliers;

    // 因為已有採購紀錄，不能刪除
    private List<String> purchaseOrderSuppliers;

    // 根本找不到的 ID
    private List<Long> notFoundIds;

    // 最後顯示訊息
    private String message;
}
