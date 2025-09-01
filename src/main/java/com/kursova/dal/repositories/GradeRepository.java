package com.kursova.dal.repositories;

import com.kursova.dal.entities.Grade;
import com.kursova.dal.entities.GradeType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Grade entity
 */
@Repository
public interface GradeRepository extends BaseRepository<Grade, Long> {
    
    /**
     * Find grades by student ID
     */
    List<Grade> findByStudentIdOrderByGradeDateDesc(Long studentId);
    
    /**
     * Find grades by teacher ID
     */
    List<Grade> findByTeacherIdOrderByGradeDateDesc(Long teacherId);
    
    /**
     * Find grades by subject ID
     */
    List<Grade> findBySubjectIdOrderByGradeDateDesc(Long subjectId);
    
    /**
     * Find grades by student and subject
     */
    List<Grade> findByStudentIdAndSubjectIdOrderByGradeDateDesc(Long studentId, Long subjectId);
    
    /**
     * Find grades by student, subject and grade type
     */
    Optional<Grade> findByStudentIdAndSubjectIdAndGradeType(Long studentId, Long subjectId, GradeType gradeType);
    
    /**
     * Find final grades by student
     */
    List<Grade> findByStudentIdAndIsFinalTrueOrderBySubjectSubjectNameAsc(Long studentId);
    
    /**
     * Find grades by grade type
     */
    List<Grade> findByGradeTypeOrderByGradeDateDesc(GradeType gradeType);
    
    /**
     * Find grades in date range
     */
    List<Grade> findByGradeDateBetweenOrderByGradeDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find grade by id and fetch related student.user, teacher.user and subject to avoid lazy init issues
     */
    @Query("select g from Grade g " +
        "left join fetch g.student s " +
        "left join fetch s.user su " +
        "left join fetch g.teacher t " +
        "left join fetch t.user tu " +
        "left join fetch g.subject subj " +
        "where g.id = :id")
    Optional<Grade> findByIdWithRelations(@Param("id") Long id);
    
    /**
     * Calculate average grade for student in subject
     */
    @Query("SELECT AVG(g.gradeValue) FROM Grade g WHERE g.student.id = :studentId AND g.subject.id = :subjectId")
    Double getAverageGradeForStudentInSubject(@Param("studentId") Long studentId, @Param("subjectId") Long subjectId);
    
    /**
     * Calculate overall average grade for student
     */
    @Query("SELECT AVG(g.gradeValue) FROM Grade g WHERE g.student.id = :studentId AND g.isFinal = true")
    Double getOverallAverageGradeForStudent(@Param("studentId") Long studentId);
    
    /**
     * Find top performing students
     */
    @Query("SELECT g.student.id, AVG(g.gradeValue) as avgGrade FROM Grade g " +
           "WHERE g.isFinal = true GROUP BY g.student.id ORDER BY avgGrade DESC")
    List<Object[]> findTopPerformingStudents();
    
    /**
     * Count grades by teacher and subject
     */
    @Query("SELECT COUNT(g) FROM Grade g WHERE g.teacher.id = :teacherId AND g.subject.id = :subjectId")
    Long countGradesByTeacherAndSubject(@Param("teacherId") Long teacherId, @Param("subjectId") Long subjectId);

    /**
     * Find all grades and fetch related student.user, teacher.user and subject to avoid lazy init issues
     */
    @Query("select distinct g from Grade g " +
        "left join fetch g.student s " +
        "left join fetch s.user su " +
        "left join fetch g.teacher t " +
        "left join fetch t.user tu " +
        "left join fetch g.subject subj")
    List<Grade> findAllWithRelations();
}
