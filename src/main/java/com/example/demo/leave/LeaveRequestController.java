package com.example.demo.leave;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.leave.dto.CreateLeaveRequest;
import com.example.demo.leave.dto.LeaveRequestResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveService;

    // 建立草稿
    @PostMapping
    public LeaveRequestResponse create(
            @RequestBody @Valid CreateLeaveRequest request) {

        return LeaveRequestResponse.from(
                leaveService.createLeaveRequest(request));
    }

    // 送出簽核
    // @PostMapping("/{id}/submit")
    // public LeaveRequestResponse submit(
    // @PathVariable Long id,
    // @RequestBody SubmitLeaveRequest request) {

    // return LeaveRequestResponse.from(
    // leaveService.submitLeaveRequest(id, request.getApproverId()));
    // }

    // 查單筆
    @GetMapping("/{id}")
    public LeaveRequestResponse getById(@PathVariable Long id) {

        return LeaveRequestResponse.from(
                leaveService.getLeaveRequestOrThrow(id));
    }

    // 查某員工所有請假單
    @GetMapping
    public List<LeaveRequestResponse> getByApplicant(
            @RequestParam Long applicantId) {

        return leaveService.getByApplicant(applicantId);
    }

    // 修改草稿
    @PutMapping("/{id}")
    public LeaveRequestResponse update(
            @PathVariable Long id,
            @RequestBody @Valid CreateLeaveRequest request) {

        return LeaveRequestResponse.from(
                leaveService.updateLeaveRequest(id, request));
    }

    // 取消請假單
    @PostMapping("/{id}/cancel")
    public LeaveRequestResponse cancel(@PathVariable Long id) {

        return LeaveRequestResponse.from(
                leaveService.cancelLeaveRequest(id));
    }
}