package com.kursova.dal.repositories;

import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.StudyForm;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Student entity
 */
@Repository
public interface StudentRepository extends BaseRepository<Student, Long> {

    /**
     * Find student by user ID
     */
    Optional<Student> findByUserId(Long userId);

    /**
     * Find student by username
     */
    @Query("SELECT s FROM Student s JOIN s.user u WHERE u.username = :username")
    Optional<Student> findByUsername(@Param("username") String username);

    /**
     * Find student by student number
     */
    Optional<Student> findByStudentNumber(String studentNumber);

    /**
     * Find active students
     */
    List<Student> findByIsActiveTrueOrderByUserLastNameAsc();

    /**
     * Find students by group
     */
    List<Student> findByGroupIdAndIsActiveTrueOrderByUserLastNameAsc(Long groupId);

    /**
     * Find students without group (active students not assigned to any group)
     */
    List<Student> findByGroupIsNullAndIsActiveTrueOrderByUserLastNameAsc();

    /**
     * Find all students by group (including inactive)
     */
    List<Student> findByGroupIdOrderByUserLastNameAsc(Long groupId);

    /**
     * Simple find by group ID
     */
    List<Student> findByGroupId(Long groupId);

    /**
     * Find students by study form
     */
    List<Student> findByStudyFormAndIsActiveTrueOrderByUserLastNameAsc(StudyForm studyForm);

    /**
     * Find students by enrollment year
     */
    List<Student> findByEnrollmentYearAndIsActiveTrueOrderByUserLastNameAsc(Integer enrollmentYear);

    /**
     * Search students by name
     */
    @Query("SELECT s FROM Student s JOIN s.user u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%')) " +
           "AND s.isActive = true ORDER BY u.lastName, u.firstName")
    List<Student> searchByName(@Param("name") String name);

    /**
     * Check if student number exists
     */
    boolean existsByStudentNumber(String studentNumber);

    /**
     * Count students in group
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.group.id = :groupId AND s.isActive = true")
    Long countByGroupId(@Param("groupId") Long groupId);

    /**
     * Find students taught by specific teacher
     */
    @Query("SELECT s FROM Student s WHERE s.isActive = true AND s.id IN (" +
           "SELECT DISTINCT g.student.id FROM Grade g " +
           "JOIN g.subject sub " +
           "JOIN sub.teachers t " +
           "WHERE t.id = :teacherId) " +
           "ORDER BY s.user.lastName, s.user.firstName")
    List<Student> findStudentsByTeacherId(@Param("teacherId") Long teacherId);
}
