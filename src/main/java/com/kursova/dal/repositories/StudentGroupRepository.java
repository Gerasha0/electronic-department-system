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

    /**
     * Find group by group code
     */
    Optional<StudentGroup> findByGroupCode(String groupCode);

    /**
     * Check if group code exists
     */
    boolean existsByGroupCode(String groupCode);

    /**
     * Find groups by enrollment year
     */
    List<StudentGroup> findByEnrollmentYear(Integer enrollmentYear);

    /**
     * Find active groups (simple method)
     */
    List<StudentGroup> findByIsActiveTrue();

    /**
     * Search groups by name (case insensitive)
     */
    List<StudentGroup> findByGroupNameContainingIgnoreCase(String name);

    /**
     * Search groups by name or group code (case insensitive)
     */
    List<StudentGroup> findByGroupNameContainingIgnoreCaseOrGroupCodeContainingIgnoreCase(String name, String code);

    /**
     * Find groups that have students
     */
    @Query("SELECT DISTINCT g FROM StudentGroup g JOIN g.students s WHERE g.isActive = true ORDER BY g.groupName")
    List<StudentGroup> findGroupsWithStudents();

    /**
     * Find groups taught by a specific teacher (through subjects)
     */
    @Query("SELECT DISTINCT g FROM StudentGroup g " +
           "JOIN g.subjects s " +
           "JOIN s.teachers t " +
           "WHERE t.id = :teacherId AND g.isActive = true " +
           "ORDER BY g.groupName")
    List<StudentGroup> findGroupsByTeacherId(@Param("teacherId") Long teacherId);
}
