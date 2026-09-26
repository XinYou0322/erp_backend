package com.example.demo.salesOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.suppliers.SupplierCreDTO;
import com.example.demo.suppliers.SupplierRespoDTO;
import com.example.demo.suppliers.SuppliersService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class SalesOrderController {
	
    private final SalesOrderService salesOrderService ;

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    public ResponseEntity<Map<String, String>> handleBusinessError(RuntimeException exception) {
        return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
    }
	
	//---新增---
	//單筆 ---暫
    @PostMapping("/api/SalesOrder/add")
    public ResponseEntity<SalesOrderRespoDTO> addSalesOrder(@Valid @RequestBody SalesOrderCreDTO salesOrderCreDTO,
	        @SessionAttribute(name = "userId", required = false) Long loginUserId
	) {
		 if (loginUserId == null) {
		        throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED, "請先登入");
		    }
        return ResponseEntity
            .status(HttpStatus.CREATED).body(salesOrderService.createSalesOrder(salesOrderCreDTO, loginUserId));
        
    }

	//---報廢---
    @PutMapping("/api/SalesOrder/{salesOrderId}/void")
    public ResponseEntity<SalesOrderRespoDTO> voidSalesOrder(
            @PathVariable Long salesOrderId,
            @SessionAttribute(name = "userId", required = false) Long loginUserId,
            @Valid @RequestBody SalesOrderVoidDTO voidDTO) {
        

	 if (loginUserId == null) {
	        throw new ResponseStatusException(
	                HttpStatus.UNAUTHORIZED, "請先登入");
	    }
        return ResponseEntity.ok(
                salesOrderService.voidSalesOrder(
                        salesOrderId,
                        loginUserId,
                        voidDTO.getVoidReason()
                )
        );
    }
    
	//---查詢---
    @GetMapping("/api/SalesOrder/find/{Id}")
    public ResponseEntity<SalesOrderDetailRespoDTO> findSupplierById(
        @PathVariable Long Id) {

    return ResponseEntity.ok(
    		salesOrderService.findById(Id)
    );
    }

    //分頁
    @GetMapping("/api/SalesOrder/page")
    public ResponseEntity<Page<SalesOrderListRespoDTO>>
            findSalesOrderPage(

            // 關鍵字:單號 / 商品名稱 / 商品 SKU
            @RequestParam(required = false)
            String keyword,

            // 狀態
            @RequestParam(required = false)
            SalesOrderStatus status,

            // 金流
            @RequestParam(required = false)
            PaymentMethod paymentMethod,

            // 建立人
            @RequestParam(required = false)
            Long createdById,

            // 開始日期
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,

            // 結束日期
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate endDate,
            
            // 最低金額
            @RequestParam(required = false)
            BigDecimal minAmount,

            // 最高金額
            @RequestParam(required = false)
            BigDecimal maxAmount,

            // 第幾頁從 0 開始
            @RequestParam(defaultValue = "0")
            int page,

            // 每頁幾筆 10/30/50
            @RequestParam(defaultValue = "10")
            int size
    ) {
        Page<SalesOrderListRespoDTO> result =
                salesOrderService.findSalesOrderPage(
                        keyword,
                        status,
                        paymentMethod,
                        createdById,
                        startDate,
                        endDate,
                        minAmount,
                        maxAmount,
                        page,
                        size
                );

        return ResponseEntity.ok(result);
    }
    
}
