package com.kursova.bll.mappers;

import com.kursova.bll.dto.StudentDto;
import com.kursova.dal.entities.Student;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for Student entity and StudentDto
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, DateTimeMapper.class})
public interface StudentMapper {

    StudentMapper INSTANCE = Mappers.getMapper(StudentMapper.class);

    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "group", ignore = true) // Avoid circular references
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "averageGrade", ignore = true)
    StudentDto toDto(Student entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "grades", ignore = true)
    Student toEntity(StudentDto dto);

    @Named("studentToDtoSimple")
    List<StudentDto> toDtoList(List<Student> entities);

    List<Student> toEntityList(List<StudentDto> dtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "grades", ignore = true)
    void updateEntityFromDto(StudentDto dto, @MappingTarget Student entity);

    // Simple mapping without nested objects for lists
    @Named("studentToDtoSimple")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "group", ignore = true) // Avoid circular references in lists
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "averageGrade", ignore = true)
    StudentDto toDtoSimple(Student entity);
}
