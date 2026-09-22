package com.example.demo.products;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.example.demo.productcategory.ProductCategory;
import com.example.demo.productcategory.ProductCategoryRepository;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;

class ProductServiceTypeTest {

    private final ProductRepository products = mock(ProductRepository.class);
    private final ProductCategoryRepository categories = mock(ProductCategoryRepository.class);
    private final MaterialRepository materials = mock(MaterialRepository.class);
    private final BomRepository boms = mock(BomRepository.class);
    private final ProductService service = new ProductService(products, categories, materials, boms);

    @Test
    void oldCreateRequestDefaultsToRecipe() {
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setActive(true);
        when(categories.findById(1L)).thenReturn(Optional.of(category));
        when(products.save(any(Products.class))).thenAnswer(call -> call.getArgument(0));

        ProductRequestDTO request = request();
        assertEquals(ProductType.RECIPE, service.create(request).getProductType());
    }

    @Test
    void oldUpdateRequestPreservesRetailType() {
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setName("零售");
        category.setActive(true);
        Products existing = new Products();
        existing.setId(2L);
        existing.setCategory(category);
        existing.setProductType(ProductType.RETAIL);
        when(products.findById(2L)).thenReturn(Optional.of(existing));
        when(categories.findById(1L)).thenReturn(Optional.of(category));
        when(products.save(any(Products.class))).thenAnswer(call -> call.getArgument(0));

        assertEquals(ProductType.RETAIL, service.update(2L, request()).getProductType());
    }

    @Test
    void retailCreateLinksOneMaterialAndOneToOneBom() {
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setActive(true);
        when(categories.findById(1L)).thenReturn(Optional.of(category));
        when(products.save(any(Products.class))).thenAnswer(call -> call.getArgument(0));
        when(materials.save(any(Material.class))).thenAnswer(call -> call.getArgument(0));

        ProductRequestDTO request = request();
        request.setProductType(ProductType.RETAIL);
        request.setRetailCost(BigDecimal.valueOf(6));
        Products product = service.create(request);

        ArgumentCaptor<Material> materialCaptor = ArgumentCaptor.forClass(Material.class);
        ArgumentCaptor<Bom> bomCaptor = ArgumentCaptor.forClass(Bom.class);
        verify(materials).save(materialCaptor.capture());
        verify(boms).save(bomCaptor.capture());
        Material material = materialCaptor.getValue();
        Bom bom = bomCaptor.getValue();
        assertEquals(ProductType.RETAIL, product.getProductType());
        assertEquals("DIRECT", material.getCostMode());
        assertEquals(BigDecimal.valueOf(6), material.getCost());
        assertSame(product, bom.getProduct());
        assertSame(material, bom.getMaterial());
        assertEquals(BigDecimal.ONE, bom.getQuantity());
    }

    @Test
    void regularUpdateCannotConvertRecipeToRetail() {
        ProductCategory category = new ProductCategory();
        category.setId(1L);
        category.setActive(true);
        Products existing = new Products();
        existing.setCategory(category);
        when(products.findById(2L)).thenReturn(Optional.of(existing));
        when(categories.findById(1L)).thenReturn(Optional.of(category));
        ProductRequestDTO request = request();
        request.setProductType(ProductType.RETAIL);

        assertThrows(IllegalArgumentException.class, () -> service.update(2L, request));
    }

    private ProductRequestDTO request() {
        ProductRequestDTO request = new ProductRequestDTO();
        request.setSku("SKU-1");
        request.setName("測試商品");
        request.setCategoryId(1L);
        request.setSellingPrice(BigDecimal.TEN);
        request.setUnit("件");
        request.setStatus("ACTIVE");
        return request;
    }
}
