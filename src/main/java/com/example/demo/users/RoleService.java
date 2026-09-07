package com.example.demo.users;

import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UsersRepository userRepository;

    // 1. 創建新角色 (接收 DTO 並回傳 DTO)
    @Transactional
    public RoleResponseDTO createRole(RoleRequestDTO dto) {
        if (roleRepository.existsByRoleName(dto.getRoleName())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "角色名稱 '" + dto.getRoleName() + "' 已存在");
        }

        Role role = new Role();
        role.setRoleName(dto.getRoleName());
        role.setDescription(dto.getDescription());

        Role saved = roleRepository.save(role);
        return RoleResponseDTO.fromEntity(saved);
    }

    // 1-2. 創建新角色 (向下相容字串參數，委託呼叫避免代碼重複)
    @Transactional
    public RoleResponseDTO createRole(String roleName, String description) {
        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRoleName(roleName);
        dto.setDescription(description);
        return createRole(dto);
    }

    // 2. 獲得所有角色清單 (回傳 DTO 清單)
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(RoleResponseDTO::fromEntity)
                .toList();
    }

    // 2-2. 向下相容別名
    @Transactional(readOnly = true)
    public List<RoleResponseDTO> getAllRole() {
        return getAllRoles();
    }

    // 3. 根據 ID 查詢角色詳細資料 (回傳 DTO)
    @Transactional(readOnly = true)
    public RoleResponseDTO getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));
        return RoleResponseDTO.fromEntity(role);
    }

    // 3-2. 根據 ID 查詢 Entity (內部模組關聯需要時使用)
    @Transactional(readOnly = true)
    public Optional<Role> findRoleEntityById(Long id) {
        return roleRepository.findById(id);
    }

    // 4. 根據名稱查詢角色
    @Transactional(readOnly = true)
    public Optional<RoleResponseDTO> getRoleByName(String roleName) {
        return roleRepository.findByRoleName(roleName).map(RoleResponseDTO::fromEntity);
    }

    // 5. 修改角色資訊 (接收 DTO 並回傳 DTO)
    @Transactional
    public RoleResponseDTO updateRole(Long id, RoleRequestDTO dto) {
        Role dbRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

        if (!dbRole.getRoleName().equals(dto.getRoleName())) {
            if (roleRepository.existsByRoleName(dto.getRoleName())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "新角色名稱已被占用");
            }
            dbRole.setRoleName(dto.getRoleName());
        }
        dbRole.setDescription(dto.getDescription());
        Role saved = roleRepository.save(dbRole);
        return RoleResponseDTO.fromEntity(saved);
    }

    // 5-2. 修改角色資訊 (向下相容 Entity 參數，委託呼叫避免代碼重複)
    @Transactional
    public RoleResponseDTO updateRole(Long id, Role updateDetails) {
        Role dbRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

        RoleRequestDTO dto = new RoleRequestDTO();
        dto.setRoleName(updateDetails.getRoleName() != null ? updateDetails.getRoleName() : dbRole.getRoleName());
        dto.setDescription(
                updateDetails.getDescription() != null ? updateDetails.getDescription() : dbRole.getDescription());
        return updateRole(id, dto);
    }

    // 6. 刪除角色 (精準捕捉 DataIntegrityViolationException)
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到指定角色"));

        // 刪除前主動檢查是否有使用者使用該角色
        if (userRepository.existsByRoleId(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "無法刪除角色：目前仍有使用者關聯至此角色");
        }

        try {
            roleRepository.delete(role);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "無法刪除角色：資料庫約束衝突（可能仍有關聯資料）");
        }
    }

}
