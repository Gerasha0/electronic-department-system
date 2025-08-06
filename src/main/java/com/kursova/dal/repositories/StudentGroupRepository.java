package com.kursova.dal.repositories;

import com.kursova.dal.entities.StudentGroup;
import com.kursova.dal.entities.StudyForm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for StudentGroup entity
 */
@Repository
public interface StudentGroupRepository extends BaseRepository<StudentGroup, Long> {
    
    /**
     * Find group by name
     */
    Optional<StudentGroup> findByGroupName(String groupName);
    
    /**
     * Find active groups
     */
    List<StudentGroup> findByIsActiveTrueOrderByGroupNameAsc();
    
    /**
     * Find groups by course year
     */
    List<StudentGroup> findByCourseYearAndIsActiveTrueOrderByGroupNameAsc(Integer courseYear);
    
    /**
     * Find groups by study form
     */
    List<StudentGroup> findByStudyFormAndIsActiveTrueOrderByGroupNameAsc(StudyForm studyForm);
    
    /**
     * Find groups by start year
     */
    List<StudentGroup> findByStartYearAndIsActiveTrueOrderByGroupNameAsc(Integer startYear);
    
    /**
     * Find groups by specialization
     */
    List<StudentGroup> findBySpecializationContainingIgnoreCaseAndIsActiveTrueOrderByGroupNameAsc(String specialization);
    
    /**
     * Check if group name exists
     */
    boolean existsByGroupName(String groupName);
    
    /**
     * Search groups by name
     */
    @Query("SELECT g FROM StudentGroup g WHERE " +
           "LOWER(g.groupName) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND g.isActive = true ORDER BY g.groupName")
    List<StudentGroup> searchByName(@Param("name") String name);
    
    /**
     * Find groups with available slots
     */
    @Query("SELECT g FROM StudentGroup g WHERE " +
           "g.isActive = true AND " +
           "(g.maxStudents IS NULL OR SIZE(g.students) < g.maxStudents) " +
           "ORDER BY g.groupName")
    List<StudentGroup> findGroupsWithAvailableSlots();
}
