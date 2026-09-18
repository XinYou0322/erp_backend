package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.purchaseOrderItem.PurchaseOrderItemUpdateDTO;
import com.example.demo.purchaseOrderItem.PurchaseOrderItems;
import com.example.demo.purchaseOrderItem.PurchaseOrderItemsCreDTO;
import com.example.demo.purchaseOrderItem.PurchaseOrderItemsRepository;
import com.example.demo.suppliers.Suppliers;
import com.example.demo.suppliers.SuppliersRepository;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;
import com.example.demo.workflow.dto.CreateWorkflowRequest;
import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.service.WorkflowService;

import lombok.RequiredArgsConstructor;


@Service 
@RequiredArgsConstructor
public class PurchaseOrdersService {
    private final PurchaseOrdersRepository purchaseOrdersRepo;
    private final SuppliersRepository suppliersRepo;
    private final MaterialRepository materialRepo;
    private final UsersRepository usersRepo;
    private final PurchaseOrderItemsRepository purchaseOrderItemsRepo;
    private final WorkflowService workflowService;
    
    //---新增---
    // 新增一張採購單
    @Transactional
    public PurchaseOrderResponseDTO insertPurchaseOrder(PurchaseOrderCreateDTO dto,Long loginUserId) {
    // supplierId → Supplier Entity
    Suppliers supplier = suppliersRepo.findById(dto.getSupplierId())
        .orElseThrow(() -> new IllegalArgumentException("找不到此供應商"));

    User creator = usersRepo.findById(loginUserId)   
            .orElseThrow(() -> new RuntimeException("找不到登入者資料"));
    
    User approver = usersRepo.findById(dto.getApprovedByUserId())
    		.orElseThrow(() -> new RuntimeException("找不到簽核人資料"));
    
    PurchaseOrders purchaseOrder = new PurchaseOrders();
    purchaseOrder.setOrderNumber(createTemporaryOrderNumber());
    purchaseOrder.setSupplier(supplier);
    purchaseOrder.setCreatedBy(creator);
    purchaseOrder.setApprovedBy(approver);
    purchaseOrder.setExpectedDeliveryDate(dto.getExpectedDeliveryDate());
    purchaseOrder.setStatus(PurchaseOrdersStatus.DRAFT);//草稿
    
    BigDecimal totalAmount = BigDecimal.ZERO;
    
    //處理明細 
    for (int i = 0; i < dto.getItems().size(); i++) {

        PurchaseOrderItemsCreDTO itemDTO =
                dto.getItems().get(i);

        //查詢原物料
        Material material = materialRepo
                .findById(itemDTO.getMaterialId())
                .orElseThrow(() -> new RuntimeException("找不到原物料"));

        // 
        PurchaseOrderItems item = new PurchaseOrderItems();
        //多對一關聯：這筆明細屬於哪張採購單
        item.setPurchaseOrder(purchaseOrder);
        item.setMaterial(material);
        item.setQuantity(itemDTO.getQuantity());
        item.setUnitPrice(itemDTO.getPrice());
        // 小計 = 數量 乘(multiply) 單價     
        BigDecimal subtotal =
                itemDTO.getQuantity().multiply(itemDTO.getPrice());

        //總金額
        totalAmount = totalAmount.add(subtotal);

        //將明細加入主單的明細集合
        purchaseOrder.getItems().add(item);
    	}
    
    	//設定後端計算出的總金額
    	purchaseOrder.setTotal(totalAmount);

    	// 主單及所有明細
    	PurchaseOrders savedOrder =
            purchaseOrdersRepo.save(purchaseOrder);

    	//Entity -> DTO
    	return PurchaseOrderResponseDTO.fromEntity(savedOrder);
}
    
