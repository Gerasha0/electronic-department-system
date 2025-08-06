package com.kursova.bll.mappers;

import com.kursova.bll.dto.StudentGroupDto;
import com.kursova.dal.entities.StudentGroup;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for StudentGroup entity and StudentGroupDto
 */
@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface StudentGroupMapper {
    
    StudentGroupMapper INSTANCE = Mappers.getMapper(StudentGroupMapper.class);
    
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "students", ignore = true) // Avoid circular references
    @Mapping(target = "currentStudentCount", expression = "java(entity.getCurrentStudentCount())")
    @Named("toDto")
    StudentGroupDto toDto(StudentGroup entity);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "students", ignore = true)
    StudentGroup toEntity(StudentGroupDto dto);
    
    @Mapping(target = "students", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "currentStudentCount", expression = "java(entity.getCurrentStudentCount())")
    @Named("toDtoSimple")
    StudentGroupDto toDtoSimple(StudentGroup entity);
    
    @Named("toDtoList")
    List<StudentGroupDto> toDtoList(List<StudentGroup> entities);
    
    List<StudentGroup> toEntityList(List<StudentGroupDto> dtos);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "students", ignore = true)
    void updateEntityFromDto(StudentGroupDto dto, @MappingTarget StudentGroup entity);
}
