package com.example.demo.inventorylog;

import java.time.LocalDate;
import java.util.List;

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
