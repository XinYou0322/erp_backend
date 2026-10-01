package com.example.demo.clockRecord;

import com.example.demo.clockRecord.ClockRecord;
import com.example.demo.clockRecord.ClockRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*") // 允許 Vue 前端跨網域 (CORS) 呼叫
@RestController
@RequestMapping("/api/clock")
public class ClockController {

    @Autowired
    private ClockRecordRepository clockRecordRepository;

    // Codex 修改：使用登入身分查詢本人，不信任前端傳入的員工 ID。
    @Autowired
    private com.example.demo.users.UsersRepository usersRepository;

    private com.example.demo.users.User currentUser(java.security.Principal principal, boolean lock) {
        if (principal == null) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "請先登入");
        return (lock ? usersRepository.findForClockUpdate(principal.getName()) : usersRepository.findByUsername(principal.getName()))
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED, "找不到登入使用者"));
    }

    private List<ClockRecord> todayRecords(String userId, LocalDateTime now) {
        return clockRecordRepository.findByUserIdAndClockTimeGreaterThanEqualAndClockTimeLessThan(
                userId, now.toLocalDate().atStartOfDay(), now.toLocalDate().plusDays(1).atStartOfDay());
    }

    // Codex 修改：打卡前取得伺服器認定的「今天＋目前使用者」狀態。
    @GetMapping("/today")
    public ResponseEntity<?> today(java.security.Principal principal) {
        String id = currentUser(principal, false).getId().toString();
        var records = todayRecords(id, LocalDateTime.now(java.time.ZoneId.of("Asia/Taipei")));
        boolean in = records.stream().anyMatch(r -> "CLOCK_IN".equals(r.getClockType()));
        boolean out = records.stream().anyMatch(r -> "CLOCK_OUT".equals(r.getClockType()));
        return ResponseEntity.ok(Map.of("isClockedIn", in && !out, "completed", out));
    }

    /**
     * 處理打卡/簽退的 API
     * 請求路徑: POST /api/clock/toggle
     */
    @PostMapping("/toggle")
    @org.springframework.transaction.annotation.Transactional
    public ResponseEntity<?> toggleClock(@RequestBody Map<String, String> request, java.security.Principal principal) {
        // 1. 從前端傳過來的 JSON 取得員工 ID 與打卡類型 (前端按鈕當下的狀態)
        String userId = currentUser(principal, true).getId().toString();
        String currentStatus = request.get("currentStatus"); // 例如 'IN' 或 'OUT'

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("員工編號不能為空");
        }

        // 2. 判斷這次要寫入的打卡類型
        // 如果前端傳過來目前是 IN(已上班)，表示這次點擊是要 OUT(簽退)，反之亦然
        String nextType = "IN".equals(currentStatus) ? "CLOCK_OUT" : "CLOCK_IN";

        // 3. 核心安全機制：打卡時間「必須」由後端伺服器生成，不能信任前端時間
        // Codex 修改：台灣日期及資料庫紀錄為準，鎖內檢查再寫入。
        LocalDateTime serverTime = LocalDateTime.now(java.time.ZoneId.of("Asia/Taipei"));
        var records = todayRecords(userId, serverTime);
        boolean clockedIn = records.stream().anyMatch(r -> "CLOCK_IN".equals(r.getClockType()));
        boolean clockedOut = records.stream().anyMatch(r -> "CLOCK_OUT".equals(r.getClockType()));
        if (!"IN".equals(currentStatus) && !"OUT".equals(currentStatus)) {
            return ResponseEntity.badRequest().body("打卡狀態不正確");
        }
        if (clockedOut || ("CLOCK_IN".equals(nextType) && clockedIn)) {
            return ResponseEntity.status(409).body("今天已有此打卡紀錄，請勿重複打卡。");
        }
        if ("CLOCK_OUT".equals(nextType) && !clockedIn) {
            return ResponseEntity.status(409).body("今天尚未上班打卡，不能直接簽退。");
        }

        // 4. 建立紀錄並存入資料庫
        ClockRecord record = new ClockRecord(userId, serverTime, nextType);
        clockRecordRepository.save(record);

        // 5. 回傳結果給前端 Vue 更新 UI
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", nextType.equals("CLOCK_IN") ? "打卡上班成功！" : "簽退成功！",
                "clockTime", serverTime.toString(),
                "isClockedIn", nextType.equals("CLOCK_IN")));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getClockHistory(@RequestParam(required = false) String userId) {
        List<ClockRecord> records;

        if (userId != null && !userId.isBlank()) {
            records = clockRecordRepository.findByUserIdOrderByClockTimeDesc(userId);
        } else {
            records = clockRecordRepository.findAllByOrderByClockTimeDesc();
        }

        List<Map<String, Object>> payload = records.stream().map(record -> {
            Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", record.getId());
            item.put("userId", record.getUserId());
            item.put("clockType", record.getClockType());
            item.put("clockTime", record.getClockTime().toString());
            item.put("label", "CLOCK_IN".equals(record.getClockType()) ? "上班打卡" : "簽退記錄");
            return item;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "success", true,
                "records", payload,
                "count", payload.size()));
    }
}