    //單號 用時間
    private String createTemporaryOrderNumber() {
        return "TMP-" + System.currentTimeMillis(); //目前時間的毫秒數
    }
    //送出
    @Transactional
    public PurchaseOrderResponseDTO submitPurchaseOrder(
            Long purchaseOrderId,
            Long loginUserId) {

        PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(purchaseOrderId)
                        .orElseThrow(() ->new RuntimeException("找不到採購單" ));

        //只有草稿或退回的採購單可以重新送出
        if (purchaseOrder.getStatus() != PurchaseOrdersStatus.DRAFT
                && purchaseOrder.getStatus() != PurchaseOrdersStatus.REJECTED) {
            throw new RuntimeException("目前採購單狀態不可送出簽核" );
        }
        //本人才能送
        if (!purchaseOrder.getCreatedBy().getId().equals(loginUserId)) {
            throw new RuntimeException( "只有採購單申請人可以送出簽核");
        }
        //檢查預計到貨日
        if (purchaseOrder.getExpectedDeliveryDate() == null) {
            throw new RuntimeException( "請填寫預計到貨日後再送出簽核");
        }
        //檢查採購明細
        if (purchaseOrder.getItems() == null
                || purchaseOrder.getItems().isEmpty()) {
            throw new RuntimeException(
                    "採購單至少需要一筆採購明細"
            );
        }
        // Workflow
        CreateWorkflowRequest workflowRequest = new CreateWorkflowRequest();
        workflowRequest.setDocumentType( DocumentType.ORDER);
        workflowRequest.setDocumentId( purchaseOrder.getId());
        workflowRequest.setApplicantId( purchaseOrder.getCreatedBy().getId());
        workflowRequest.setApproverId(purchaseOrder.getApprovedBy().getId());
        workflowRequest.setRemark(purchaseOrder.getDecisionRemark());
        
        workflowService.startWorkflow(workflowRequest);
        //更新採購單狀態
        purchaseOrder.setStatus(
                PurchaseOrdersStatus.PENDING_APPROVAL
        );

        PurchaseOrders savedPurchaseOrder =purchaseOrdersRepo.save(purchaseOrder );

        return PurchaseOrderResponseDTO
                .fromEntity(savedPurchaseOrder);
    }
    
    // ---查詢---
    // 單筆
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDTO findPurchaseOrderById(Long id) {
    	PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單，採購單 ID：" + id));
         return PurchaseOrderResponseDTO.fromEntity(purchaseOrder);
    }
    // 多筆
    @Transactional(readOnly = true)
    public PurchaseOrderQueryResponseDTO findPurchaseOrdersByIds(
        List<Long> ids) {
    	List<PurchaseOrders> purchaseOrders = purchaseOrdersRepo.findAllById(ids);
    	List<Long> foundIds = new ArrayList<>();
    	for (PurchaseOrders purchaseOrder: purchaseOrders) {
            foundIds.add(purchaseOrder.getId());
        }
    	List<Long> notFoundIds = new ArrayList<>();
        for (Long id : ids) {
        if (!foundIds.contains(id)) {
            notFoundIds.add(id);
        }
        }
        //將每筆 Entity -> ResponseDTO
        List<PurchaseOrderResponseDTO> responseDTOList = new ArrayList<>();
        
        for (PurchaseOrders purchaseOrder : purchaseOrders) {

            PurchaseOrderResponseDTO responseDTO = PurchaseOrderResponseDTO.fromEntity(purchaseOrder);
                   
            responseDTOList.add(responseDTO);
        }    
        // 建立最後回傳結果
        PurchaseOrderQueryResponseDTO result = new PurchaseOrderQueryResponseDTO();           

        // 查到的資料
        result.setPurchaseOrders(responseDTOList);

        // 沒查到的 ID
        result.setNotFoundIds(notFoundIds);

        //設定提示訊息
        if (notFoundIds.isEmpty()) {

            result.setMessage("全部採購單查詢成功");

        } else {

            result.setMessage(
                    "以下採購單查無此資料：" + notFoundIds
            );
        }


        // 同時回傳「有的資料 + 沒有的資料」
        return result;
    }
    // 全部
    @Transactional(readOnly = true)
    public List<PurchaseOrderResponseDTO> findAllPurchaseOrders() {
    	List<PurchaseOrders> purchaseOrders = purchaseOrdersRepo.findAll();
    	List<PurchaseOrderResponseDTO> responseDTOList =  new ArrayList<>();
    	for (PurchaseOrders purchaseOrder : purchaseOrders) {

            PurchaseOrderResponseDTO responseDTO = PurchaseOrderResponseDTO.fromEntity(purchaseOrder);
                   
            responseDTOList.add(responseDTO);
        }        
    	
    return responseDTOList;
    }
    //分頁
 
