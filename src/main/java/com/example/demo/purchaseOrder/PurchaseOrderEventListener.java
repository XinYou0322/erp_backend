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

@Component
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderEventListener {

    private final PurchaseOrdersRepository purchaseOrdersRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleWorkflowStatusChanged(WorkflowStatusChangedEvent event) {
        if (event.documentType() != DocumentType.ORDER) {
            return;
        }

        try {
            PurchaseOrders purchaseOrder = purchaseOrdersRepository
                    .findById(event.documentId())
                    .orElse(null);
            if (purchaseOrder == null) {
                log.error("找不到採購單, documentId={}", event.documentId());
                return;
            }

            switch (event.newStatus()) {
                case APPROVED -> purchaseOrder.setStatus(PurchaseOrdersStatus.APPROVED);
                case REJECTED -> purchaseOrder.setStatus(PurchaseOrdersStatus.REJECTED);
                case CANCELLED -> purchaseOrder.setStatus(PurchaseOrdersStatus.CANCELLED);
                default -> {
                    return;
                }
            }
            purchaseOrdersRepository.save(purchaseOrder);
        } catch (Exception exception) {
            log.error("同步採購單簽核狀態失敗, documentId={}", event.documentId(), exception);
        }
    }
}
