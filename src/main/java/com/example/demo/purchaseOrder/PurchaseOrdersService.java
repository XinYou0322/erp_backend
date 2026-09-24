package com.example.demo.purchaseOrder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import com.example.demo.suppliers.SupplierStatus;
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
    private final WorkflowService workflowService;
    
    //---新增---
    // 新增一張採購單
    @Transactional
    public PurchaseOrderResponseDTO insertPurchaseOrder(PurchaseOrderCreateDTO dto,Long loginUserId) {

    Suppliers supplier = getActiveSupplier(dto.getSupplierId());

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
    Set<Long> materialIds = new HashSet<>();

    //處理明細 
    for (int i = 0; i < dto.getItems().size(); i++) {

        PurchaseOrderItemsCreDTO itemDTO =
                dto.getItems().get(i);

        //查詢原物料
        if (!materialIds.add(itemDTO.getMaterialId())) {
            throw new IllegalArgumentException("原物料 ID：" + itemDTO.getMaterialId() + " 重複出現");
        }
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

        // 使用者選擇「建立後送簽」時，沿用既有送簽驗證與 Workflow 流程。
        // null 或未傳欄位時一律視為儲存草稿，兼容舊版前端。
        if (Boolean.FALSE.equals(dto.getSaveAsDraft())) {
            return submitPurchaseOrder(savedOrder.getId(), loginUserId);
        }

    	//Entity -> DTO
    	return PurchaseOrderResponseDTO.fromEntity(savedOrder);
}
    
    // 一次新增多張採購單；其中任何一張失敗時，整批交易會回滾。
    @Transactional
    public List<PurchaseOrderResponseDTO> insertPurchaseOrders(
            List<PurchaseOrderCreateDTO> dtoList,
            Long loginUserId) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new IllegalArgumentException("採購單清單不可為空");
        }

        usersRepo.findById(loginUserId)
                .orElseThrow(() -> new IllegalArgumentException("找不到登入者資料"));

        List<PurchaseOrderResponseDTO> result = new ArrayList<>();
        for (PurchaseOrderCreateDTO dto : dtoList) {
            result.add(insertPurchaseOrder(dto, loginUserId));
        }
        return result;
    }

    //單號
    private String createTemporaryOrderNumber() {
        return "TMP-" + UUID.randomUUID();
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
        //建立時是 ACTIVE，不代表送簽時仍 ACTIVE；送簽前再次驗證供應商狀態。
        if (purchaseOrder.getSupplier().getStatus() != SupplierStatus.ACTIVE) {
            throw new IllegalStateException("此供應商目前不是合作中狀態，無法送出採購簽核");
        }
        //檢查預計到貨日
        if (purchaseOrder.getExpectedDeliveryDate() == null) {
            throw new RuntimeException( "請填寫預計到貨日後再送出簽核");
        }
        if (purchaseOrder.getExpectedDeliveryDate().isBefore(LocalDate.now())) {
            throw new IllegalStateException("預計到貨日不可早於今天");
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
    //---取消---
    @Transactional
    public PurchaseOrderResponseDTO cancelPurchaseOrder(Long purchaseOrderId, Long loginUserId) {
        PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(purchaseOrderId)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單"));

        if (!purchaseOrder.getCreatedBy().getId().equals(loginUserId)) {
            throw new IllegalStateException("只有採購單申請人可以取消採購單");
        }

        if (purchaseOrder.getStatus() != PurchaseOrdersStatus.DRAFT
                && purchaseOrder.getStatus() != PurchaseOrdersStatus.REJECTED
                && purchaseOrder.getStatus() != PurchaseOrdersStatus.PENDING_APPROVAL) {
            throw new IllegalStateException("此採購單目前狀態不可取消");
        }

        // 已送簽但尚未簽核時，同時取消 Workflow，避免簽核中心還留著待審資料。
        if (purchaseOrder.getStatus() == PurchaseOrdersStatus.PENDING_APPROVAL) {
            workflowService.cancelWorkflow(purchaseOrderId, DocumentType.ORDER);
        }

        purchaseOrder.setStatus(PurchaseOrdersStatus.CANCELLED);
        return PurchaseOrderResponseDTO.fromEntity(purchaseOrdersRepo.save(purchaseOrder));
    }
    // ---查詢---
    // 單筆
    @Transactional(readOnly = true)
    public PurchaseOrderResponseDTO findPurchaseOrderById(Long id) {
    	PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單，採購單 ID：" + id));
         return PurchaseOrderResponseDTO.fromEntityWithItems(purchaseOrder);
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
    @Transactional
    public PurchaseOrderResponseDTO updatePurchaseOrder(
            Long id,
            PurchaseOrderUpdateDTO updateDTO,
            Long loginUserId) {

        PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單"));

        // 只能修改自己建立的採購單
        if (!purchaseOrder.getCreatedBy().getId().equals(loginUserId)) {
            throw new IllegalStateException("只能修改自己建立的採購單");
        }

        // 只有草稿、駁回狀態可以修改
        if (purchaseOrder.getStatus() != PurchaseOrdersStatus.DRAFT
                && purchaseOrder.getStatus() != PurchaseOrdersStatus.REJECTED) {
            throw new IllegalStateException("此採購單目前狀態不可修改");
        }

        // 修改供應商
        Suppliers supplier = getActiveSupplier(updateDTO.getSupplierId());
        purchaseOrder.setSupplier(supplier);

        // 修改預計到貨日
        purchaseOrder.setExpectedDeliveryDate(
                updateDTO.getExpectedDeliveryDate()
        );

        // 修改備註
        purchaseOrder.setDecisionRemark(
                updateDTO.getDecisionRemark() == null
                        ? null
                        : updateDTO.getDecisionRemark().trim()
        );

        // 建立目前資料庫中的明細 Map
        Map<Long, PurchaseOrderItems> existingItems = new HashMap<>();

        for (PurchaseOrderItems item : purchaseOrder.getItems()) {
            existingItems.put(item.getId(), item);
        }
        //驗證前端傳入的明細
        Set<Long> incomingItemIds = new HashSet<>();
        Set<Long> materialIds = new HashSet<>();

        for (PurchaseOrderItemUpdateDTO itemDTO : updateDTO.getItems()) {

            // 防止同一個原物料重複加入
            if (!materialIds.add(itemDTO.getMaterialId())) {
                throw new IllegalArgumentException(
                        "原物料 ID：" + itemDTO.getMaterialId() + " 重複出現"
                );
            }
            //代表這筆是「原本就存在的採購明細」

            if (itemDTO.getId() != null) {

                // 防止同一個明細 id 重複傳入
                if (!incomingItemIds.add(itemDTO.getId())) {
                    throw new IllegalArgumentException(
                            "採購明細 ID：" + itemDTO.getId() + " 重複出現"
                    );
                }

                // 防止修改到別張採購單的明細
                if (!existingItems.containsKey(itemDTO.getId())) {
                    throw new IllegalArgumentException(
                            "採購明細 ID：" + itemDTO.getId()
                                    + " 不屬於此採購單"
                    );
                }
            }
        }

        // 刪除前端已移除的明細
        purchaseOrder.getItems().removeIf(
                oldItem -> !incomingItemIds.contains(oldItem.getId())
        );

        // 新增 / 修改採購明細
        for (PurchaseOrderItemUpdateDTO itemDTO : updateDTO.getItems()) {

            Material material = materialRepo.findById(itemDTO.getMaterialId())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "找不到原物料，ID："
                                            + itemDTO.getMaterialId()
                            )
                    );

            PurchaseOrderItems item;
            //代表前端新增了一筆新的明細

            if (itemDTO.getId() == null) {

                item = new PurchaseOrderItems();

                purchaseOrder.addItem(item);

            } else {
            	//修改原本的明細
                item = existingItems.get(itemDTO.getId());
            }

            item.setMaterial(material);
            item.setQuantity(itemDTO.getQuantity());
            item.setUnitPrice(itemDTO.getPrice());

        }

         //重新計算採購單總金額
        BigDecimal total = BigDecimal.ZERO;

        for (PurchaseOrderItems item : purchaseOrder.getItems()) {
            total = total.add(item.getSubtotal());
        }

        purchaseOrder.setTotal(total);

        //統一儲存

        PurchaseOrders savedPurchaseOrder =
                purchaseOrdersRepo.save(purchaseOrder);

        return PurchaseOrderResponseDTO.fromEntity(savedPurchaseOrder);
    }
    private Suppliers getActiveSupplier(Long supplierId) {
        Suppliers supplier = suppliersRepo.findById(supplierId)
                .orElseThrow(() -> new IllegalArgumentException("找不到此供應商"));

        if (supplier.getStatus() != SupplierStatus.ACTIVE) {
            throw new IllegalStateException("供應商「" + supplier.getName() + "」目前不是合作中狀態");
        }
        return supplier;
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
