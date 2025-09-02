package com.kursova.dal.repositories;

import com.kursova.dal.entities.Teacher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Teacher entity
 */
@Repository
public interface TeacherRepository extends BaseRepository<Teacher, Long> {

    /**
     * Find teacher by user ID
     */
    Optional<Teacher> findByUserId(Long userId);

    /**
     * Find teacher by username
     */
    @Query("SELECT t FROM Teacher t JOIN t.user u WHERE u.username = :username")
    Optional<Teacher> findByUsername(@Param("username") String username);

    /**
     * Find active teachers
     */
    List<Teacher> findByIsActiveTrueOrderByUserLastNameAsc();

    /**
     * Find teachers by academic title
     */
    List<Teacher> findByAcademicTitleContainingIgnoreCaseAndIsActiveTrue(String academicTitle);

    /**
     * Find teachers by department position
     */
    List<Teacher> findByDepartmentPositionContainingIgnoreCaseAndIsActiveTrue(String position);

    /**
     * Find teachers teaching specific subject
     */
    @Query("SELECT DISTINCT t FROM Teacher t JOIN t.subjects s WHERE s.id = :subjectId AND t.isActive = true")
    List<Teacher> findBySubjectId(@Param("subjectId") Long subjectId);

    /**
     * Search teachers by name
     */
    @Query("SELECT t FROM Teacher t JOIN t.user u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND t.isActive = true ORDER BY u.lastName, u.firstName")
    List<Teacher> searchByName(@Param("name") String name);

    /**
     * Search teachers by name or email
     */
    @Query("SELECT t FROM Teacher t JOIN t.user u WHERE " +
           "(LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND t.isActive = true ORDER BY u.lastName, u.firstName")
    List<Teacher> searchByNameOrEmail(@Param("searchTerm") String searchTerm);
}
