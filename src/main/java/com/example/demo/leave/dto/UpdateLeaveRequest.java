package com.example.demo.leave.dto;

import java.time.LocalDate;

import com.example.demo.leave.enums.LeaveType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateLeaveRequest {

    // 1. 移除 applicantId！更新草稿不允許更改申請人

    // 2. 移除 @NotNull，允許前端「部分更新」(只傳想改的欄位)
    private LeaveType leaveType;
    private LocalDate startDate;
    private LocalDate endDate;

    // 3. 保留格式驗證，但不強制必填
    // 注意：@NotBlank 對 null 是放行的。
    // 意思是：如果前端沒傳 reason，不會報錯；但如果傳了 "" 或 " "，就會報錯。
    @NotBlank(message = "若提供申請原因，則不能為空白")
    @Size(max = 500, message = "申請原因不能超過 500 字")
    private String reason;

}
