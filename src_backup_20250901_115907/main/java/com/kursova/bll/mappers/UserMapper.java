package com.kursova.bll.mappers;

import com.kursova.bll.dto.UserDto;
import com.kursova.dal.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for User entity and UserDto
 */
@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface UserMapper {
    
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
    
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    UserDto toDto(User entity);
    
    @Mapping(target = "password", ignore = true) // Password should be handled separately
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "student", ignore = true)
    User toEntity(UserDto dto);
    
    List<UserDto> toDtoList(List<User> entities);
    
    List<User> toEntityList(List<UserDto> dtos);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "student", ignore = true)
    void updateEntityFromDto(UserDto dto, @MappingTarget User entity);
}
