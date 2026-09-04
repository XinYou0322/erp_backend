package com.example.demo.purchase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequiredArgsConstructor
public class PurchaseOrdersController {
    private final PurchaseOrdersService purchaseOrdersService;
   

    //---新增---
    // 單筆
    @PostMapping("/api/purchaseOrder/add")
    public  PurchaseOrderResponseDTO addPurchaseOrder(@RequestBody PurchaseOrderCreateDTO purchaseOrderCreateDTO) {
          PurchaseOrders purchaseOrder = purchaseOrdersService.insertPurchaseOrder(
            purchaseOrderCreateDTO.getSupplierId(),
            purchaseOrderCreateDTO.getCreatedBy(),
            purchaseOrderCreateDTO.getExpectedDeliveryDate(),
            purchaseOrderCreateDTO.getTotal()
          );
       PurchaseOrderResponseDTO responseDTO = PurchaseOrderResponseDTO.toResponseDTO(purchaseOrder);
          return responseDTO;
    }
    

}
