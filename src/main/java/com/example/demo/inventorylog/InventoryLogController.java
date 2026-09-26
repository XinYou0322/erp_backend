package com.example.demo.inventorylog;

import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.inventorylog.DTO.InventoryAdjustmentRequestDTO;
import com.example.demo.inventorylog.DTO.InventoryLogResponseDTO;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InventoryLogController {


    private final InventoryLogService inventoryLogService;

    // 【本次修改：銷售與庫存同步】
    // 保留原本的手動扣庫存 API，並將 refId 一併傳入 Service，讓扣除紀錄可關聯來源單據。
    @PostMapping("/api/inventory/deduct")
    public ResponseEntity<?> deduct(@RequestBody InventoryLogRequest request) {

        try {

        	inventoryLogService.deduct(request.getProductId(), request.getQuantity(), request.getRefId());

            return new ResponseEntity<>("扣庫存成功", HttpStatus.OK);

        // 【本次修改：銷售與庫存同步】
        // BOM 不完整、數量不合法或庫存不足時，回傳 400 與可直接顯示的錯誤訊息。
        } catch (IllegalStateException | IllegalArgumentException e) {

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
    @GetMapping("/api/inventory-logs/date-range")
    public ResponseEntity<List<InventoryLogResponseDTO>>
            getLogsByDateRange(
                @RequestParam LocalDate startDate,
                @RequestParam LocalDate endDate) {

        List<InventoryLogResponseDTO> logs =
            inventoryLogService.getLogsByDateRange(
                startDate,
                endDate
            );

        return ResponseEntity.ok(logs);
    }
    
    
    
}
