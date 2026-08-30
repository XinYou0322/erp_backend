package com.example.demo.users;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final PasswordEncoder pwdEncoder1;

    private final UsersRepository usersRepo;

    private final PasswordEncoder pwdEncoder;

    // 1. 檢查帳號是否存在
    public boolean checkUsernameExist(String username) {
        return usersRepo.findByUsername(username).isPresent();
    }

    // 2. 註冊 / 新增員工
    public boolean register(String username,
            String password,
            String name, String email,
            Long roleId, Long departmentId, Long storeId) {
        boolean exist = checkUsernameExist(username);

        if (exist) {
            return false;
        }

        Users users = new Users();
        users.setUsername(username);
        users.setPassword(pwdEncoder1.encode(password));

        // 負責的 RBAC 與組織架構欄位
        users.setName(name);
        users.setEmail(email);
        // users.setRoleId(roleId);
        users.setDepartmentId(departmentId);
        users.setStatus("ACTIVE");// 預設帳號啟用

        usersRepo.save(users);

        return true;
    }

    // 3. 登入檢查
    public Users checkLogin(String inputUsername, String inputPassword) {
        boolean exist = checkUsernameExist(inputUsername);

        if (!exist) {
            return null;
        }

        Optional<Users> op = usersRepo.findByUsername(inputUsername);
        Users dbUsers = op.get();

        // 帳號狀態防禦：若被後台停用則拒絕登入
        if ("INACTIVE".equals(dbUsers.getStatus())) {
            return null;
        }

        String dbPassword = dbUsers.getPassword();
        boolean result = pwdEncoder1.matches(inputPassword, dbPassword);

        if (result) {
            return dbUsers;
        }

        return null;
    }

    // 4. 依據 ID 查詢單一使用者 (中介層驗證/個人資料 API 常用)
    public Users findById(Long id) {
        Optional<Users> op = usersRepo.findById(id);
        return op.orElse(null);
    }

    // 6. 後台修改員工組織與權限資料
    public boolean updateUser(Long id, Users updateDetails) {
        Optional<Users> op = usersRepo.findById(id);

        if (!op.isPresent()) {
            return false;
        }

        Users dbUser = op.get();
        dbUser.setName(updateDetails.getName());
        dbUser.setEmail(updateDetails.getEmail());
        // dbUser.setRoleId(updateDetails.getRoleId());
        dbUser.setDepartmentId(updateDetails.getDepartmentId());
        dbUser.setStatus(updateDetails.getStatus());

        usersRepo.save(dbUser);
        return true;
    }

    // 7. 凍結或啟用帳號 (軟刪除)
    public boolean updateStatus(Long id, String status) {
        Optional<Users> op = usersRepo.findById(id);

        if (!op.isPresent()) {
            return false;
        }

        Users dbUser = op.get();
        dbUser.setStatus(status);

        usersRepo.save(dbUser);
        return true;
    }

}
