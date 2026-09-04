package com.example.demo.users;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring") // 註冊為 Spring Bean，可在 Service 直接 @Autowired
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    // MapStruct 會自動根據名稱對齊欄位，並自動生成巢狀物件（Role -> RoleInfo）的轉換邏輯
    UserResponseDTO toDto(User user);

}
