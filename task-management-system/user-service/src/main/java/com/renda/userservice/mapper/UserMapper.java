package com.renda.userservice.mapper;

import com.renda.userservice.dto.UserRequestDto;
import com.renda.userservice.dto.UserResponseDto;
import com.renda.userservice.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    /* ============ Entity → DTO ============ */

    UserResponseDto toDto(User user);

    List<UserResponseDto> toDtoList(List<User> users);

    /* ============ DTO → Entity ============ */

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdTime", ignore = true)
    User toEntity(UserRequestDto dto);

}
