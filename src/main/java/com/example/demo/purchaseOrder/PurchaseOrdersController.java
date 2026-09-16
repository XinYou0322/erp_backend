package com.example.demo.purchaseOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.suppliers.SupplierMultiQueryRespoDTO;
import com.example.demo.suppliers.SupplierRespoDTO;

import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequiredArgsConstructor
public class PurchaseOrdersController {
    private final PurchaseOrdersService purchaseOrdersService;
   

    //---新增---
    // 單筆
  @PostMapping("/api/purchaseOrder/add")
  public ResponseEntity<PurchaseOrderResponseDTO> addPurchaseOrder(@RequestBody PurchaseOrderCreateDTO dto, @RequestParam Long loginUserId) {
    
    
    return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrdersService.insertPurchaseOrder(dto, loginUserId));
}

  //---查詢---
  @GetMapping("/api/purchaseOrder/find/{id}")
  public ResponseEntity<PurchaseOrderResponseDTO> findSupplierById(  @PathVariable Long id) {
    
  return ResponseEntity.ok(purchaseOrdersService.findPurchaseOrderById(id)
		  
  );
  }
  //多筆
  @GetMapping("/api/purchaseOrder/findByIds")
  public ResponseEntity<List<PurchaseOrderResponseDTO>> findSuppliersByIds(
      @RequestParam("ids") List<Long> ids) {

  return ResponseEntity.ok(
		  purchaseOrdersService.findPurchaseOrdersByIds(ids)
  );
}
}
