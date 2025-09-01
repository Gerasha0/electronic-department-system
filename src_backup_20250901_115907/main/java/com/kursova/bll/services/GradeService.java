package com.kursova.bll.services;

import com.kursova.bll.dto.GradeDto;
import com.kursova.dal.entities.GradeType;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for Grade operations
 */
public interface GradeService extends BaseService<GradeDto, Long> {
    
    /**
     * Find grades by student ID
     */
    List<GradeDto> findByStudentId(Long studentId);
    
    /**
     * Find grades by teacher ID
     */
    List<GradeDto> findByTeacherId(Long teacherId);
    
    /**
     * Find grades by subject ID
     */
    List<GradeDto> findBySubjectId(Long subjectId);
    
    /**
     * Find grades by student and subject
     */
    List<GradeDto> findByStudentAndSubject(Long studentId, Long subjectId);
    
    /**
     * Find grade by student, subject and grade type
     */
    GradeDto findByStudentSubjectAndType(Long studentId, Long subjectId, GradeType gradeType);
    
    /**
     * Find final grades by student
     */
    List<GradeDto> findFinalGradesByStudent(Long studentId);
    
    /**
     * Find grades by grade type
     */
    List<GradeDto> findByGradeType(GradeType gradeType);
    
    /**
     * Find grades in date range
     */
    List<GradeDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Calculate average grade for student in subject
     */
    Double getAverageGradeForStudentInSubject(Long studentId, Long subjectId);
    
    /**
     * Calculate overall average grade for student
     */
    Double getOverallAverageGradeForStudent(Long studentId);
    
    /**
     * Get top performing students
     */
    List<Object[]> getTopPerformingStudents();
    
    /**
     * Count grades by teacher and subject
     */
    Long countGradesByTeacherAndSubject(Long teacherId, Long subjectId);
    
    /**
     * Create grade with validation
     */
    GradeDto createGradeWithValidation(Long studentId, Long teacherId, Long subjectId, 
                                      Integer gradeValue, GradeType gradeType, String comments);
    
    /**
     * Update grade with validation
     */
    GradeDto updateGradeWithValidation(Long gradeId, Integer gradeValue, String comments);
    
    /**
     * Mark grade as final
     */
    GradeDto markAsFinal(Long gradeId);
}
