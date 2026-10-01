package com.example.demo.materialsettlement;

import java.time.LocalDate;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.materialsettlement.dto.SaveSettlementRequest;
import com.example.demo.materialsettlement.dto.SettlementPreviewResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/material-settlements")
@RequiredArgsConstructor
public class DailyMaterialSettlementController {

    private final DailyMaterialSettlementService settlementService;

    @GetMapping("/preview")
    public ResponseEntity<SettlementPreviewResponse> preview(
            @RequestParam(required = false) LocalDate date) {
        return ResponseEntity.ok(settlementService.preview(date));
    }

    @PutMapping("/{date}")
    public ResponseEntity<SettlementPreviewResponse> saveDraft(
            @PathVariable LocalDate date,
            @RequestBody SaveSettlementRequest request,
            HttpServletRequest servletRequest) {
        return ResponseEntity.ok(settlementService.saveDraft(
                date, request, requireSessionUserId(servletRequest)));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<SettlementPreviewResponse> complete(
            @PathVariable Long id,
            HttpServletRequest servletRequest) {
        requireSessionUserId(servletRequest);
        return ResponseEntity.ok(settlementService.complete(id));
    }

    private Long requireSessionUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object value = session == null ? null : session.getAttribute("userId");
        if (value instanceof Number number) return number.longValue();
        throw new IllegalStateException("尚未登入，無法執行當日領料結算");
    }
}
