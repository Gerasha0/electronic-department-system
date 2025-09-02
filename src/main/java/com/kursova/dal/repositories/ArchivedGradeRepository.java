package com.kursova.dal.repositories;

import com.kursova.dal.entities.ArchivedGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ArchivedGrade entities
 */
@Repository
public interface ArchivedGradeRepository extends JpaRepository<ArchivedGrade, Long> {

    /**
     * Find archived grades by original student ID
     */
    List<ArchivedGrade> findByOriginalStudentId(Long originalStudentId);

    /**
     * Find archived grades by student number
     */
    List<ArchivedGrade> findByStudentNumberContainingIgnoreCase(String studentNumber);

    /**
     * Find archived grades by original group ID
     */
    List<ArchivedGrade> findByOriginalGroupId(Long originalGroupId);

    /**
     * Find archived grades by group code
     */
    List<ArchivedGrade> findByGroupCodeContainingIgnoreCase(String groupCode);

    /**
     * Find archived grades by subject name
     */
    List<ArchivedGrade> findBySubjectNameContainingIgnoreCase(String subjectName);

    /**
     * Find archived grades by who archived them
     */
    List<ArchivedGrade> findByArchivedBy(String archivedBy);

    /**
     * Find archived grades within date range
     */
    @Query("SELECT ag FROM ArchivedGrade ag WHERE ag.archivedAt BETWEEN :startDate AND :endDate ORDER BY ag.archivedAt DESC")
    List<ArchivedGrade> findByArchivedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find all archived grades ordered by archived date (newest first)
     */
    List<ArchivedGrade> findAllByOrderByArchivedAtDesc();

    /**
     * Get average grade for archived student
     */
    @Query("SELECT AVG(ag.gradeValue) FROM ArchivedGrade ag WHERE ag.originalStudentId = :studentId")
    Double getAverageGradeForArchivedStudent(@Param("studentId") Long studentId);
}
