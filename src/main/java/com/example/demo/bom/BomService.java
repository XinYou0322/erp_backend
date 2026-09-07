package com.example.demo.bom;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.materials.Material;
import com.example.demo.materials.MaterialRepository;
import com.example.demo.products.ProductRepository;
import com.example.demo.products.Products;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BomService {

	private final BomRepository bomRepository;

	private final ProductRepository productRepository;

	private final MaterialRepository materialRepository;

	public Bom create(BomRequestDTO dto) {

	    Products product =
	            productRepository
	                    .findById(dto.getProductId())
	                    .orElseThrow(() ->
	                            new EntityNotFoundException(
	                                    "找不到商品 id=" + dto.getProductId()
	                            )
	                    );

	    Material material =
	            materialRepository
	                    .findById(dto.getMaterialId())
	                    .orElseThrow(() ->
	                            new EntityNotFoundException(
	                                    "找不到原物料 id=" + dto.getMaterialId()
	                            )
	                    );

	    Bom bom = new Bom();

	    bom.setProduct(product);
	    bom.setMaterial(material);
	    bom.setQuantity(dto.getQuantity());

	    Bom saved =
	            bomRepository.save(bom);

	    recalculateProductCost(
	            product.getId()
	    );

	    return saved;
	}
	
	
	private void recalculateProductCost(Long productId) {

	    List<Bom> bomList =
	            bomRepository.findByProductId(productId);

	    BigDecimal totalCost =
	            BigDecimal.ZERO;

	    for (Bom bom : bomList) {

	        BigDecimal materialCost =
	                bom.getMaterial().getCost();

	        BigDecimal quantity =
	                bom.getQuantity();

	        BigDecimal itemCost =
	                materialCost.multiply(quantity);

	        totalCost =
	                totalCost.add(itemCost);
	    }

	    Products product =
	            productRepository
	                    .findById(productId)
	                    .orElseThrow();

	    product.setCostPrice(totalCost);

	    productRepository.save(product);
	}
	
	
	
	
	
	
    // 查詢某商品底下所有配方
	public List<BomResponseDTO> findByProductId(Long productId) {

	    List<Bom> bomList =
	            bomRepository.findByProductId(productId);

	    return bomList.stream()
	            .map(bom -> new BomResponseDTO(
	                    bom.getId(),
	                    bom.getMaterial().getId(),
	                    bom.getMaterial().getName(),
	                    bom.getMaterial().getCode(),
	                    bom.getQuantity(),
	                    bom.getMaterial().getUnit(),
	                    bom.getMaterial().getCost()
	            ))
	            .toList();
	}

    // 修改一筆配方（通常只會改 quantity）
    public Bom update(Long id, Bom bom) {
        Bom exist = bomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 id=" + id + " 的配方"));

        exist.setQuantity(bom.getQuantity());
        Bom saved =
                bomRepository.save(exist);

        recalculateProductCost(
                saved.getProduct().getId()
        );

        return saved;
    }

    // 刪除一筆配方
    public void delete(Long id) {

        Bom bom =
                bomRepository
                        .findById(id)
                        .orElseThrow();

        Long productId =
                bom.getProduct().getId();

        bomRepository.delete(bom);

        recalculateProductCost(productId);
    }
}