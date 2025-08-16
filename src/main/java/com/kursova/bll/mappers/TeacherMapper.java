package com.kursova.bll.mappers;

import com.kursova.bll.dto.TeacherDto;
import com.kursova.dal.entities.Teacher;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for Teacher entity and TeacherDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, SubjectMapper.class, DateTimeMapper.class})
public interface TeacherMapper {
    
    TeacherMapper INSTANCE = Mappers.getMapper(TeacherMapper.class);
    
    @Mapping(target = "hireDate", source = "hireDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "subjects", source = "subjects", qualifiedByName = "subjectToDto")
    TeacherDto toDto(Teacher entity);
    
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "hireDate", ignore = true)
    @Mapping(target = "subjects", ignore = true)
    @Mapping(target = "grades", ignore = true)
    Teacher toEntity(TeacherDto dto);
    
    @Named("teacherToDtoSimple")
    List<TeacherDto> toDtoList(List<Teacher> entities);
    
    List<Teacher> toEntityList(List<TeacherDto> dtos);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "hireDate", ignore = true)
    @Mapping(target = "subjects", ignore = true)
    @Mapping(target = "grades", ignore = true)
    void updateEntityFromDto(TeacherDto dto, @MappingTarget Teacher entity);
    
    // Simple mapping without nested objects for lists
    @Named("teacherToDtoSimple")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "subjects", ignore = true) // Avoid circular references in lists
    @Mapping(target = "hireDate", source = "hireDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    TeacherDto toDtoSimple(Teacher entity);
}
