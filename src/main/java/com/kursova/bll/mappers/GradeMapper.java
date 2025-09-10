package com.kursova.bll.mappers;

import com.kursova.bll.dto.GradeDto;
import com.kursova.dal.entities.Grade;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for Grade entity and GradeDto
 */
@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface GradeMapper {

    GradeMapper INSTANCE = Mappers.getMapper(GradeMapper.class);

    @Mapping(target = "gradeDate", source = "gradeDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "gradeCategoryEnum", source = "gradeCategoryEnum")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentUserId", source = "student.user.id")
    @Mapping(target = "studentName", expression = "java(entity.getStudent().getUser().getFirstName() + \" \" + entity.getStudent().getUser().getLastName())")
    @Mapping(target = "studentNumber", source = "student.studentNumber")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", expression = "java(entity.getTeacher().getUser().getFirstName() + \" \" + entity.getTeacher().getUser().getLastName())")
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", source = "subject.subjectName")
    @Mapping(target = "subjectCode", source = "subject.subjectCode")
    @Mapping(target = "groupId", source = "student.group.id")
    @Mapping(target = "groupName", source = "student.group.groupName")
    GradeDto toDto(Grade entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "gradeDate", ignore = true)
    @Mapping(target = "gradeCategoryEnum", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "subject", ignore = true)
    Grade toEntity(GradeDto dto);

    List<GradeDto> toDtoList(List<Grade> entities);

    List<Grade> toEntityList(List<GradeDto> dtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "student", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "subject", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "gradeDate", ignore = true)
    @Mapping(target = "gradeCategory", ignore = true)
    void updateEntityFromDto(GradeDto dto, @MappingTarget Grade entity);

    // Simple mapping without nested objects for lists
    @Named("gradeToDtoSimple")
    @Mapping(target = "gradeDate", source = "gradeDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentUserId", source = "student.user.id")
    @Mapping(target = "studentName", ignore = true)
    @Mapping(target = "studentNumber", ignore = true)
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", ignore = true)
    @Mapping(target = "subjectId", source = "subject.id")
    @Mapping(target = "subjectName", ignore = true)
    @Mapping(target = "subjectCode", ignore = true)
    @Mapping(target = "groupId", source = "student.group.id")
    @Mapping(target = "groupName", ignore = true)
    GradeDto toDtoSimple(Grade entity);
}
