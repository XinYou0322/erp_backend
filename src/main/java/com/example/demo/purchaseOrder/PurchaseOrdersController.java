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
import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.security.access.prepost.PreAuthorize;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController 
@RequiredArgsConstructor
public class PurchaseOrdersController {
    private final PurchaseOrdersService purchaseOrdersService;
    // 【新增】簽核人選單由後端依 Session 身分決定。
    private final PurchaseApproverPolicy purchaseApproverPolicy;

    @GetMapping("/api/purchaseOrder/approvers")
    public List<PurchaseApproverPolicy.Option> approvers(
            @SessionAttribute(name = "userId", required = false) Long loginUserId) {
        return purchaseApproverPolicy.options(loginUserId);
    }

    private final PurchaseOrderReceivingService purchaseOrderReceivingService;
   

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
          // 【新增】範圍來自頁籤，登入者只能來自後端 Session，不新增 LoginUserService。
          @RequestParam(defaultValue = "overview") String scope,
          @SessionAttribute(name = "userId", required = false) Long loginUserId,
          @RequestParam(required = false) String keyword, // 搜尋：單號 / 金額 / 建立人 / 供應商 / 品名
          @RequestParam(required = false) PurchaseOrdersStatus status,
          @RequestParam(required = false) Long supplierId,
          @RequestParam(required = false) BigDecimal minAmount,
          @RequestParam(required = false) BigDecimal maxAmount,
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
          @RequestParam(defaultValue = "0") int page, //第幾頁
          @RequestParam(defaultValue = "10") int size //預設一頁10筆
  ) {
      // 【新增】總覽限定三種狀態；自己限定 Session 使用者，包含本人所有狀態。
      if (!"overview".equals(scope) && !"mine".equals(scope)) {
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "不支援的頁籤");
      }
      Long createdByUserId = null;
      List<PurchaseOrdersStatus> visibleStatuses = List.of(
              PurchaseOrdersStatus.PENDING_APPROVAL, PurchaseOrdersStatus.APPROVED, PurchaseOrdersStatus.RECEIVED);
      if ("mine".equals(scope)) {
          if (loginUserId == null || loginUserId <= 0) {
              throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "請先登入");
          }
          createdByUserId = loginUserId;
          visibleStatuses = List.of(PurchaseOrdersStatus.values());
      }
      // 【修改】把頁籤條件交給 Service，在分頁前套用。
      Page<PurchaseOrderResponseDTO> result =
              purchaseOrdersService.findPurchaseOrderPage(
                      visibleStatuses,
                      createdByUserId,
                      keyword,
                      status,
                      supplierId,
                      minAmount,
                      maxAmount,
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
