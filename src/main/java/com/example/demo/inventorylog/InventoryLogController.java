package com.example.demo.inventorylog;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InventoryLogController {


    private final InventoryLogService inventoryLogService;

    @PostMapping("/api/inventory/deduct") // 提供給 POS/訂單模組呼叫，依商品+數量扣庫存
    public ResponseEntity<?> deduct(@RequestBody InventoryLogRequest request) {

        try {

        	inventoryLogService.deduct(request.getProductId(), request.getQuantity(), request.getRefId());

            return new ResponseEntity<>("扣庫存成功", HttpStatus.OK);

        } catch (IllegalStateException e) {

            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    
    
    
    // 查全部庫存異動紀錄
    @GetMapping("/api/inventory-logs")
    public ResponseEntity<List<InventoryLogResponseDTO>> findAllLogs() {

        List<InventoryLogResponseDTO> list =
                inventoryLogService.findAllLogs();

        return ResponseEntity.ok(list);
    }


    // 查某個原物料的異動紀錄
    @GetMapping("/api/inventory-logs/material/{materialId}")
    public ResponseEntity<List<InventoryLogResponseDTO>> findByMaterialId(
            @PathVariable Long materialId) {

        List<InventoryLogResponseDTO> list =
                inventoryLogService.findByMaterialId(materialId);

        return ResponseEntity.ok(list);
    }
    
    @PostMapping("/api/inventory-logs/adjustments")
    public ResponseEntity<Void> adjustInventory(
            @RequestBody InventoryAdjustmentRequestDTO request) {

        inventoryLogService.adjustInventory(request);

        return ResponseEntity.noContent().build();
    }
    
    
    
    
}