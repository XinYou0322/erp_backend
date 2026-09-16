package com.example.demo.leave;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.leave.dto.CreateLeaveRequest;
import com.example.demo.leave.dto.LeaveRequestResponse;
import com.example.demo.leave.dto.SubmitLeaveRequest;
import com.example.demo.leave.dto.UpdateLeaveRequest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/leave-requests")
@RequiredArgsConstructor
public class LeaveRequestController {

    private final LeaveRequestService leaveService;

    // 建立草稿
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LeaveRequestResponse create(
            @RequestBody @Valid CreateLeaveRequest request) {

        return LeaveRequestResponse.from(
                leaveService.createLeaveRequest(request));
    }

    // 送出簽核
    @PostMapping("/{id}/submit")
    public LeaveRequestResponse submit(
            @PathVariable Long id,
            @RequestBody @Valid SubmitLeaveRequest request) {

        return LeaveRequestResponse.from(
                leaveService.submitLeaveRequest(id, request.getApproverId()));
    }

    // 查單筆
    @GetMapping("/{id}")
    public LeaveRequestResponse getById(@PathVariable Long id) {

        return LeaveRequestResponse.from(
                leaveService.getLeaveRequestOrThrow(id));
    }

    // 查某員工所有請假單
    @GetMapping("/applicant/{applicantId}")
    public List<LeaveRequestResponse> getByApplicant(
            @PathVariable Long applicantId) {

        return leaveService.getByApplicant(applicantId);
    }

    // 修改草稿
    @PutMapping("/{id}")
    public LeaveRequestResponse update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateLeaveRequest request) {

        return LeaveRequestResponse.from(
                leaveService.updateLeaveRequest(id, request));
    }

    // 取消請假單
    @PostMapping("/{id}/cancel")
    public ResponseEntity<LeaveRequestResponse> cancel(@PathVariable Long id) {

        LeaveRequestResponse response = LeaveRequestResponse.from(
                leaveService.cancelLeaveRequest(id));
        return ResponseEntity.ok(response);
    }

    // 刪除草稿
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveRequest(@PathVariable Long id) {
        leaveService.deleteLeaveRequest(id);
        return ResponseEntity.noContent().build();
    }
}