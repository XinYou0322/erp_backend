package com.example.demo.purchaseOrder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.inventories.InventoryService;
import com.example.demo.purchaseOrderItem.PurchaseOrderItems;
import com.example.demo.systemsetting.SystemSettingKey;
import com.example.demo.systemsetting.SystemSettingService;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PurchaseOrderReceivingService {

    private static final Set<PurchaseOrdersStatus> RECEIVABLE_STATUSES =
            Set.of(PurchaseOrdersStatus.APPROVED);

    private final PurchaseOrdersRepository purchaseOrdersRepository;
    private final UsersRepository usersRepository;
    private final InventoryService inventoryService;
    private final SystemSettingService systemSettingService;

    @Transactional(readOnly = true)
    public List<ReceivablePurchaseOrderDTO> findReceivable(LocalDate expectedDeliveryDate) {
        requireFeatureEnabled();
        List<PurchaseOrders> purchaseOrders = expectedDeliveryDate == null
                ? purchaseOrdersRepository.findReceivableOrderByExpectedDeliveryDate(
                        RECEIVABLE_STATUSES)
                : purchaseOrdersRepository
                        .findByExpectedDeliveryDateAndStatusInOrderByOrderNumberAsc(
                                expectedDeliveryDate, RECEIVABLE_STATUSES);

        return purchaseOrders
                .stream()
                .map(ReceivablePurchaseOrderDTO::fromEntity)
                .toList();
    }

    @Transactional
    public PurchaseOrderResponseDTO receive(
            Long purchaseOrderId,
            PurchaseOrderReceiveRequestDTO request,
            Long receivedByUserId) {
        requireFeatureEnabled();
        if (receivedByUserId == null) {
            throw new IllegalArgumentException("尚未登入，無法執行收貨");
        }

        PurchaseOrders purchaseOrder = purchaseOrdersRepository
                .findByIdForReceiving(purchaseOrderId)
                .orElseThrow(() -> new IllegalArgumentException("找不到採購單"));

        if (!RECEIVABLE_STATUSES.contains(purchaseOrder.getStatus())) {
            if (purchaseOrder.getStatus() == PurchaseOrdersStatus.RECEIVED) {
                throw new IllegalStateException("此採購單已完成收貨，不可重複入庫");
            }
            throw new IllegalStateException("目前採購單狀態不可收貨");
        }
        if (purchaseOrder.getItems() == null || purchaseOrder.getItems().isEmpty()) {
            throw new IllegalStateException("採購單沒有可收貨的明細");
        }

        User receiver = usersRepository.findById(receivedByUserId)
                .orElseThrow(() -> new IllegalArgumentException("找不到收貨人資料"));
        Map<Long, LocalDate> expiryDates = getExpiryDates(request, purchaseOrder.getItems());

        for (PurchaseOrderItems item : purchaseOrder.getItems()) {
            inventoryService.receive(
                    item.getMaterial(),
                    item.getQuantity(),
                    expiryDates.get(item.getId()),
                    purchaseOrder.getId());
        }

        purchaseOrder.setStatus(PurchaseOrdersStatus.RECEIVED);
        purchaseOrder.setReceivedBy(receiver);
        purchaseOrder.setReceivedAt(LocalDateTime.now());
        return PurchaseOrderResponseDTO.fromEntity(purchaseOrdersRepository.save(purchaseOrder));
    }

    private Map<Long, LocalDate> getExpiryDates(
            PurchaseOrderReceiveRequestDTO request,
            List<PurchaseOrderItems> orderItems) {
        Map<Long, LocalDate> expiryDates = new HashMap<>();
        if (request == null || request.getItems() == null) {
            return expiryDates;
        }

        Set<Long> orderItemIds = new HashSet<>();
        for (PurchaseOrderItems orderItem : orderItems) {
            orderItemIds.add(orderItem.getId());
        }
        for (PurchaseOrderReceiveRequestDTO.Item requestItem : request.getItems()) {
            if (requestItem == null || requestItem.getPurchaseOrderItemId() == null) {
                throw new IllegalArgumentException("採購明細 ID 不得為空");
            }
            Long itemId = requestItem.getPurchaseOrderItemId();
            if (!orderItemIds.contains(itemId)) {
                throw new IllegalArgumentException("收貨資料包含不屬於此採購單的明細");
            }
            if (expiryDates.containsKey(itemId)) {
                throw new IllegalArgumentException("同一筆採購明細不可重複輸入");
            }
            expiryDates.put(itemId, requestItem.getExpiryDate());
        }
        return expiryDates;
    }

    private void requireFeatureEnabled() {
        if (!systemSettingService.isEnabled(SystemSettingKey.PURCHASE_ORDER_RECEIVING_ENABLED)) {
            throw new IllegalStateException("採購單收貨功能尚未啟用");
        }
    }
}
