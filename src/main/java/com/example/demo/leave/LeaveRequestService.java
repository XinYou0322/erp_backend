package com.example.demo.leave;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.example.demo.leave.dto.CreateLeaveRequest;

public class LeaveRequestService {

    // 新增請假單
    public LeaveRequest createLeaveRequest(CreateLeaveRequest request) {

        // 驗證請假天數日期區間是否合法
        // 建立LeaveRequest entity並存檔
        // 呼叫workflowService.startworkflow(...),
        // 回傳LeaveRequest

        return null;

    }

    // 送出請假單

    // 列出某員工所有請假單
    // public List<LeaveRequestResponse> getByApplicant(Long applicatId){

    // return ;
    // }

    // 更新請假單
    public void updateLeaveRequest(Long id, Long applicatId) {

    }

    // 取消請假單
    public void cancelLeaveRequest(Long id, Long applicatId) {

    }

    // 更新狀態

}
