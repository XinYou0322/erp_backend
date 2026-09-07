package com.example.demo.users;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final PasswordEncoder passwordEncoder;
    private final UsersRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    // 1. 檢查帳號是否存在
    @Transactional(readOnly = true)
    public boolean checkUsernameExist(String username) {
        return userRepository.existsByUsername(username);
    }

    // 2. 檢查 Email 是否存在
    @Transactional(readOnly = true)
    public boolean checkEmailExist(String email) {
        return userRepository.existsByEmail(email);
    }

    // 3. 註冊 / 新增員工 (接收 DTO)
    @Transactional
    public UserResponseDTO createUser(UserRegisterDTO dto) {
        if (checkUsernameExist(dto.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在");
        }
        if (checkEmailExist(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email 已被使用");
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(role);
        // user.setDepartmentId(dto.getDepartmentId());
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    // 4. 註冊 (保留舊版多參數方法，委託呼叫 createUser 避免代碼重複)
    @Transactional
    public User register(String username, String password, String name, String email, Long roleId, Long departmentId) {
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername(username);
        dto.setPassword(password);
        dto.setName(name);
        dto.setEmail(email);
        dto.setRoleId(roleId);
        // dto.setDepartmentId(departmentId);

        createUser(dto);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "使用者建立失敗"));
    }

    // 5. 登入檢查 (回傳 User Entity)
    @Transactional(readOnly = true)
    public Optional<User> checkLogin(String inputUsername, String inputPassword) {
        return userRepository.findByUsername(inputUsername)
                .filter(user -> {
                    if (UserStatus.INACTIVE.equals(user.getStatus()) || UserStatus.LOCKED.equals(user.getStatus())) {
                        return false;
                    }
                    return passwordEncoder.matches(inputPassword, user.getPassword());
                });
    }

    // 6. 登入檢查 (接收 DTO 並回傳 DTO)
    @Transactional(readOnly = true)
    public UserResponseDTO login(LoginRequestDTO dto) {
        User user = userRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤"));

        if (UserStatus.INACTIVE.equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "帳號已停用，請聯絡管理員");
        }
        if (UserStatus.LOCKED.equals(user.getStatus())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "帳號已被鎖定");
        }

        if (!passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "帳號或密碼錯誤");
        }

        return userMapper.toDto(user);
    }

    // 7. 依據 ID 查詢單一使用者
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    // 8. 依據 ID 查詢單一使用者 DTO
    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));
        return userMapper.toDto(user);
    }

    // 9. 查詢所有使用者清單
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    // 10. 分頁與關鍵字查詢使用者
    @Transactional(readOnly = true)
    public Page<UserResponseDTO> getUsersPage(String keyword, Pageable pageable) {
        Page<User> pageResult;
        if (keyword != null && !keyword.trim().isEmpty()) {
            pageResult = userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                    keyword.trim(), keyword.trim(), pageable);
        } else {
            pageResult = userRepository.findAll(pageable);
        }
        return pageResult.map(userMapper::toDto);
    }

    // 11. 依據部門查詢員工
    // @Transactional(readOnly = true)
    // public List<UserResponseDTO> getUsersByDepartment(Long departmentId) {
    // return userRepository.findByDepartmentId(departmentId).stream()
    // .map(userMapper::toDto)
    // .toList();
    // }

    // 12. 後台修改員工資料 (接收 DTO)
    @Transactional
    public UserResponseDTO updateUser(Long id, UserUpdateDTO dto) {
        User dbUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

        // 若 Email 有變動，檢查是否被其他人占用
        if (!dbUser.getEmail().equals(dto.getEmail()) && userRepository.existsByEmail(dto.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email 已被其他使用者使用");
        }

        Role role = roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

        dbUser.setName(dto.getName());
        dbUser.setEmail(dto.getEmail());
        // dbUser.setDepartmentId(dto.getDepartmentId());
        dbUser.setStatus(dto.getStatus());
        dbUser.setRole(role);

        User saved = userRepository.save(dbUser);
        return userMapper.toDto(saved);
    }

    // 13. 後台修改員工資料 (保留舊版 Entity 參數方法，委託呼叫新版方法避免重複)
    @Transactional
    public User updateUser(Long id, User updateDetails) {
        User dbUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

        Long roleId = updateDetails.getRole() != null ? updateDetails.getRole().getId()
                : (dbUser.getRole() != null ? dbUser.getRole().getId() : null);

        UserUpdateDTO dto = new UserUpdateDTO();
        dto.setName(updateDetails.getName() != null ? updateDetails.getName() : dbUser.getName());
        dto.setEmail(updateDetails.getEmail() != null ? updateDetails.getEmail() : dbUser.getEmail());
        // dto.setDepartmentId(updateDetails.getDepartmentId() != null ?
        // updateDetails.getDepartmentId()
        // : dbUser.getDepartmentId());
        dto.setStatus(updateDetails.getStatus() != null ? updateDetails.getStatus() : dbUser.getStatus());
        dto.setRoleId(roleId);

        updateUser(id, dto);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));
    }

    // 14. 凍結或啟用帳號
    @Transactional
    public void updateStatus(Long id, UserStatus status) {
        User dbUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

        dbUser.setStatus(status);
        userRepository.save(dbUser);
    }

    // 15. 修改密碼
    @Transactional
    public void updatePassword(Long id, UpdatePasswordDTO dto) {
        User dbUser = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

        if (!passwordEncoder.matches(dto.getOldPassword(), dbUser.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "舊密碼不正確");
        }

        dbUser.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(dbUser);
    }

}
