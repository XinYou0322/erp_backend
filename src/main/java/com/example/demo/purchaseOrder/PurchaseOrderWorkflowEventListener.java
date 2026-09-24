package com.example.demo.purchaseOrder;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.example.demo.workflow.enums.DocumentType;
import com.example.demo.workflow.event.WorkflowStatusChangedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//【我新增】Workflow 核准/駁回後，同步更新 PurchaseOrder 本身的商業狀態。
@Component
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderWorkflowEventListener {

    private final PurchaseOrdersRepository purchaseOrdersRepo;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleWorkflowStatusChanged(WorkflowStatusChangedEvent event) {
        if (event.documentType() != DocumentType.ORDER) {
            return;
        }

        try {
            PurchaseOrders purchaseOrder = purchaseOrdersRepo.findById(event.documentId()).orElse(null);
            if (purchaseOrder == null) {
                log.error("找不到採購單，documentId={}", event.documentId());
                return;
            }

            switch (event.newStatus()) {
                case APPROVED -> purchaseOrder.setStatus(PurchaseOrdersStatus.APPROVED);
                case REJECTED -> purchaseOrder.setStatus(PurchaseOrdersStatus.REJECTED);
                case CANCELLED -> purchaseOrder.setStatus(PurchaseOrdersStatus.CANCELLED);
                default -> {
                    log.warn("未處理的採購簽核狀態：{}", event.newStatus());
                    return;
                }
            }

            purchaseOrdersRepo.save(purchaseOrder);
        } catch (Exception e) {
            // AFTER_COMMIT 的同步失敗不能反向破壞已完成的 workflow transaction，因此記錄錯誤供後續追查。
            log.error("同步採購單狀態失敗，documentId={}", event.documentId(), e);
        }
    }
}
