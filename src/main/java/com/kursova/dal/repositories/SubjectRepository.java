package com.kursova.dal.repositories;

import com.kursova.dal.entities.Subject;
import com.kursova.dal.entities.AssessmentType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Subject entity
 */
@Repository
public interface SubjectRepository extends BaseRepository<Subject, Long> {
    
    /**
     * Find subject by code
     */
    Optional<Subject> findBySubjectCode(String subjectCode);
    
    /**
     * Find active subjects
     */
    List<Subject> findByIsActiveTrueOrderBySubjectNameAsc();
    
    /**
     * Find subjects by assessment type
     */
    List<Subject> findByAssessmentTypeAndIsActiveTrueOrderBySubjectNameAsc(AssessmentType assessmentType);
    
    /**
     * Find subjects by semester
     */
    List<Subject> findBySemesterAndIsActiveTrueOrderBySubjectNameAsc(Integer semester);
    
    /**
     * Find subjects by credits
     */
    List<Subject> findByCreditsAndIsActiveTrueOrderBySubjectNameAsc(Integer credits);
    
    /**
     * Find subjects taught by teacher
     */
    @Query("SELECT DISTINCT s FROM Subject s JOIN s.teachers t WHERE t.id = :teacherId AND s.isActive = true")
    List<Subject> findByTeacherId(@Param("teacherId") Long teacherId);
    
    /**
     * Search subjects by name
     */
    @Query("SELECT s FROM Subject s WHERE " +
           "LOWER(s.subjectName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND s.isActive = true ORDER BY s.subjectName")
    List<Subject> searchByName(@Param("name") String name);
    
    /**
     * Check if subject code exists
     */
    boolean existsBySubjectCode(String subjectCode);
    
    /**
     * Find subjects with grades for student
     */
    @Query("SELECT DISTINCT s FROM Subject s JOIN s.grades g WHERE g.student.id = :studentId")
    List<Subject> findSubjectsWithGradesForStudent(@Param("studentId") Long studentId);
}
