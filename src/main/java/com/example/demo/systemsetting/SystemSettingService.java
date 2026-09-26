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
        SystemSetting setting = repository.findById(key.name()).orElseGet(SystemSetting::new);
        setting.setKey(key.name());
        setting.setValue(value);
        setting.setDescription(key.getDescription());
        setting.setUpdatedByUserId(userId);
        return toResponse(repository.save(setting));
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
