package com.example.demo.purchaseOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequiredArgsConstructor
public class PurchaseOrdersController {
    private final PurchaseOrdersService purchaseOrdersService;
   

    //---新增---
    // 單筆
  @PostMapping("/api/purchaseOrder/add")
  public ResponseEntity<PurchaseOrderResponseDTO> addPurchaseOrder(@RequestBody PurchaseOrderCreateDTO dto) {
    PurchaseOrderResponseDTO response = purchaseOrdersService.insertPurchaseOrder(dto);
    
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}

}
