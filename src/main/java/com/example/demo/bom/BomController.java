package com.example.demo.bom;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BomController {

    private final BomService bomService;


    // 新增一筆 BOM 配方
    @PostMapping("/api/bom")
    public ResponseEntity<Bom> create(
            @RequestBody BomRequestDTO dto) {

        Bom bom = bomService.create(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bom);
    }


    // 查詢某商品的所有 BOM 配方
    @GetMapping("/api/bom/product/{productId}")
    public ResponseEntity<List<BomResponseDTO>> findByProductId(
            @PathVariable Long productId) {

        List<BomResponseDTO> list =
                bomService.findByProductId(productId);

        return ResponseEntity.ok(list);
    }


    // 修改某一筆 BOM 用量
    @PutMapping("/api/bom/{id}")
    public ResponseEntity<Bom> update(
            @PathVariable Long id,
            @RequestBody Bom bom) {

        Bom updated =
                bomService.update(id, bom);

        return ResponseEntity.ok(updated);
    }


    // 刪除某一筆 BOM
    @DeleteMapping("/api/bom/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id) {

        bomService.delete(id);

        return ResponseEntity
                .noContent()
                .build();
    }
 // 儲存某商品的整份 BOM 配方
    @PutMapping("/api/bom/product/{productId}")
    public ResponseEntity<List<Bom>> saveFullBom(
            @PathVariable Long productId,
            @RequestBody BomSaveRequestDTO dto) {

        dto.setProductId(productId);

        List<Bom> list =
                bomService.saveFullBom(dto);

        return ResponseEntity.ok(list);
    }
}