package com.example.demo.purchaseOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PurchaseOrderReceiveRequestDTO {

    @Valid
    private List<Item> items = new ArrayList<>();

    // 【新增：收貨備註】收貨時可直接建立並綁定這張採購單的供應商備註。
    @Size(max = 200, message = "供應商備註不可超過 200 字")
    private String supplierRemark;

    @Data
    public static class Item {
        @NotNull(message = "採購明細 ID 不得為空")
        private Long purchaseOrderItemId;

        private LocalDate expiryDate;
    }
}
