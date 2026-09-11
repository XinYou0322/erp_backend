package com.example.demo.users;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UsersService usersService;

    // 允許排序的欄位白名單
    private static final Set<String> ALLOWED_SORT_FIELDS = Set.of(
            "id", "username", "name", "email", "createdAt", "status");

    // 1. 使用者登入驗證 (寫入 Session)
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto, HttpServletRequest request) {
        UserResponseDTO userResponse = usersService.login(dto);

        // 將使用者資訊與 ID 存入 Session，供 NotificationController 驗證
        HttpSession session = request.getSession(true);
        session.setAttribute("userId", userResponse.getId());
        session.setAttribute("currentUser", userResponse);
        return ResponseEntity.ok(Map.of(
                "message", "登入成功",
                "user", userResponse));
    }

    // 1-2. 取得當前已登入使用者資料 (對接前端 userService.js)
    @GetMapping("/now")
    public ResponseEntity<?> getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            return ResponseEntity.ok(session.getAttribute("currentUser"));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "尚未登入"));
    }

    // 1-3. 登出端點 (清除 Session)
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.ok(Map.of("message", "已登出系統"));
    }

    // 2. 新增使用者 / 員工註冊
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserRegisterDTO dto) {
        UserResponseDTO createdUser = usersService.createUser(dto);
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    // 2-2. 新增使用者別名路由
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterDTO dto) {
        return createUser(dto);
    }

    // 3. 查詢使用者分頁列表（支援關鍵字搜尋姓名或帳號）
    @GetMapping
    public ResponseEntity<Page<UserResponseDTO>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        // 排序欄位白名單安全校驗，防止非法欄位導致 JPA 500 異常
        String safeSortBy = ALLOWED_SORT_FIELDS.contains(sortBy) ? sortBy : "id";
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(safeSortBy).ascending()
                : Sort.by(safeSortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserResponseDTO> result = usersService.getUsersPage(keyword, pageable);
        return ResponseEntity.ok(result);
    }

    // 4. 查詢所有使用者（不分頁，供前端下拉清單使用）
    @GetMapping("/all")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserResponseDTO> users = usersService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 5. 依據 ID 查詢使用者詳細資訊
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO user = usersService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // 6. 依據部門查詢使用者
    // @GetMapping("/department/{departmentId}")
    // public ResponseEntity<List<UserResponseDTO>>
    // getUsersByDepartment(@PathVariable Long departmentId) {
    // List<UserResponseDTO> users =
    // usersService.getUsersByDepartment(departmentId);
    // return ResponseEntity.ok(users);
    // }

    // 7. 修改使用者資料
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO dto) {
        UserResponseDTO updated = usersService.updateUser(id, dto);
        return ResponseEntity.ok(updated);
    }

    // 8. 啟用 / 停用 / 鎖定帳號狀態
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestParam UserStatus status) {
        usersService.updateStatus(id, status);
        return ResponseEntity.ok(Map.of(
                "message", "狀態已更新為: " + status.name()));
    }

    // 9. 修改個人密碼
    @PutMapping("/{id}/password")
    public ResponseEntity<?> updatePassword(
            @PathVariable Long id,
            @Valid @RequestBody UpdatePasswordDTO dto) {
        usersService.updatePassword(id, dto);
        return ResponseEntity.ok(Map.of(
                "message", "密碼更新成功"));
    }

    // 10. 刪除使用者
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        usersService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message", "使用者刪除成功"));
    }

}
