package com.kursova.dal.repositories;

import com.kursova.dal.entities.ArchivedStudentGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for ArchivedStudentGroup entities
 */
@Repository
public interface ArchivedStudentGroupRepository extends JpaRepository<ArchivedStudentGroup, Long> {

    /**
     * Find archived groups by group code
     */
    List<ArchivedStudentGroup> findByGroupCodeContainingIgnoreCase(String groupCode);

    /**
     * Find archived groups by group name
     */
    List<ArchivedStudentGroup> findByGroupNameContainingIgnoreCase(String groupName);

    /**
     * Find archived groups by original group ID
     */
    List<ArchivedStudentGroup> findByOriginalGroupId(Long originalGroupId);

    /**
     * Find archived groups by who archived them
     */
    List<ArchivedStudentGroup> findByArchivedBy(String archivedBy);

    /**
     * Find archived groups within date range
     */
    @Query("SELECT ag FROM ArchivedStudentGroup ag WHERE ag.archivedAt BETWEEN :startDate AND :endDate ORDER BY ag.archivedAt DESC")
    List<ArchivedStudentGroup> findByArchivedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find all archived groups ordered by archived date (newest first)
     */
    List<ArchivedStudentGroup> findAllByOrderByArchivedAtDesc();
}
