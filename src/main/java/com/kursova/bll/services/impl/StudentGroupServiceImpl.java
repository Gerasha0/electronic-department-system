package com.kursova.bll.services.impl;

import com.kursova.bll.dto.StudentGroupDto;
import com.kursova.bll.mappers.StudentGroupMapper;
import com.kursova.bll.services.ArchiveService;
import com.kursova.bll.services.StudentGroupService;
import com.kursova.dal.entities.StudentGroup;
import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.EducationLevel;
import com.kursova.dal.entities.StudyForm;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of StudentGroupService
 */
@Service
@Transactional
public class StudentGroupServiceImpl implements StudentGroupService {

    private final UnitOfWork unitOfWork;
    private final StudentGroupMapper groupMapper;
    private final ArchiveService archiveService;

    @Autowired
    public StudentGroupServiceImpl(UnitOfWork unitOfWork, StudentGroupMapper groupMapper, ArchiveService archiveService) {
        this.unitOfWork = unitOfWork;
        this.groupMapper = groupMapper;
        this.archiveService = archiveService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGroupDto> findAll() {
        return unitOfWork.getStudentGroupRepository().findAll()
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentGroupDto findById(Long id) {
        StudentGroup group = unitOfWork.getStudentGroupRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("StudentGroup not found with id: " + id));
        return groupMapper.toDto(group);
    }

    @Override
    public StudentGroupDto create(StudentGroupDto groupDto) {
        if (existsByGroupCode(groupDto.getGroupCode())) {
            throw new RuntimeException("Group code already exists: " + groupDto.getGroupCode());
        }

        StudentGroup group = groupMapper.toEntity(groupDto);
        group.setCreatedAt(LocalDateTime.now());
        group.setUpdatedAt(LocalDateTime.now());
        group.setIsActive(true);

        StudentGroup savedGroup = unitOfWork.getStudentGroupRepository().save(group);
        return groupMapper.toDto(savedGroup);
    }

    @Override
    @Transactional
    public StudentGroupDto update(Long id, StudentGroupDto groupDto) {
        StudentGroup existingGroup = unitOfWork.getStudentGroupRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("StudentGroup not found with id: " + id));

        // Check if group code is being changed and if it already exists
        if (!existingGroup.getGroupCode().equals(groupDto.getGroupCode()) && 
            existsByGroupCode(groupDto.getGroupCode())) {
            throw new RuntimeException("Group code already exists: " + groupDto.getGroupCode());
        }

        // Store original values to check if education level, study form, or course year changed
        EducationLevel originalEducationLevel = existingGroup.getEducationLevel();
        StudyForm originalStudyForm = existingGroup.getStudyForm();
        Integer originalCourseYear = existingGroup.getCourseYear();

        groupMapper.updateEntityFromDto(groupDto, existingGroup);
        existingGroup.setUpdatedAt(LocalDateTime.now());

        StudentGroup updatedGroup = unitOfWork.getStudentGroupRepository().save(existingGroup);
        
        // If education level, study form, or course year changed, update all students in the group
        if ((originalEducationLevel != updatedGroup.getEducationLevel()) || 
            (originalStudyForm != updatedGroup.getStudyForm()) ||
            (!originalCourseYear.equals(updatedGroup.getCourseYear()))) {
            
            for (Student student : updatedGroup.getStudents()) {
                student.setEducationLevel(updatedGroup.getEducationLevel());
                student.setStudyForm(updatedGroup.getStudyForm());
                student.setCourseYear(updatedGroup.getCourseYear());
                student.setUpdatedAt(LocalDateTime.now());
                unitOfWork.getStudentRepository().save(student);
            }
        }
        
        return groupMapper.toDto(updatedGroup);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        System.out.println("=== StudentGroupService.delete() called for group ID: " + id + " ===");
        // Archive the group and all related data instead of just deleting
        archiveService.archiveStudentGroup(id, "SYSTEM", "Group deleted by user");
        System.out.println("=== StudentGroupService.delete() completed for group ID: " + id + " ===");
    }

    @Override
    public boolean existsById(Long id) {
        return unitOfWork.getStudentGroupRepository().existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGroupDto> findActiveGroups() {
        return unitOfWork.getStudentGroupRepository().findByIsActiveTrue()
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGroupDto> searchByName(String name) {
        return unitOfWork.getStudentGroupRepository().findByGroupNameContainingIgnoreCase(name)
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGroupDto> searchByNameOrCode(String searchTerm) {
        return unitOfWork.getStudentGroupRepository()
                .findByGroupNameContainingIgnoreCaseOrGroupCodeContainingIgnoreCase(searchTerm, searchTerm)
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentGroupDto findByGroupCode(String groupCode) {
        StudentGroup group = unitOfWork.getStudentGroupRepository().findByGroupCode(groupCode)
                .orElseThrow(() -> new RuntimeException("StudentGroup not found with code: " + groupCode));
        return groupMapper.toDto(group);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByGroupCode(String groupCode) {
        return unitOfWork.getStudentGroupRepository().existsByGroupCode(groupCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGroupDto> findByEnrollmentYear(Integer year) {
        return unitOfWork.getStudentGroupRepository().findByEnrollmentYear(year)
                .stream()
                .map(groupMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public StudentGroupDto activateGroup(Long groupId) {
        StudentGroup group = unitOfWork.getStudentGroupRepository().findById(groupId)
                .orElseThrow(() -> new RuntimeException("StudentGroup not found with id: " + groupId));
        
        group.setIsActive(true);
        group.setUpdatedAt(LocalDateTime.now());
        
        StudentGroup updatedGroup = unitOfWork.getStudentGroupRepository().save(group);
        return groupMapper.toDto(updatedGroup);
    }

    @Override
    public StudentGroupDto deactivateGroup(Long groupId) {
        StudentGroup group = unitOfWork.getStudentGroupRepository().findById(groupId)
                .orElseThrow(() -> new RuntimeException("StudentGroup not found with id: " + groupId));
        
        group.setIsActive(false);
        group.setUpdatedAt(LocalDateTime.now());
        
        StudentGroup updatedGroup = unitOfWork.getStudentGroupRepository().save(group);
        return groupMapper.toDto(updatedGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentGroupDto findByIdWithStudentCount(Long id) {
        StudentGroup group = unitOfWork.getStudentGroupRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("StudentGroup not found with id: " + id));
        
        StudentGroupDto dto = groupMapper.toDto(group);
        
        // Count students in this group
        long studentCount = unitOfWork.getStudentRepository().countByGroupId(id);
        dto.setCurrentStudentCount((int) studentCount);
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGroupDto> findGroupsWithStudents() {
        return unitOfWork.getStudentGroupRepository().findGroupsWithStudents()
                .stream()
                .map(group -> {
                    StudentGroupDto dto = groupMapper.toDto(group);
                    long studentCount = unitOfWork.getStudentRepository().countByGroupId(group.getId());
                    dto.setCurrentStudentCount((int) studentCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentGroupDto> findGroupsByTeacherId(Long teacherId) {
        // Find groups that are assigned to subjects taught by this teacher
        return unitOfWork.getStudentGroupRepository().findGroupsByTeacherId(teacherId)
                .stream()
                .map(group -> {
                    StudentGroupDto dto = groupMapper.toDto(group);
                    long studentCount = unitOfWork.getStudentRepository().countByGroupId(group.getId());
                    dto.setCurrentStudentCount((int) studentCount);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}
