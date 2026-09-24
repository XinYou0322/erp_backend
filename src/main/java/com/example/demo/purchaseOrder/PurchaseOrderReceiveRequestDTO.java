package com.example.demo.purchaseOrder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PurchaseOrderReceiveRequestDTO {

    @Valid
    private List<Item> items = new ArrayList<>();

    @Data
    public static class Item {
        @NotNull(message = "採購明細 ID 不得為空")
        private Long purchaseOrderItemId;

        private LocalDate expiryDate;
    }
}
