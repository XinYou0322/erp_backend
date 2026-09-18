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

    /**
     * 處理打卡/簽退的 API
     * 請求路徑: POST /api/clock/toggle
     */
    @PostMapping("/toggle")
    public ResponseEntity<?> toggleClock(@RequestBody Map<String, String> request) {
        // 1. 從前端傳過來的 JSON 取得員工 ID 與打卡類型 (前端按鈕當下的狀態)
        String userId = request.get("userId");
        String currentStatus = request.get("currentStatus"); // 例如 'IN' 或 'OUT'

        if (userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("員工編號不能為空");
        }

        // 2. 判斷這次要寫入的打卡類型
        // 如果前端傳過來目前是 IN(已上班)，表示這次點擊是要 OUT(簽退)，反之亦然
        String nextType = "IN".equals(currentStatus) ? "CLOCK_OUT" : "CLOCK_IN";

        // 3. 核心安全機制：打卡時間「必須」由後端伺服器生成，不能信任前端時間
        LocalDateTime serverTime = LocalDateTime.now();

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
