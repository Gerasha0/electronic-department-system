package com.kursova.bll.services;

import com.kursova.dal.entities.ArchivedGrade;
import com.kursova.dal.entities.ArchivedStudent;
import com.kursova.dal.entities.ArchivedStudentGroup;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for archive operations
 */
public interface ArchiveService {

    /**
     * Archive a student group and all related data
     */
    void archiveStudentGroup(Long groupId, String archivedBy, String reason);

    /**
     * Archive a student and all related data
     */
    void archiveStudent(Long studentId, String archivedBy, String reason);

    /**
     * Get all archived groups
     */
    List<ArchivedStudentGroup> getAllArchivedGroups();

    /**
     * Get all archived students
     */
    List<ArchivedStudent> getAllArchivedStudents();

    /**
     * Get all archived grades
     */
    List<ArchivedGrade> getAllArchivedGrades();

    /**
     * Search archived groups by name or code
     */
    List<ArchivedStudentGroup> searchArchivedGroups(String searchTerm);

    /**
     * Search archived students by student number
     */
    List<ArchivedStudent> searchArchivedStudents(String searchTerm);

    /**
     * Get archived students by original group ID
     */
    List<ArchivedStudent> getArchivedStudentsByGroupId(Long originalGroupId);

    /**
     * Get archived grades by original student ID
     */
    List<ArchivedGrade> getArchivedGradesByStudentId(Long originalStudentId);

    /**
     * Get archived data within date range
     */
    List<ArchivedStudentGroup> getArchivedGroupsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Get statistics about archived data
     */
    ArchiveStatistics getArchiveStatistics();

    /**
     * Inner class for archive statistics
     */
    class ArchiveStatistics {
        private long totalArchivedGroups;
        private long totalArchivedStudents;
        private long totalArchivedGrades;
        private LocalDateTime lastArchiveDate;

        public ArchiveStatistics(long totalArchivedGroups, long totalArchivedStudents, 
                               long totalArchivedGrades, LocalDateTime lastArchiveDate) {
            this.totalArchivedGroups = totalArchivedGroups;
            this.totalArchivedStudents = totalArchivedStudents;
            this.totalArchivedGrades = totalArchivedGrades;
            this.lastArchiveDate = lastArchiveDate;
        }

        // Getters
        public long getTotalArchivedGroups() { return totalArchivedGroups; }
        public long getTotalArchivedStudents() { return totalArchivedStudents; }
        public long getTotalArchivedGrades() { return totalArchivedGrades; }
        public LocalDateTime getLastArchiveDate() { return lastArchiveDate; }
    }
}