    @Transactional(readOnly = true)
    public Page<PurchaseOrderResponseDTO> findPurchaseOrderPage(
			         String keyword,
			         PurchaseOrdersStatus status,
			         Long supplierId,
			         LocalDate startDate,
			         LocalDate endDate,
			         int page,
			         int size) {

     // 10/30/50
     // 一律改回預設 10 筆
     if (size != 10
             && size != 30
             && size != 50) {
         size = 10;
     }
     if (page < 0) {
         page = 0;
     }

     //檢查日期
     // 不合法 Ex. 2026/09/30 ~ 2026/09/01
     if (startDate != null
             && endDate != null
             && startDate.isAfter(endDate)) {
         throw new IllegalArgumentException(
                 "開始日期不能晚於結束日期"
         );
     }
     //LocalDate → LocalDateTime
     LocalDateTime startDateTime = null;
     LocalDateTime endDateTime = null;

     // 開始日期
     if (startDate != null) {
         startDateTime =
                 startDate.atStartOfDay();
     }

     // 結束日期
     if (endDate != null) {
         //endDate = 2026/09/30 實際會變成：2026/10/01 00:00
    	 //Repository 使用： po.createdAt < endDateTime 
    	 //所以 9/30 這一整天都會包含
         endDateTime =
                 endDate
                         .plusDays(1)
                         .atStartOfDay();
     }

     //搜尋關鍵字
     String searchKeyword = null;

     if (keyword != null
             && !keyword.trim().isEmpty()) {
         searchKeyword =
                 keyword.trim();
     }

     // 6. 建立分頁設定
     Pageable pageable =
             PageRequest.of(
                     page,
                     size,
                     // 最新建立的採購單排最前面
                     Sort.by(
                             Sort.Direction.DESC,
                             "createdAt"
                     )
             );

     // 7. Repository 查詢
     Page<PurchaseOrders> purchaseOrderPage =
             purchaseOrdersRepo
                     .searchPurchaseOrders(
                             status,
                             supplierId,
                             startDateTime,
                             endDateTime,
                             searchKeyword,
                             pageable
                     );

     // 8. Entity → DTO
 
     List<PurchaseOrderResponseDTO> dtoList =
             new ArrayList<>();

     for (PurchaseOrders purchaseOrder
             : purchaseOrderPage.getContent()) {
         PurchaseOrderResponseDTO dto =
                 PurchaseOrderResponseDTO
                         .fromEntity(purchaseOrder);

         dtoList.add(dto);
     }

     //Page<DTO>
  
     return new PageImpl<PurchaseOrderResponseDTO>(
             dtoList,
             pageable,
             purchaseOrderPage.getTotalElements()
     );
 }
  
