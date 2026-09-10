package com.example.demo.productcategory;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-categories")
public class ProductCategoryController {

    private final ProductCategoryService categoryService;


    // =========================
    // 新增商品分類
    // =========================
    @PostMapping
    public ResponseEntity<ProductCategory> create(
            @RequestBody ProductCategory category) {

        ProductCategory created =
                categoryService.create(category);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(created);
    }


    // =========================
    // 查詢全部分類
    // 給分類管理頁使用
    // =========================
    @GetMapping
    public ResponseEntity<List<ProductCategory>> findAll() {

        return ResponseEntity.ok(
                categoryService.findAll()
        );
    }


    // =========================
    // 查詢啟用中的分類
    // 給新增 / 修改商品的下拉選單使用
    // =========================
    @GetMapping("/active")
    public ResponseEntity<List<ProductCategory>> findActive() {

        return ResponseEntity.ok(
                categoryService.findActive()
        );
    }


    // =========================
    // 修改分類名稱
    // =========================
    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> update(
            @PathVariable Long id,
            @RequestBody ProductCategory category) {

        ProductCategory updated =
                categoryService.update(
                        id,
                        category
                );

        return ResponseEntity.ok(updated);
    }


    // =========================
    // 啟用 / 停用分類
    // =========================
    @PatchMapping("/{id}/active")
    public ResponseEntity<ProductCategory> updateActive(
            @PathVariable Long id,
            @RequestBody Boolean active) {

        ProductCategory updated =
                categoryService.updateActive(
                        id,
                        active
                );

        return ResponseEntity.ok(updated);
    }
    
    
    
}

