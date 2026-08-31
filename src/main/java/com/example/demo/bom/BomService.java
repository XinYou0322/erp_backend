package com.example.demo.bom;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BomService {

    public final BomRepository bomRepository;

    // 新增一筆配方
    public Bom create(Bom bom) {
        return bomRepository.save(bom);
    }

    // 查詢某商品底下所有配方
    public List<Bom> findByProductId(Long productId) {
        return bomRepository.findByProductId(productId);
    }

    // 修改一筆配方（通常只會改 quantity）
    public Bom update(Long id, Bom bom) {
        Bom exist = bomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 id=" + id + " 的配方"));

        exist.setQuantity(bom.getQuantity());

        return bomRepository.save(exist);
    }

    // 刪除一筆配方
    public void delete(Long id) {
        bomRepository.deleteById(id);
    }
}