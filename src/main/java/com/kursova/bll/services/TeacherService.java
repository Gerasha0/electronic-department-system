package com.kursova.bll.services;

import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.dto.UserDto;

import java.util.List;

/**
 * Service interface for Teacher operations
 */
public interface TeacherService extends BaseService<TeacherDto, Long> {
    
    /**
     * Find teacher by user ID
     */
    TeacherDto findByUserId(Long userId);
    
    /**
     * Find teacher by username
     */
    TeacherDto findByUsername(String username);
    
    /**
     * Find all active teachers
     */
    List<TeacherDto> findActiveTeachers();
    
    /**
     * Search teachers by name
     */
    List<TeacherDto> searchByName(String name);
    
    /**
     * Find teachers by academic title
     */
    List<TeacherDto> findByAcademicTitle(String academicTitle);
    
    /**
     * Find teachers by department position
     */
    List<TeacherDto> findByDepartmentPosition(String position);
    
    /**
     * Find teachers teaching specific subject
     */
    List<TeacherDto> findBySubjectId(Long subjectId);
    
    /**
     * Create teacher with user information
     */
    TeacherDto createWithUser(UserDto userDto, String password, TeacherDto teacherDto);
    
    /**
     * Assign subject to teacher
     */
    TeacherDto assignSubject(Long teacherId, Long subjectId);
    
    /**
     * Remove subject from teacher
     */
    TeacherDto removeSubject(Long teacherId, Long subjectId);
    
    /**
     * Activate teacher
     */
    TeacherDto activateTeacher(Long teacherId);
    
    /**
     * Deactivate teacher
     */
    TeacherDto deactivateTeacher(Long teacherId);
}
