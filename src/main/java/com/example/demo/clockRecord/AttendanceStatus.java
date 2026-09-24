package com.example.demo.clockRecord;

public enum AttendanceStatus {

    NORMAL("正常出勤"),
    LATE("遲到"),
    EARLY_LEAVE("早退"),
    LATE_AND_EARLY_LEAVE("遲到/早退"),
    ABSENT("缺勤"),
    INCOMPLETE("上班中"),   // 今天已打卡上班，尚未打卡下班
    REST_DAY("休假日"),     // 週末或國定假日，不列入出勤計算
    FUTURE("尚未到達");      // 未來日期，尚無資料

    private final String label;

    AttendanceStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
