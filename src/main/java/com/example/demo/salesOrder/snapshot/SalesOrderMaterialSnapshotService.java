package com.example.demo.salesOrder.snapshot;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.bom.Bom;
import com.example.demo.bom.BomRepository;
import com.example.demo.bom.version.BomVersion;
import com.example.demo.bom.version.BomVersionRepository;
import com.example.demo.materials.Material;
import com.example.demo.products.ProductType;
import com.example.demo.products.Products;
import com.example.demo.salesOrder.SalesOrderItem;
import com.example.demo.salesOrder.SalesOrders;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SalesOrderMaterialSnapshotService {
    private final BomRepository bomRepository;
    private final SalesOrderMaterialUsageRepository usageRepository;
    private final BomVersionRepository bomVersionRepository;

    public void validateRecipes(List<SalesOrderItem> items) {
        for (SalesOrderItem orderItem : items) {
            Products product = orderItem.getProduct();
            if (product.getProductType() == ProductType.RETAIL) continue;

            List<Bom> bom = bomRepository.findByProductId(product.getId());
            if (bom.isEmpty()) {
                throw new IllegalStateException("商品「" + product.getName() + "」沒有 BOM，無法完成交易");
            }
            for (Bom component : bom) {
                if (!"ACTIVE".equalsIgnoreCase(component.getMaterial().getStatus())) {
                    throw new IllegalStateException("商品「" + product.getName() + "」的 BOM 含停用原物料「"
                            + component.getMaterial().getName() + "」，無法完成交易");
                }
            }
        }
    }

    public void createSnapshot(SalesOrders order, List<SalesOrderItem> items) {
        if (usageRepository.existsBySalesOrderId(order.getId())) return;

        for (SalesOrderItem orderItem : items) {
            Products product = orderItem.getProduct();
            if (product.getProductType() == ProductType.RETAIL) continue;

            BomVersion version = bomVersionRepository
                    .findTopByProductIdOrderByVersionNumberDesc(product.getId())
                    .orElse(null);

            for (Bom bom : bomRepository.findByProductId(product.getId())) {
                Material material = bom.getMaterial();
                SalesOrderMaterialUsage usage = new SalesOrderMaterialUsage();
                usage.setSalesOrder(order);
                usage.setSalesOrderItem(orderItem);
                usage.setProductId(product.getId());
                usage.setProductSku(product.getSku());
                if (version != null) {
                    usage.setBomVersionId(version.getId());
                    usage.setBomVersionNumber(version.getVersionNumber());
                }
                usage.setMaterialId(material.getId());
                usage.setMaterialCode(material.getCode());
                usage.setMaterialName(material.getName());
                usage.setMaterialUnit(material.getUnit());
                usage.setProductQuantity(orderItem.getQuantity());
                usage.setBomUnitQuantity(bom.getQuantity());
                usage.setTheoreticalQuantity(orderItem.getQuantity().multiply(bom.getQuantity()));
                usageRepository.save(usage);
            }
        }
    }
}
