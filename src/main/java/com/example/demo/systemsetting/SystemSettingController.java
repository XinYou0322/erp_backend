package com.example.demo.systemsetting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system-settings")
@RequiredArgsConstructor
public class SystemSettingController {

    private static final long MAX_BRANDING_IMAGE_SIZE = 2L * 1024 * 1024;
    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/png", "image/jpeg", "image/webp", "image/x-icon", "image/vnd.microsoft.icon");

    private final SystemSettingService service;

    @GetMapping("/{key}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> get(@PathVariable String key) {
        try {
            return ResponseEntity.ok(service.get(key));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @PutMapping("/{key}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> update(
            @PathVariable String key,
            @Valid @RequestBody SystemSettingUpdateDTO dto,
            HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            Long userId = session == null ? null : (Long) session.getAttribute("userId");
            return ResponseEntity.ok(service.update(key, dto.getValue(), userId));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
        }
    }

    @PostMapping("/branding/logo")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadBrandingLogo(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "請選擇圖片檔案"));
        }
        if (file.getSize() > MAX_BRANDING_IMAGE_SIZE) {
            return ResponseEntity.badRequest().body(Map.of("message", "圖片大小不得超過 2 MB"));
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return ResponseEntity.badRequest().body(Map.of("message", "僅支援 PNG、JPG、WebP 或 ICO 圖片"));
        }

        String extension = switch (contentType.toLowerCase()) {
            case "image/jpeg" -> ".jpg";
            case "image/webp" -> ".webp";
            case "image/x-icon", "image/vnd.microsoft.icon" -> ".ico";
            default -> ".png";
        };

        try {
            Path directory = Paths.get(System.getProperty("user.dir"), "uploads", "branding");
            Files.createDirectories(directory);
            String fileName = UUID.randomUUID() + extension;
            Path target = directory.resolve(fileName).normalize();
            if (!target.startsWith(directory.normalize())) {
                return ResponseEntity.badRequest().body(Map.of("message", "圖片檔名不正確"));
            }
            file.transferTo(target);
            return ResponseEntity.ok(Map.of(
                    "message", "網站圖示上傳成功",
                    "imageUrl", "/uploads/branding/" + fileName));
        } catch (IOException exception) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "網站圖示儲存失敗"));
        }
    }
}
