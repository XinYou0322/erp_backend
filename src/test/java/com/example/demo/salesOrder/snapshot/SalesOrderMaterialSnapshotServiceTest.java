package com.example.demo.salesOrder.snapshot;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;
import com.example.demo.bom.version.BomVersionRepository;
import com.example.demo.materials.Material;
import com.example.demo.products.ProductType;
import com.example.demo.products.Products;
import com.example.demo.salesOrder.SalesOrderItem;
import com.example.demo.salesOrder.SalesOrders;

class SalesOrderMaterialSnapshotServiceTest {
    private final BomRepository bomRepository = mock(BomRepository.class);
    private final SalesOrderMaterialUsageRepository usageRepository = mock(SalesOrderMaterialUsageRepository.class);
    private final BomVersionRepository versionRepository = mock(BomVersionRepository.class);
    private final SalesOrderMaterialSnapshotService service =
            new SalesOrderMaterialSnapshotService(bomRepository, usageRepository, versionRepository);

    @Test
    void recipeWithoutBomCannotCompleteSale() {
        SalesOrderItem item = orderItem(recipeProduct(), "2");
        when(bomRepository.findByProductId(10L)).thenReturn(List.of());

        assertThrows(IllegalStateException.class, () -> service.validateRecipes(List.of(item)));
    }

    @Test
    void snapshotKeepsTheoreticalUsageAtCheckout() {
        Products product = recipeProduct();
        SalesOrderItem item = orderItem(product, "3");
        SalesOrders order = new SalesOrders();
        order.setId(99L);
        Material material = material("ACTIVE");
        Bom bom = new Bom();
        bom.setMaterial(material);
        bom.setProduct(product);
        bom.setQuantity(new BigDecimal("12.5"));
        when(bomRepository.findByProductId(10L)).thenReturn(List.of(bom));
        when(usageRepository.existsBySalesOrderId(99L)).thenReturn(false);

        service.validateRecipes(List.of(item));
        service.createSnapshot(order, List.of(item));

        ArgumentCaptor<SalesOrderMaterialUsage> captor = ArgumentCaptor.forClass(SalesOrderMaterialUsage.class);
        verify(usageRepository).save(captor.capture());
        assertEquals(0, new BigDecimal("37.5").compareTo(captor.getValue().getTheoreticalQuantity()));
        assertEquals("MAT001", captor.getValue().getMaterialCode());
    }

    @Test
    void retailProductDoesNotRequireBomOrCreateSnapshot() {
        Products product = recipeProduct();
        product.setProductType(ProductType.RETAIL);
        SalesOrderItem item = orderItem(product, "1");
        SalesOrders order = new SalesOrders();
        order.setId(100L);
        when(usageRepository.existsBySalesOrderId(100L)).thenReturn(false);

        service.validateRecipes(List.of(item));
        service.createSnapshot(order, List.of(item));

        verify(bomRepository, never()).findByProductId(any());
        verify(usageRepository, never()).save(any());
    }

    private Products recipeProduct() {
        Products product = new Products();
        product.setId(10L);
        product.setSku("P001");
        product.setName("紅茶");
        product.setProductType(ProductType.RECIPE);
        return product;
    }

    private SalesOrderItem orderItem(Products product, String quantity) {
        SalesOrderItem item = new SalesOrderItem();
        item.setId(20L);
        item.setProduct(product);
        item.setQuantity(new BigDecimal(quantity));
        return item;
    }

    private Material material(String status) {
        Material material = new Material();
        material.setId(30L);
        material.setCode("MAT001");
        material.setName("紅茶葉");
        material.setUnit("g");
        material.setStatus(status);
        return material;
    }
}
