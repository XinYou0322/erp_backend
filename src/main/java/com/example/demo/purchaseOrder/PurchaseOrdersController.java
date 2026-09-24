package com.example.demo.purchaseOrder;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.suppliers.SupplierMultiQueryRespoDTO;
import com.example.demo.suppliers.SupplierRespoDTO;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PutMapping;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController 
@RequiredArgsConstructor
public class PurchaseOrdersController {
    private final PurchaseOrdersService purchaseOrdersService;
    private final PurchaseOrderReceivingService purchaseOrderReceivingService;
   

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

  @GetMapping("/api/purchaseOrder/receivable")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<List<ReceivablePurchaseOrderDTO>> findReceivablePurchaseOrders(
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
      try {
          return ResponseEntity.ok(purchaseOrderReceivingService.findReceivable(date));
      } catch (IllegalStateException exception) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
      }
  }

  @PostMapping("/api/purchaseOrder/{id}/receive")
  @PreAuthorize("isAuthenticated()")
  public ResponseEntity<PurchaseOrderResponseDTO> receivePurchaseOrder(
          @PathVariable Long id,
          @Valid @RequestBody(required = false) PurchaseOrderReceiveRequestDTO request,
          HttpServletRequest servletRequest) {
      HttpSession session = servletRequest.getSession(false);
      Long userId = session == null ? null : (Long) session.getAttribute("userId");
      try {
          return ResponseEntity.ok(purchaseOrderReceivingService.receive(id, request, userId));
      } catch (IllegalArgumentException exception) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
      } catch (IllegalStateException exception) {
          throw new ResponseStatusException(HttpStatus.CONFLICT, exception.getMessage(), exception);
      }
  }
  
  
  
  
  
  //送出
  @PostMapping("/api/purchaseOrder/{id}/submit")
  public ResponseEntity<PurchaseOrderResponseDTO> submitPurchaseOrder(
          @PathVariable Long id,
          @RequestParam Long loginUserId) {

      PurchaseOrderResponseDTO result = purchaseOrdersService.submitPurchaseOrder(id,loginUserId);

      return ResponseEntity.ok(result);
  }
  //修改
  @PutMapping("/api/purchaseOrder/{purchaseOrderId}")
  public ResponseEntity<PurchaseOrderResponseDTO> updatePurchaseOrder(
          @PathVariable Long purchaseOrderId,
          @Valid @RequestBody PurchaseOrderUpdateDTO updateDTO,
          @RequestParam Long loginUserId) {

      // 呼叫 Service 修改採購單
      PurchaseOrderResponseDTO result = purchaseOrdersService.updatePurchaseOrder(purchaseOrderId,updateDTO,loginUserId);
   
      // 修改成功回傳 200 OK + 修改後資料
      return ResponseEntity.ok(result);
  }
 
}
