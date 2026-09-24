package com.example.demo.systemsetting;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/system-settings")
@RequiredArgsConstructor
public class SystemSettingController {

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
}
