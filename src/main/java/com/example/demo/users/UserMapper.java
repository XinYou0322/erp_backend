package com.example.demo.users;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "roleLevel", target = "roleLevel")
    @Mapping(source = "avatar", target = "avatar")
    UserResponseDTO toDto(User user);
}
