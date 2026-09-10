package com.example.demo.products;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService pdService;


    // =========================
    // 新增產品
    // =========================
    @PostMapping("/api/product/add")
    public ResponseEntity<?> create(
            @RequestBody ProductRequestDTO dto) {

        Products product =
                pdService.create(dto);

        return new ResponseEntity<>(
                product,
                HttpStatus.CREATED
        );
    }


    // =========================
    // 查詢全部產品
    // =========================
    @GetMapping("/api/product/list")
    public ResponseEntity<?> findAll() {

        List<ProductResponseDTO> list =
                pdService.findAll();

        return new ResponseEntity<>(
                list,
                HttpStatus.OK
        );
    }


    // =========================
    // 查詢單一產品
    // =========================
    @GetMapping("/api/product/{id}")
    public ResponseEntity<?> findById(
            @PathVariable Long id) {

        ProductResponseDTO product =
                pdService.findById(id);

        return new ResponseEntity<>(
                product,
                HttpStatus.OK
        );
    }


    // =========================
    // 修改產品
    // =========================
    @PutMapping("/api/productupdate/{id}")
    public ResponseEntity<?> update(
            @PathVariable Long id,
            @RequestBody ProductRequestDTO dto) {

        ProductResponseDTO product =
                pdService.update(id, dto);

        return new ResponseEntity<>(
                product,
                HttpStatus.OK
        );
    }


    // =========================
    // 刪除產品
    // =========================
    @DeleteMapping("/api/productdelete/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id) {

        pdService.delete(id);

        return new ResponseEntity<>(
                HttpStatus.NO_CONTENT
        );
    }
    
    
    
}