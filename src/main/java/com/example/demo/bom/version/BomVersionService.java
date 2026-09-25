package com.example.demo.bom.version;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;
import com.example.demo.products.Products;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BomVersionService {
    private final BomVersionRepository versionRepository;
    private final BomRepository bomRepository;

    public BomVersion recordCurrentVersion(Products product, String changeType) {
        BomVersion version = new BomVersion();
        version.setProduct(product);
        version.setVersionNumber(versionRepository.findLatestVersionNumber(product.getId()) + 1);
        version.setChangeType(changeType);

        for (Bom bom : bomRepository.findByProductId(product.getId())) {
            BomVersionItem item = new BomVersionItem();
            item.setMaterialId(bom.getMaterial().getId());
            item.setMaterialCode(bom.getMaterial().getCode());
            item.setMaterialName(bom.getMaterial().getName());
            item.setMaterialUnit(bom.getMaterial().getUnit());
            item.setQuantity(bom.getQuantity());
            version.addItem(item);
        }
        return versionRepository.save(version);
    }

    public List<BomVersion> findByProductId(Long productId) {
        return versionRepository.findByProductIdOrderByVersionNumberDesc(productId);
    }
}
