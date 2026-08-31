package com.example.demo.users;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    // 1.創建新角色
    @Transactional
    public Role createRole(String roleNmae, String description) {
        // 檢查角色名稱是否已存在，防止重複
        if (roleRepository.existsByRoleName(roleNmae)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "角色名稱 '" + roleNmae + "' 已存在");
        }

        Role role = new Role();
        role.setRoleName(roleNmae);
        role.setDescription(description);

        return roleRepository.save(role);
    }

    // 2.獲得所有角色清單(用於後台下拉選單)
    public List<Role> getAllRole() {
        return roleRepository.findAll();
    }

    // 3.根據ID查詢角色詳細資料
    public Optional<Role> getRoleById(Long id) {
        return roleRepository.findById(id);
    }

    // 4.根據名稱查詢角色
    public Optional<Role> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName);
    }

    // 5.修改角色資訊
    @Transactional
    public Role updateRole(Long id, Role updateDetails) {
        Role dbRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

        // 如果修改了名稱，需要檢查新名稱是否被其他角色占用
        if (!dbRole.getRoleName().equals(updateDetails.getRoleName())) {
            if (roleRepository.existsByRoleName(updateDetails.getRoleName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "新角色名稱已被占用");
            }
            dbRole.setRoleName(updateDetails.getRoleName());
        }
        dbRole.setDescription(updateDetails.getDescription());
        return roleRepository.save(dbRole);
    }

    // 6.刪除角色
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

        try {
            roleRepository.delete(role);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無法刪除角色：目前仍有使用者關聯至此角色");
        }
    }
}
