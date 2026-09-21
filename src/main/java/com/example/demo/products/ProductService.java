package com.example.demo.products;


import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.productcategory.ProductCategory;
import com.example.demo.productcategory.ProductCategoryRepository;

import lombok.RequiredArgsConstructor;

	@RequiredArgsConstructor
@Service
public class ProductService {

		private final ProductRepository productRepo;

		private final ProductCategoryRepository categoryRepo;
		public Products create(ProductRequestDTO dto) {

		    // 先找使用者選擇的分類
		    ProductCategory category =
		            categoryRepo.findById(dto.getCategoryId())
		                    .orElseThrow(() ->
		                            new IllegalArgumentException(
		                                    "找不到商品分類 id=" + dto.getCategoryId()
		                            )
		                            
		                            
		                         
		                            
		                            
		                    );
	
		    // 不允許使用已停用分類
		    if (!category.getActive()) {

		        throw new IllegalArgumentException(
		                "此商品分類已停用"
		        );
		    }


		    Products product = new Products();

		    product.setSku(dto.getSku());
		    product.setName(dto.getName());

		    // 重點：Entity 放進 Product
		    product.setCategory(category);

		    product.setSellingPrice(dto.getSellingPrice());

		    // 成本之後由 BOM 計算
		    product.setCostPrice(BigDecimal.ZERO);

		    product.setUnit(dto.getUnit());
		    product.setStatus(dto.getStatus());
		    product.setImageUrl(dto.getImageUrl());

		    return productRepo.save(product);
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

		    product.setSku(dto.getSku());
		    product.setName(dto.getName());
		    product.setCategory(category);
		    product.setSellingPrice(dto.getSellingPrice());
		    product.setUnit(dto.getUnit());
		    product.setStatus(dto.getStatus());
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
