package com.example.demo.materials;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.demo.inventories.InventoryRepository;

class MaterialsServicePurchasingDataTest {

    private final InventoryRepository inventoryRepository = mock(InventoryRepository.class);
    private final MaterialRepository materialRepository = mock(MaterialRepository.class);
    private final MaterialsService service = new MaterialsService(inventoryRepository, materialRepository);

    @Test
    void oldCreateRequestGetsSafePurchasingDefaults() {
        Material request = conversionMaterial();
        when(materialRepository.save(any(Material.class))).thenAnswer(call -> call.getArgument(0));

        Material result = service.create(request);

        assertEquals(7, result.getLeadTimeDays());
        assertEquals(0, new BigDecimal("2500").compareTo(result.getPurchasePackQuantity()));
    }

    @Test
    void oldUpdateRequestPreservesExistingPurchasingData() {
        Material existing = conversionMaterial();
        existing.setId(1L);
        existing.setLeadTimeDays(5);
        existing.setPurchasePackQuantity(new BigDecimal("2500"));
        Material request = conversionMaterial();
        when(materialRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(materialRepository.save(any(Material.class))).thenAnswer(call -> call.getArgument(0));

        Material result = service.update(1L, request);

        assertEquals(5, result.getLeadTimeDays());
        assertEquals(0, new BigDecimal("2500").compareTo(result.getPurchasePackQuantity()));
    }

    @Test
    void invalidLeadTimeIsRejected() {
        Material request = conversionMaterial();
        request.setLeadTimeDays(0);

        assertThrows(IllegalArgumentException.class, () -> service.create(request));
    }

    private Material conversionMaterial() {
        Material material = new Material();
        material.setCode("MAT999");
        material.setName("測試原料");
        material.setUnit("g");
        material.setCostMode("CONVERSION");
        material.setPurchaseUnit("包");
        material.setConversionQuantity(new BigDecimal("2500"));
        material.setPurchaseCost(new BigDecimal("500"));
        material.setSafetyStock(new BigDecimal("1000"));
        return material;
    }
}
