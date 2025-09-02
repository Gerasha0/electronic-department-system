package com.kursova.dal.repositories;

import com.kursova.dal.entities.ArchivedStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ArchivedStudent entities
 */
@Repository
public interface ArchivedStudentRepository extends JpaRepository<ArchivedStudent, Long> {

    /**
     * Find archived students by student number
     */
    List<ArchivedStudent> findByStudentNumberContainingIgnoreCase(String studentNumber);

    /**
     * Find archived students by original student ID
     */
    List<ArchivedStudent> findByOriginalStudentId(Long originalStudentId);

    /**
     * Find archived students by original group ID
     */
    List<ArchivedStudent> findByOriginalGroupId(Long originalGroupId);

    /**
     * Find archived students by group code
     */
    List<ArchivedStudent> findByGroupCodeContainingIgnoreCase(String groupCode);

    /**
     * Find archived students by who archived them
     */
    List<ArchivedStudent> findByArchivedBy(String archivedBy);

    /**
     * Find archived students within date range
     */
    @Query("SELECT as FROM ArchivedStudent as WHERE as.archivedAt BETWEEN :startDate AND :endDate ORDER BY as.archivedAt DESC")
    List<ArchivedStudent> findByArchivedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find all archived students ordered by archived date (newest first)
     */
    List<ArchivedStudent> findAllByOrderByArchivedAtDesc();
}
