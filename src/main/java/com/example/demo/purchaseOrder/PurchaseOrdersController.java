package com.example.demo.purchaseOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.suppliers.SupplierMultiQueryRespoDTO;
import com.example.demo.suppliers.SupplierRespoDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
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
  public ResponseEntity<PurchaseOrderResponseDTO> addPurchaseOrder(@RequestBody@Valid PurchaseOrderCreateDTO dto, 	       
		  @SessionAttribute(name = "userId", required = false) Long loginUserId
			) {
		 if (loginUserId == null) {
		        throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED, "請先登入");
		    }
    return ResponseEntity.status(HttpStatus.CREATED).body(purchaseOrdersService.insertPurchaseOrder(dto, loginUserId));
}

  // 多筆新增
  @PostMapping("/api/purchaseOrder/addAll")
  public ResponseEntity<List<PurchaseOrderResponseDTO>> addPurchaseOrders(
          @Valid @RequestBody
          @NotEmpty(message = "採購單清單不可為空")
          List<@Valid PurchaseOrderCreateDTO> dtoList,
	      @SessionAttribute(name = "userId", required = false) Long loginUserId
	) {
		 if (loginUserId == null) {
		        throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED, "請先登入");
		    }

      return ResponseEntity.status(HttpStatus.CREATED)
              .body(purchaseOrdersService.insertPurchaseOrders(dtoList, loginUserId));
  }

  //---查詢---
  @GetMapping("/api/purchaseOrder/find/{id}")
  public ResponseEntity<PurchaseOrderResponseDTO> findSupplierById(  @PathVariable Long id) {
    
  return ResponseEntity.ok(purchaseOrdersService.findPurchaseOrderById(id)
		  
  );
  }
  //多筆
  @GetMapping("/api/purchaseOrder/findByIds")
  public ResponseEntity<PurchaseOrderQueryResponseDTO> findSuppliersByIds(
      @RequestParam("ids") List<Long> ids) {

  return ResponseEntity.ok(
		  purchaseOrdersService.findPurchaseOrdersByIds(ids)
  );
}
  //全部
  @GetMapping("/api/purchaseOrder/findAll")
  public ResponseEntity<List<PurchaseOrderResponseDTO>> findSuppliersAll(
      ) {

  return ResponseEntity.ok(
		  purchaseOrdersService.findAllPurchaseOrders()
  );
}
  //分頁
  @GetMapping("/api/purchaseOrder/page")
  public ResponseEntity<Page<PurchaseOrderResponseDTO>>
          findPurchaseOrderPage(
          @RequestParam(required = false) String keyword, // 搜尋：採購單號 / 品名
          @RequestParam(required = false) PurchaseOrdersStatus status,
          @RequestParam(required = false) Long supplierId,
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
          @RequestParam(defaultValue = "0") int page, //第幾頁
          @RequestParam(defaultValue = "10") int size //預設一頁10筆
  ) {
      // Controller 把收到的條件交給 Service
      Page<PurchaseOrderResponseDTO> result =
              purchaseOrdersService.findPurchaseOrderPage(
                      keyword,
                      status,
                      supplierId,
                      startDate,
                      endDate,
                      page,
                      size
              );


      return ResponseEntity.ok(result);
  }
  
  //取消
  @PutMapping("/api/purchaseOrder/{id}/cancel")
  public ResponseEntity<PurchaseOrderResponseDTO> cancelPurchaseOrder(
          @PathVariable Long id,
	        @SessionAttribute(name = "userId", required = false) Long loginUserId
	) {
		 if (loginUserId == null) {
		        throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED, "請先登入");
		    }

      return ResponseEntity.ok(
              purchaseOrdersService.cancelPurchaseOrder(id, loginUserId)
      );
  }
  
  
  
  //送出
  @PostMapping("/api/purchaseOrder/{id}/submit")
  public ResponseEntity<PurchaseOrderResponseDTO> submitPurchaseOrder(
          @PathVariable Long id,
	        @SessionAttribute(name = "userId", required = false) Long loginUserId
	) {
		 if (loginUserId == null) {
		        throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED, "請先登入");
		    }

      PurchaseOrderResponseDTO result = purchaseOrdersService.submitPurchaseOrder(id,loginUserId);

      return ResponseEntity.ok(result);
  }
  //修改
  @PutMapping("/api/purchaseOrder/{purchaseOrderId}")
  public ResponseEntity<PurchaseOrderResponseDTO> updatePurchaseOrder(
          @PathVariable Long purchaseOrderId,
          @Valid @RequestBody PurchaseOrderUpdateDTO updateDTO,
	        @SessionAttribute(name = "userId", required = false) Long loginUserId
	) {
		 if (loginUserId == null) {
		        throw new ResponseStatusException(
		                HttpStatus.UNAUTHORIZED, "請先登入");
		    }

      // 呼叫 Service 修改採購單
      PurchaseOrderResponseDTO result = purchaseOrdersService.updatePurchaseOrder(purchaseOrderId,updateDTO,loginUserId);
   
      // 修改成功回傳 200 OK + 修改後資料
      return ResponseEntity.ok(result);
  }
 
}
