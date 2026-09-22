package com.example.demo.products;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;
import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.productcategory.ProductCategory;
import com.example.demo.productcategory.ProductCategoryRepository;

import lombok.RequiredArgsConstructor;

	@RequiredArgsConstructor
@Service
public class ProductService {

		private final ProductRepository productRepo;

		private final ProductCategoryRepository categoryRepo;
		private final MaterialRepository materialRepo;
		private final BomRepository bomRepo;
        @Transactional
        public Products create(ProductRequestDTO dto) {
            ProductCategory category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "找不到商品分類 id=" + dto.getCategoryId()));
            if (!Boolean.TRUE.equals(category.getActive())) {
                throw new IllegalArgumentException("此商品分類已停用");
            }

            ProductType type = dto.getProductType() == null
                    ? ProductType.RECIPE : dto.getProductType();
            if (type == ProductType.RETAIL) {
                return createRetailProduct(dto, category);
            }
            return createRecipeProduct(dto, category);
        }

        private Products createRecipeProduct(ProductRequestDTO dto, ProductCategory category) {
            Products product = newProduct(dto, category, ProductType.RECIPE);
            product.setCostPrice(BigDecimal.ZERO);
            return productRepo.save(product);
        }

        private Products createRetailProduct(ProductRequestDTO dto, ProductCategory category) {
            if (dto.getSku() == null || dto.getSku().isBlank()
                    || dto.getName() == null || dto.getName().isBlank()
                    || dto.getUnit() == null || dto.getUnit().isBlank()) {
                throw new IllegalArgumentException("零售商品編號、名稱與單位不得為空");
            }
            if (dto.getSku().length() > 50) {
                throw new IllegalArgumentException("零售商品編號不得超過 50 字");
            }
            if (dto.getRetailCost() == null
                    || dto.getRetailCost().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("零售商品成本不得為空或小於 0");
            }
            if (dto.getStatus() == null
                    || (!"ACTIVE".equals(dto.getStatus()) && !"INACTIVE".equals(dto.getStatus()))) {
                throw new IllegalArgumentException("商品狀態只能是 ACTIVE 或 INACTIVE");
            }
            if (materialRepo.findByCode(dto.getSku()).isPresent()) {
                throw new IllegalArgumentException("原物料編號已存在：" + dto.getSku());
            }

            Material material = new Material();
            material.setCode(dto.getSku());
            material.setName(dto.getName());
            material.setUnit(dto.getUnit());
            material.setCostMode("DIRECT");
            material.setCost(dto.getRetailCost());
            material.setSafetyStock(BigDecimal.ZERO);
            material.setStatus(dto.getStatus());
            material = materialRepo.save(material);

            Products product = newProduct(dto, category, ProductType.RETAIL);
            product.setCostPrice(dto.getRetailCost());
            product = productRepo.save(product);

            Bom bom = new Bom();
            bom.setProduct(product);
            bom.setMaterial(material);
            bom.setQuantity(BigDecimal.ONE);
            bomRepo.save(bom);
            return product;
        }

        private Products newProduct(ProductRequestDTO dto, ProductCategory category, ProductType type) {
            Products product = new Products();
            product.setSku(dto.getSku());
            product.setName(dto.getName());
            product.setCategory(category);
            product.setSellingPrice(dto.getSellingPrice());
            product.setUnit(dto.getUnit());
            product.setStatus(dto.getStatus());
            product.setProductType(type);
            product.setImageUrl(dto.getImageUrl());
            return product;
        }
		public ProductResponseDTO findById(Long id) {

		    Products product =
		            productRepo
		                    .findById(id)
		                    .orElseThrow(() ->
		                            new IllegalArgumentException(
		                                    "找不到商品 id=" + id
		                            )
		                    );

		    return convertToDTO(product);
		}
		public List<ProductResponseDTO> findAll() {

		    return productRepo
		            .findAll()
		            .stream()
		            .map(this::convertToDTO)
		            .toList();
		}


		public ProductResponseDTO update(
		        Long id,
		        ProductRequestDTO dto) {

		    Products product =
		            productRepo.findById(id)
		                    .orElseThrow(() ->
		                            new IllegalArgumentException(
		                                    "找不到商品 id=" + id
		                            )
		                    );

		    ProductCategory category =
		            categoryRepo.findById(dto.getCategoryId())
		                    .orElseThrow(() ->
		                            new IllegalArgumentException(
		                                    "找不到商品分類 id=" + dto.getCategoryId()
		                            )
		                    );

		    if (!category.getActive()) {
		        throw new IllegalArgumentException(
		                "此商品分類已停用"
		        );
		    }
		    ProductType currentType = product.getProductType() == null
		            ? ProductType.RECIPE : product.getProductType();
		    if (dto.getProductType() != null && dto.getProductType() != currentType) {
		        throw new IllegalArgumentException("不能透過一般商品編輯變更商品類型");
		    }

		    product.setSku(dto.getSku());
		    product.setName(dto.getName());
		    product.setCategory(category);
		    product.setSellingPrice(dto.getSellingPrice());
		    product.setUnit(dto.getUnit());
		    product.setStatus(dto.getStatus());
		    // 舊資料尚未回填類型時，第一次編輯補為配方商品。
		    product.setProductType(currentType);
		    product.setImageUrl(dto.getImageUrl());
		    Products saved =
		            productRepo.save(product);

		    return convertToDTO(saved);
		}
private ProductResponseDTO convertToDTO(Products product) {

    ProductResponseDTO dto = new ProductResponseDTO();

    dto.setId(product.getId());
    dto.setSku(product.getSku());
    dto.setName(product.getName());

    dto.setCategoryId(
            product.getCategory().getId()
    );

    dto.setCategoryName(
            product.getCategory().getName()
    );

    dto.setSellingPrice(
            product.getSellingPrice()
    );

    dto.setCostPrice(
            product.getCostPrice()
    );

    dto.setUnit(product.getUnit());
    dto.setStatus(product.getStatus());
    dto.setProductType(product.getProductType() == null
            ? ProductType.RECIPE : product.getProductType());
    dto.setImageUrl(product.getImageUrl());
    return dto;
}
public ProductResponseDTO updateStatus(
        Long id,
        String status) {

    Products product = productRepo.findById(id)
            .orElseThrow(() ->
                    new IllegalArgumentException(
                            "找不到商品 id=" + id
                    )
            );

    if (!"ACTIVE".equals(status)
            && !"INACTIVE".equals(status)) {
        throw new IllegalArgumentException(
                "商品狀態只能是 ACTIVE 或 INACTIVE"
        );
    }

    product.setStatus(status);

    Products saved = productRepo.save(product);

    return convertToDTO(saved);
}

public ProductResponseDTO updateImage(Long id, String imageUrl) {
    if (imageUrl == null || !imageUrl.startsWith("/uploads/products/")) {
        throw new IllegalArgumentException("商品圖片路徑無效");
    }

    Products product = productRepo.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("找不到商品 id=" + id));

    product.setImageUrl(imageUrl);
    return convertToDTO(productRepo.save(product));
}

public void delete(Long id) {
	
	 productRepo.deleteById(id);
	
}





		
}
