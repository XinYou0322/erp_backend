package com.example.demo.systemsetting;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SystemSettingService {

    private final SystemSettingRepository repository;

    @Transactional(readOnly = true)
    public SystemSettingResponseDTO get(String keyText) {
        SystemSettingKey key = parseKey(keyText);
        return repository.findById(key.name())
                .map(this::toResponse)
                .orElseGet(() -> new SystemSettingResponseDTO(
                        key.name(), key.getDefaultValue(), key.getDescription(), null, null));
    }

    @Transactional(readOnly = true)
    public boolean isEnabled(SystemSettingKey key) {
        String value = repository.findById(key.name())
                .map(SystemSetting::getValue)
                .orElse(key.getDefaultValue());
        return Boolean.parseBoolean(value);
    }

    @Transactional
    public SystemSettingResponseDTO update(String keyText, String rawValue, Long userId) {
        SystemSettingKey key = parseKey(keyText);
        String value = normalizeValue(key, rawValue);
        SystemSetting setting = saveSetting(key, value, userId);

        // 【本次新增：零售模式連動銷售與庫存同步】
        // 開啟零售模式時，在同一個資料庫交易內自動開啟銷售庫存同步。
        // 任一設定儲存失敗會一起回滾，避免出現零售模式已開啟但銷售未扣庫存的狀態。
        if (key == SystemSettingKey.RETAIL_MODE_ENABLED && Boolean.parseBoolean(value)) {
            saveSetting(SystemSettingKey.SALES_INVENTORY_SYNC_ENABLED, "true", userId);
        }

        return toResponse(setting);
    }

    // 【本次新增：零售模式連動銷售與庫存同步】
    // 共用設定新增／更新流程，供零售模式與被連動的銷售庫存同步設定使用。
    private SystemSetting saveSetting(SystemSettingKey key, String value, Long userId) {
        SystemSetting setting = repository.findById(key.name()).orElseGet(SystemSetting::new);
        setting.setKey(key.name());
        setting.setValue(value);
        setting.setDescription(key.getDescription());
        setting.setUpdatedByUserId(userId);
        return repository.save(setting);
    }

    private SystemSettingKey parseKey(String keyText) {
        try {
            return SystemSettingKey.valueOf(keyText);
        } catch (IllegalArgumentException | NullPointerException exception) {
            throw new IllegalArgumentException("不支援的系統設定：" + keyText);
        }
    }

    private String normalizeValue(SystemSettingKey key, String rawValue) {
        String value = rawValue == null ? "" : rawValue.trim();

        if (key.getValueType() == SystemSettingKey.ValueType.BOOLEAN) {
            value = value.toLowerCase();
            if (!"true".equals(value) && !"false".equals(value)) {
                throw new IllegalArgumentException("設定值只能是 true 或 false");
            }
        }

        if (key == SystemSettingKey.SITE_NAME) {
            if (value.isBlank()) {
                throw new IllegalArgumentException("網站名稱不得為空");
            }
            if (value.length() > 30) {
                throw new IllegalArgumentException("網站名稱不得超過 30 個字元");
            }
        }

        if (key == SystemSettingKey.SITE_LOGO_URL
                && !value.isBlank()
                && !value.startsWith("/uploads/branding/")) {
            throw new IllegalArgumentException("網站圖示網址格式不正確");
        }
        return value;
    }

    private SystemSettingResponseDTO toResponse(SystemSetting setting) {
        return new SystemSettingResponseDTO(
                setting.getKey(), setting.getValue(), setting.getDescription(),
                setting.getUpdatedAt(), setting.getUpdatedByUserId());
    }
}
