package com.example.demo.salesOrder;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.suppliers.SupplierCreDTO;
import com.example.demo.suppliers.SupplierRespoDTO;
import com.example.demo.suppliers.SuppliersService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SalesOrderController {
	
	private final SalesOrderService salesOrderService ;
	
	//---新增---
	//單筆 ---暫
    @PostMapping("/api/SalesOrder/add")
    public ResponseEntity<SalesOrderRespoDTO> addSalesOrder(@Valid @RequestBody SalesOrderCreDTO salesOrderCreDTO,
    		 											@RequestParam Long loginUserId//測試用
    		 											) {
       //Long loginUserId = userUtil.getUserId();
        return ResponseEntity
            .status(HttpStatus.CREATED).body(salesOrderService.createSalesOrder(salesOrderCreDTO, loginUserId));
        
    }

	//---報廢---
    @PutMapping("/api/SalesOrder/{salesOrderId}/void")
    public ResponseEntity<SalesOrderRespoDTO> voidSalesOrder(
            @PathVariable Long salesOrderId,
            @RequestParam Long loginUserId,
            @Valid @RequestBody SalesOrderVoidDTO voidDTO) {

        return ResponseEntity.ok(
                salesOrderService.voidSalesOrder(
                        salesOrderId,
                        loginUserId,
                        voidDTO.getVoidReason()
                )
        );
    }
    
	//---查詢---
    @GetMapping("/api/SalesOrder/find/{salesOrderId}")
    public ResponseEntity<SalesOrderRespoDTO> findSupplierById(
        @PathVariable Long salesOrderId) {

    return ResponseEntity.ok(
    		salesOrderService.findSalesOrderById(salesOrderId)
    );
    }
    
	
	//
}
