// package com.example.demo.users;

// import java.util.List;
// import java.util.Optional;

// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.web.server.ResponseStatusException;

// import lombok.RequiredArgsConstructor;

// import org.springframework.http.HttpStatus;
// import org.springframework.security.crypto.password.PasswordEncoder;

// @Service
// @RequiredArgsConstructor
// public class UsersService {

//     private final PasswordEncoder passwordEncoder;

//     private final UsersRepository userRepository;

//     private final RoleRepository roleRepository;

//     // 1. 檢查帳號是否存在
//     public boolean checkUsernameExist(String username) {
//         return userRepository.existsByUsername(username);
//     }

//     // 2. 註冊 / 新增員工
//     @Transactional
//     public User register(String username, String password, String name, String email, Long roleId, Long departmentId) {
//         if (checkUsernameExist(username)) {
//             throw new ResponseStatusException(HttpStatus.CONFLICT, "帳號已存在");
//         }

//         Role role = roleRepository.findById(roleId)
//                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

//         User user = new User();
//         user.setUsername(username);
//         user.setPassword(passwordEncoder.encode(password));
//         user.setName(name);
//         user.setEmail(email);
//         user.setRole(role);
//         user.setDepartmentId(departmentId);
//         user.setStatus(UserStatus.ACTIVE);

//         return userRepository.save(user);
//     }

//     // 3. 登入檢查
//     public Optional<User> checkLogin(String inputUsername, String inputPassword) {
//         return userRepository.findByUsername(inputUsername)
//                 .filter(user -> {
//                     // 檢查狀態是否為啟用
//                     if (UserStatus.INACTIVE.equals(user.getStatus())) {
//                         return false;
//                     }
//                     // 檢查密碼是否匹配
//                     return passwordEncoder.matches(inputPassword, user.getPassword());
//                 });

//     }

//     // 4. 依據 ID 查詢單一使用者 (中介層驗證/個人資料 API 常用)
//     public Optional<User> findById(Long id) {
//         return userRepository.findById(id);
//     }

//     // 5. 後台修改員工資料
//     @Transactional
//     public User updateUser(Long id, User updateDetails) {
//         User dbUser = userRepository.findById(id)
//                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

//         dbUser.setName(updateDetails.getName());
//         dbUser.setEmail(updateDetails.getEmail());
//         dbUser.setDepartmentId(updateDetails.getDepartmentId());
//         dbUser.setStatus(updateDetails.getStatus());

//         if (updateDetails.getRole() != null) {
//             dbUser.setRole(updateDetails.getRole());
//         }
//         return userRepository.save(dbUser);
//     }

//     // 6. 凍結或啟用帳號
//     @Transactional
//     public void updateStatus(Long id, UserStatus status) {
//         User dbUser = userRepository.findById(id)
//                 .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到使用者"));

//         dbUser.setStatus(status);
//         userRepository.save(dbUser);
//     }

// }
