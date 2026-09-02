package com.example.demo.inventories;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class InventoryController {

    public final InventoryService inventoryService;

    @PostMapping(" ") // 新增一批（進貨）
    public ResponseEntity<?> create(@RequestBody Inventory inventory) {

        Inventory saved = inventoryService.create(inventory);

        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/api/inventory/material/{materialId}") // 查詢某原物料所有批次
    public ResponseEntity<?> findByMaterialId(@PathVariable Long materialId) {

        List<Inventory> list = inventoryService.findByMaterialId(materialId);

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("/api/inventory/material/{materialId}/total") // 查詢某原物料總庫存
    public ResponseEntity<?> getTotalQuantity(@PathVariable Long materialId) {

        BigDecimal total = inventoryService.getTotalQuantity(materialId);

        return new ResponseEntity<>(total, HttpStatus.OK);
    }

    @PutMapping("/api/inventoryupdate/{id}") // 修改某一批
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody Inventory inventory) {

        Inventory updated = inventoryService.update(id, inventory);

        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/api/inventorydelete/{id}") // 刪除一批
    public ResponseEntity<?> delete(@PathVariable Long id) {

        inventoryService.delete(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}