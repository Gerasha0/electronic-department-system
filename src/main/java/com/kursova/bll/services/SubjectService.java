package com.kursova.bll.services;

import com.kursova.bll.dto.SubjectDto;
import com.kursova.dal.entities.AssessmentType;

import java.util.List;

/**
 * Service interface for Subject operations
 */
public interface SubjectService extends BaseService<SubjectDto, Long> {

    /**
     * Find subject by code
     */
    SubjectDto findBySubjectCode(String subjectCode);

    /**
     * Find all active subjects
     */
    List<SubjectDto> findActiveSubjects();

    /**
     * Search subjects by name
     */
    List<SubjectDto> searchByName(String name);

    /**
     * Find subjects by assessment type
     */
    List<SubjectDto> findByAssessmentType(AssessmentType assessmentType);

    /**
     * Find subjects by semester
     */
    List<SubjectDto> findBySemester(Integer semester);

    /**
     * Find subjects by credits
     */
    List<SubjectDto> findByCredits(Integer credits);

    /**
     * Find subjects taught by teacher
     */
    List<SubjectDto> findByTeacherId(Long teacherId);

    /**
     * Check if subject code exists
     */
    boolean existsBySubjectCode(String subjectCode);

    /**
     * Find subjects with grades for student
     */
    List<SubjectDto> findSubjectsWithGradesForStudent(Long studentId);

    /**
     * Assign teacher to subject
     */
    void assignTeacher(Long subjectId, Long teacherId);

    /**
     * Remove teacher from subject
     */
    void removeTeacher(Long subjectId, Long teacherId);

    /**
     * Activate subject
     */
    SubjectDto activateSubject(Long subjectId);

    /**
     * Deactivate subject
     */
    SubjectDto deactivateSubject(Long subjectId);
}
