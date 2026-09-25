package com.example.demo.purchaseOrder;

import java.util.List;
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.example.demo.users.User;
import com.example.demo.users.UserStatus;
import com.example.demo.users.UsersRepository;
import lombok.RequiredArgsConstructor;

/** 【新增】採購簽核資格唯一規則來源，使用資料庫 users.role_level。 */
@Service
@RequiredArgsConstructor
public class PurchaseApproverPolicy {
    private final UsersRepository usersRepository;

    // 【新增】只輸出選單需要的欄位，不回傳使用者實體或密碼。
    public record Option(Long id, String name, String username, Integer roleLevel) {}

    private boolean activeWithLevel(User user) {
        return user != null && user.getId() != null && user.getStatus() == UserStatus.ACTIVE
                && user.getRoleLevel() != null && user.getRoleLevel() >= 1;
    }

    public boolean eligible(User applicant, User approver) {
        if (!activeWithLevel(applicant) || !activeWithLevel(approver)) return false;
        // 【新增】1 為最高，僅可自簽；其他層級只能找數字更小的簽核人。
        if (applicant.getRoleLevel() == 1) return Objects.equals(applicant.getId(), approver.getId());
        return !Objects.equals(applicant.getId(), approver.getId())
                && approver.getRoleLevel() < applicant.getRoleLevel();
    }

    @Transactional(readOnly = true)
    public User requireEligible(Long applicantId, Long approverId) {
        if (applicantId == null || approverId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "請指定有效的申請人及簽核人");
        User applicant = usersRepository.findById(applicantId).orElse(null);
        User approver = usersRepository.findById(approverId).orElse(null);
        if (!eligible(applicant, approver))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "簽核人資格不符：請選擇權限更高的啟用帳號；層級 1 僅可自簽");
        return approver;
    }

    @Transactional(readOnly = true)
    public List<Option> options(Long applicantId) {
        if (applicantId == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "請先登入");
        User applicant = usersRepository.findById(applicantId).orElse(null);
        if (!activeWithLevel(applicant))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "登入帳號未啟用或角色層級無效");
        return usersRepository.findAll().stream().filter(user -> eligible(applicant, user))
                .sorted(java.util.Comparator.comparing(User::getRoleLevel).thenComparing(User::getId))
                .map(user -> new Option(user.getId(), user.getName(), user.getUsername(), user.getRoleLevel()))
                .toList();
    }
}
