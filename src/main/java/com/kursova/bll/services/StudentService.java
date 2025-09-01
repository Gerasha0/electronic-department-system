package com.kursova.bll.services;

import com.kursova.bll.dto.StudentDto;
import java.util.List;

/**
 * Service interface for Student management
 */
public interface StudentService extends BaseService<StudentDto, Long> {

    /**
     * Find all active students
     */
    List<StudentDto> findActiveStudents();

    /**
     * Find student by user ID
     */
    StudentDto findByUserId(Long userId);

    /**
     * Search students by name
     */
    List<StudentDto> searchByName(String name);

    /**
     * Find students by group
     */
    List<StudentDto> findByGroup(Long groupId);

    /**
     * Find students by enrollment year
     */
    List<StudentDto> findByEnrollmentYear(Integer year);

    /**
     * Calculate average grade for student
     */
    Double calculateAverageGrade(Long studentId);

    /**
     * Get student with calculated course (based on enrollment year)
     */
    StudentDto findByIdWithCalculatedData(Long id);

    /**
     * Find students by group ID
     */
    List<StudentDto> findByGroupId(Long groupId);

    /**
     * Activate student account
     */
    StudentDto activateStudent(Long studentId);

    /**
     * Deactivate student account
     */
    StudentDto deactivateStudent(Long studentId);
}
