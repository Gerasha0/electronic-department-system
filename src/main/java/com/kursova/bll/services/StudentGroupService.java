package com.kursova.bll.services;

import com.kursova.bll.dto.StudentGroupDto;
import java.util.List;

/**
 * Service interface for Student Group management
 */
public interface StudentGroupService extends BaseService<StudentGroupDto, Long> {

    /**
     * Find all active groups
     */
    List<StudentGroupDto> findActiveGroups();

    /**
     * Search groups by name
     */
    List<StudentGroupDto> searchByName(String name);

    /**
     * Search groups by name or group code
     */
    List<StudentGroupDto> searchByNameOrCode(String searchTerm);

    /**
     * Find group by group code
     */
    StudentGroupDto findByGroupCode(String groupCode);

    /**
     * Check if group code exists
     */
    boolean existsByGroupCode(String groupCode);

    /**
     * Find groups by enrollment year
     */
    List<StudentGroupDto> findByEnrollmentYear(Integer year);

    /**
     * Activate group
     */
    StudentGroupDto activateGroup(Long groupId);

    /**
     * Deactivate group
     */
    StudentGroupDto deactivateGroup(Long groupId);

    /**
     * Get group with student count
     */
    StudentGroupDto findByIdWithStudentCount(Long id);

    /**
     * Find groups with students
     */
    List<StudentGroupDto> findGroupsWithStudents();
}
