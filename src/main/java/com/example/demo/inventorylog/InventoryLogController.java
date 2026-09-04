package com.example.demo.inventorylog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InventoryLogController {

    public final InventoryLogService inventoryDeductionService;

    @PostMapping("/api/inventory/deduct") // 提供給 POS/訂單模組呼叫，依商品+數量扣庫存
    public ResponseEntity<?> deduct(@RequestBody InventoryLogRequest request) {

        try {

            inventoryDeductionService.deduct(request.getProductId(), request.getQuantity(), request.getRefId());

            return new ResponseEntity<>("扣庫存成功", HttpStatus.OK);

        } catch (IllegalStateException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}