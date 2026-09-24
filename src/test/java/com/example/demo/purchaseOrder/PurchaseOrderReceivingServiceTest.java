package com.example.demo.purchaseOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.demo.inventories.InventoryService;
import com.example.demo.materials.Material;
import com.example.demo.purchaseOrderItem.PurchaseOrderItems;
import com.example.demo.systemsetting.SystemSettingKey;
import com.example.demo.systemsetting.SystemSettingService;
import com.example.demo.users.User;
import com.example.demo.users.UsersRepository;

class PurchaseOrderReceivingServiceTest {

    private final PurchaseOrdersRepository purchaseOrders = mock(PurchaseOrdersRepository.class);
    private final UsersRepository users = mock(UsersRepository.class);
    private final InventoryService inventory = mock(InventoryService.class);
    private final SystemSettingService settings = mock(SystemSettingService.class);
    private final PurchaseOrderReceivingService service = new PurchaseOrderReceivingService(
            purchaseOrders, users, inventory, settings);

    @Test
    void receivingCreatesStockForEveryItemAndCompletesOrder() {
        PurchaseOrders order = order(PurchaseOrdersStatus.APPROVED);
        User receiver = mock(User.class);
        when(receiver.getId()).thenReturn(8L);
        when(receiver.getName()).thenReturn("收貨人");
        when(settings.isEnabled(SystemSettingKey.PURCHASE_ORDER_RECEIVING_ENABLED)).thenReturn(true);
        when(purchaseOrders.findByIdForReceiving(10L)).thenReturn(Optional.of(order));
        when(users.findById(8L)).thenReturn(Optional.of(receiver));
        when(purchaseOrders.save(any(PurchaseOrders.class))).thenAnswer(call -> call.getArgument(0));

        PurchaseOrderResponseDTO result = service.receive(
                10L, new PurchaseOrderReceiveRequestDTO(), 8L);

        assertEquals(PurchaseOrdersStatus.RECEIVED, result.getStatus());
        assertEquals(8L, result.getReceivedByUserId());
        verify(inventory).receive(order.getItems().get(0).getMaterial(), BigDecimal.valueOf(3), null, 10L);
        verify(inventory).receive(order.getItems().get(1).getMaterial(), BigDecimal.valueOf(5), null, 10L);
    }

    @Test
    void alreadyReceivedOrderCannotCreateStockAgain() {
        PurchaseOrders order = order(PurchaseOrdersStatus.RECEIVED);
        when(settings.isEnabled(SystemSettingKey.PURCHASE_ORDER_RECEIVING_ENABLED)).thenReturn(true);
        when(purchaseOrders.findByIdForReceiving(10L)).thenReturn(Optional.of(order));

        assertThrows(IllegalStateException.class,
                () -> service.receive(10L, new PurchaseOrderReceiveRequestDTO(), 8L));

        verify(inventory, never()).receive(any(), any(), any(), any());
    }

    @Test
    void disabledFeatureDoesNotReadOrModifyPurchaseOrder() {
        when(settings.isEnabled(SystemSettingKey.PURCHASE_ORDER_RECEIVING_ENABLED)).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> service.receive(10L, new PurchaseOrderReceiveRequestDTO(), 8L));

        verify(purchaseOrders, never()).findByIdForReceiving(any());
        verify(inventory, never()).receive(any(), any(), any(), any());
    }

    @Test
    void receivableQueryWithoutDateReturnsAllEligibleOrdersInRepositoryOrder() {
        PurchaseOrders first = order(PurchaseOrdersStatus.APPROVED);
        first.setOrderNumber("PO-EARLIER");
        PurchaseOrders second = order(PurchaseOrdersStatus.ORDERED);
        second.setId(11L);
        second.setOrderNumber("PO-LATER");
        when(settings.isEnabled(SystemSettingKey.PURCHASE_ORDER_RECEIVING_ENABLED)).thenReturn(true);
        when(purchaseOrders.findReceivableOrderByExpectedDeliveryDate(any()))
                .thenReturn(List.of(first, second));

        List<ReceivablePurchaseOrderDTO> result = service.findReceivable(null);

        assertEquals(List.of("PO-EARLIER", "PO-LATER"),
                result.stream().map(ReceivablePurchaseOrderDTO::getOrderNumber).toList());
        verify(purchaseOrders).findReceivableOrderByExpectedDeliveryDate(any());
    }

    private PurchaseOrders order(PurchaseOrdersStatus status) {
        PurchaseOrders order = new PurchaseOrders();
        order.setId(10L);
        order.setOrderNumber("PO-10");
        order.setStatus(status);
        order.setSupplier(supplier());
        order.setCreatedBy(user("申請人"));
        order.setApprovedBy(user("簽核人"));
        order.addItem(item(101L, 201L, BigDecimal.valueOf(3)));
        order.addItem(item(102L, 202L, BigDecimal.valueOf(5)));
        return order;
    }

    private com.example.demo.suppliers.Suppliers supplier() {
        com.example.demo.suppliers.Suppliers supplier = new com.example.demo.suppliers.Suppliers();
        supplier.setId(3L);
        supplier.setName("供應商");
        return supplier;
    }

    private User user(String name) {
        User user = mock(User.class);
        when(user.getName()).thenReturn(name);
        return user;
    }

    private PurchaseOrderItems item(Long itemId, Long materialId, BigDecimal quantity) {
        Material material = new Material();
        material.setId(materialId);
        material.setName("原物料 " + materialId);
        material.setCode("M-" + materialId);
        material.setUnit("個");
        material.setCostMode("DIRECT");

        PurchaseOrderItems item = new PurchaseOrderItems();
        item.setId(itemId);
        item.setMaterial(material);
        item.setQuantity(quantity);
        item.setUnitPrice(BigDecimal.ONE);
        return item;
    }
}
