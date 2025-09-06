package com.kursova.bll.mappers;

import com.kursova.bll.dto.SubjectDto;
import com.kursova.dal.entities.Subject;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Mapper for Subject entity and SubjectDto
 */
@Mapper(componentModel = "spring", uses = {DateTimeMapper.class})
public interface SubjectMapper {

    SubjectMapper INSTANCE = Mappers.getMapper(SubjectMapper.class);

    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "teachers", ignore = true) // Handle teachers separately to avoid circular dependency
    @Mapping(target = "groupCount", expression = "java(entity.getGroups() != null ? entity.getGroups().size() : 0)")
    SubjectDto toDto(Subject entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "groups", ignore = true)
    Subject toEntity(SubjectDto dto);

    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "groupCount", expression = "java(entity.getGroups() != null ? entity.getGroups().size() : 0)")
    List<SubjectDto> toDtoList(List<Subject> entities);

    List<Subject> toEntityList(List<SubjectDto> dtos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "grades", ignore = true)
    @Mapping(target = "groups", ignore = true)
    void updateEntityFromDto(SubjectDto dto, @MappingTarget Subject entity);

    // Simple mapping without nested objects for lists
    @Named("subjectToDto")
    @Mapping(target = "teachers", ignore = true)
    @Mapping(target = "createdAt", source = "createdAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "updatedAt", source = "updatedAt", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "groupCount", expression = "java(entity.getGroups() != null ? entity.getGroups().size() : 0)")
    SubjectDto toDtoSimple(Subject entity);
}
