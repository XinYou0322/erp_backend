package com.example.demo.products;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;
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
    
    @PatchMapping("/api/product/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        ProductResponseDTO product =
                pdService.updateStatus(id, status);

        return ResponseEntity.ok(product);
    }

    @PatchMapping("/api/product/{id}/image")
    public ResponseEntity<ProductResponseDTO> updateImage(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {

        ProductResponseDTO product =
                pdService.updateImage(id, body.get("imageUrl"));

        return ResponseEntity.ok(product);
    }

    @PostMapping("/api/product/upload-image")
    public ResponseEntity<?> uploadProductImage(
            @RequestParam("file") MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "請選擇圖片檔案"));
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !contentType.startsWith("image/")) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("message", "只能上傳圖片檔案"));
        }

        try {
            String uploadDir =
                    System.getProperty("user.dir")
                    + "/uploads/products";

            Path directory = Paths.get(uploadDir);
            Files.createDirectories(directory);

            String originalName = file.getOriginalFilename();
            String extension = "";

            if (originalName != null &&
                    originalName.contains(".")) {
                extension = originalName.substring(
                        originalName.lastIndexOf(".")
                );
            }

            String fileName =
                    UUID.randomUUID() + extension;

            Path target = directory.resolve(fileName);
            Files.write(target, file.getBytes());

            String imageUrl =
                    "/uploads/products/" + fileName;

            return ResponseEntity.ok(
                    Map.of(
                            "message", "圖片上傳成功",
                            "imageUrl", imageUrl
                    )
            );
        } catch (IOException error) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "圖片儲存失敗"));
        }
    }
    
    
}