    // ---修改---
    public PurchaseOrderResponseDTO updatePurchaseOrder(Long id, PurchaseOrderUpdateDTO updateDTO,Long loginUserId) {
        // 先確認這筆採購單存不存在
        PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單"));
        
        if (!purchaseOrder.getCreatedBy().getId().equals(loginUserId)) {
            throw new RuntimeException( "只能修改自己建立的採購單");
        }
        //檢查狀態 DRAFT/REJECTED
        if (purchaseOrder.getStatus() != PurchaseOrdersStatus.DRAFT
                && purchaseOrder.getStatus() != PurchaseOrdersStatus.REJECTED) {

            throw new RuntimeException("此採購單目前狀態不可修改"
                    );
                }           
        Suppliers supplier = suppliersRepo.findById(updateDTO.getSupplierId())
                .orElseThrow(() ->
                        new RuntimeException("找不到供應商"));
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setExpectedDeliveryDate(updateDTO.getExpectedDeliveryDate());
        purchaseOrder.setDecisionRemark(updateDTO.getDecisionRemark());
        
        //紀錄這次前端傳回來的「既有明細 ID」
        Set<Long> updateItemIds = new HashSet<>();
        
        for (int i = 0 ; i < updateDTO.getItems().size() ; i++) {
        	
        	PurchaseOrderItemUpdateDTO itemDTO = updateDTO.getItems().get(i);
        	if (itemDTO.getId() != null) {
        		updateItemIds.add( itemDTO.getId() );
        	}
        }
        //刪除採購明細
        Iterator<PurchaseOrderItems> iterator =
                purchaseOrder.getItems().iterator();


        while (iterator.hasNext()) {

            PurchaseOrderItems oldItem =
                    iterator.next();

            if (!updateItemIds.contains(oldItem.getId())) {

                iterator.remove();
            }
        }
        //處理前端傳回來的所有明細
        for (int i = 0 ; i < updateDTO.getItems().size() ; i++) {

               PurchaseOrderItemUpdateDTO itemDTO = updateDTO.getItems().get(i);

               // 7-1. 查詢原物料
               Material material = materialRepo.findById(itemDTO.getMaterialId())
                       .orElseThrow(() -> new RuntimeException("找不到原物料"));


               // 7-2. id == null
               // 代表使用者新增了一筆明細
               if (itemDTO.getId() == null) {

                   PurchaseOrderItems newItem = new PurchaseOrderItems();
                   newItem.setMaterial(material);
                   newItem.setQuantity(itemDTO.getQuantity());
                   newItem.setUnitPrice(itemDTO.getPrice());

                   // 使用 Entity 原本寫好的雙向關聯方法
                   purchaseOrder.addItem(newItem);
               }

               // 7-3. id != null
               // 代表修改原本存在的明細

               else {
                   PurchaseOrderItems oldItem = null;

                   // 從目前這張採購單的 items 裡
                   // 找對應的 item
                   for (int j = 0 ; j < purchaseOrder.getItems().size() ; j++) {

                       PurchaseOrderItems currentItem = purchaseOrder.getItems().get(j);

                       if (currentItem.getId() != null
                               && currentItem.getId().equals(itemDTO.getId())) {

                           oldItem = currentItem;
                           break;
                       }
                   }

                   // 找不到代表：
                   // 這個 itemId 不屬於目前的採購單

                   if (oldItem == null) {
                       throw new RuntimeException(
                               "找不到此採購單的採購明細"
                       );
                   }
                   // 修改原物料
                   oldItem.setMaterial(material);

                   // 修改數量
                   oldItem.setQuantity(itemDTO.getQuantity()
                   );

                   // 修改價格
                   oldItem.setUnitPrice(itemDTO.getPrice()
                   );
               }
           }

           // 重新計算總金額
           BigDecimal total = BigDecimal.ZERO;

           for (int i = 0 ; i < purchaseOrder.getItems().size() ; i++) {

               PurchaseOrderItems item = purchaseOrder.getItems().get(i);

               BigDecimal subtotal = item.getQuantity().multiply( item.getUnitPrice() );

               total = total.add(subtotal);
           }

           //更新採購單總金額
           purchaseOrder.setTotal(total);

           // 10. 儲存主單

           // 因為 CascadeType.ALL：
           // 新增明細 → 自動 INSERT
           // 修改明細 → 自動 UPDATE
           // 因為 orphanRemoval = true：
           // List 移除明細 → 自動 DELETE
           PurchaseOrders savedPurchaseOrder = purchaseOrdersRepo.save( purchaseOrder );

           // Entity → ResponseDTO
   
           return PurchaseOrderResponseDTO.fromEntity(savedPurchaseOrder);
       }
    
    //報廢

//    // 刪除
//    // 刪除單筆
//    public void deletePurchaseOrder(Long id) {
//        if (!purchaseOrdersRepo.existsById(id)) {
//            throw new IllegalArgumentException("找不到採購單");
//        }
//        purchaseOrdersRepo.deleteById(id);
//    }


    // 採購流程
   
 

    // 核准採購單

    // 駁回採購單

    // 完成採購單

}