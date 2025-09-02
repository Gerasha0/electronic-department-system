package com.kursova.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Archived Grade entity for storing grades of deleted students
 */
@Entity
@Table(name = "archived_grades")
public class ArchivedGrade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_grade_id", nullable = false)
    private Long originalGradeId;

    @Column(name = "original_student_id", nullable = false)
    private Long originalStudentId;

    @Column(name = "student_number")
    private String studentNumber;

    @Column(name = "original_subject_id")
    private Long originalSubjectId;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "grade_value")
    private Integer gradeValue;

    @Column(name = "original_group_id")
    private Long originalGroupId;

    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "original_created_at")
    private LocalDateTime originalCreatedAt;

    @Column(name = "original_updated_at")
    private LocalDateTime originalUpdatedAt;

    @Column(name = "archived_at", nullable = false)
    private LocalDateTime archivedAt;

    @Column(name = "archived_by")
    private String archivedBy;

    @Column(name = "archive_reason")
    private String archiveReason;

    // Constructors
    public ArchivedGrade() {}

    public ArchivedGrade(Grade originalGrade, String archivedBy, String archiveReason) {
        this.originalGradeId = originalGrade.getId();
        this.originalStudentId = originalGrade.getStudent().getId();
        this.studentNumber = originalGrade.getStudent().getStudentNumber();
        
        if (originalGrade.getSubject() != null) {
            this.originalSubjectId = originalGrade.getSubject().getId();
            this.subjectName = originalGrade.getSubject().getSubjectName();
        }
        
        this.gradeValue = originalGrade.getGradeValue();
        
        if (originalGrade.getStudent().getGroup() != null) {
            this.originalGroupId = originalGrade.getStudent().getGroup().getId();
            this.groupCode = originalGrade.getStudent().getGroup().getGroupCode();
        }
        
        this.originalCreatedAt = originalGrade.getCreatedAt();
        this.originalUpdatedAt = originalGrade.getUpdatedAt();
        this.archivedAt = LocalDateTime.now();
        this.archivedBy = archivedBy;
        this.archiveReason = archiveReason;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOriginalGradeId() {
        return originalGradeId;
    }

    public void setOriginalGradeId(Long originalGradeId) {
        this.originalGradeId = originalGradeId;
    }

    public Long getOriginalStudentId() {
        return originalStudentId;
    }

    public void setOriginalStudentId(Long originalStudentId) {
        this.originalStudentId = originalStudentId;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Long getOriginalSubjectId() {
        return originalSubjectId;
    }

    public void setOriginalSubjectId(Long originalSubjectId) {
        this.originalSubjectId = originalSubjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public Integer getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(Integer gradeValue) {
        this.gradeValue = gradeValue;
    }

    public Long getOriginalGroupId() {
        return originalGroupId;
    }

    public void setOriginalGroupId(Long originalGroupId) {
        this.originalGroupId = originalGroupId;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public LocalDateTime getOriginalCreatedAt() {
        return originalCreatedAt;
    }

    public void setOriginalCreatedAt(LocalDateTime originalCreatedAt) {
        this.originalCreatedAt = originalCreatedAt;
    }

    public LocalDateTime getOriginalUpdatedAt() {
        return originalUpdatedAt;
    }

    public void setOriginalUpdatedAt(LocalDateTime originalUpdatedAt) {
        this.originalUpdatedAt = originalUpdatedAt;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public String getArchivedBy() {
        return archivedBy;
    }

    public void setArchivedBy(String archivedBy) {
        this.archivedBy = archivedBy;
    }

    public String getArchiveReason() {
        return archiveReason;
    }

    public void setArchiveReason(String archiveReason) {
        this.archiveReason = archiveReason;
    }
}
