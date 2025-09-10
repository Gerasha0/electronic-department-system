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

    @Column(name = "student_name")
    private String studentName;

    @Column(name = "original_subject_id")
    private Long originalSubjectId;

    @Column(name = "subject_name")
    private String subjectName;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_category_enum")
    private GradeCategory gradeCategoryEnum;

    @Column(name = "grade_value")
    private Integer gradeValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type")
    private GradeType gradeType;

    @Column(name = "comments", columnDefinition = "TEXT")
    private String comments;

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
        
        // Set student full name
        if (originalGrade.getStudent().getUser() != null) {
            this.studentName = originalGrade.getStudent().getUser().getFullName();
        }
        
        if (originalGrade.getSubject() != null) {
            this.originalSubjectId = originalGrade.getSubject().getId();
            this.subjectName = originalGrade.getSubject().getSubjectName();
        }
        
        this.gradeCategoryEnum = originalGrade.getGradeCategoryEnum();
        this.comments = originalGrade.getComments();
        
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
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

    public GradeCategory getGradeCategoryEnum() {
        return gradeCategoryEnum;
    }

    public void setGradeCategoryEnum(GradeCategory gradeCategoryEnum) {
        this.gradeCategoryEnum = gradeCategoryEnum;
    }

    // For backward compatibility
    public String getGradeCategory() {
        return gradeCategoryEnum != null ? gradeCategoryEnum.getDisplayName() : null;
    }

    // For backward compatibility
    public void setGradeCategory(String gradeCategory) {
        // Convert to enum for new system
        if (gradeCategory != null) {
            switch (gradeCategory) {
                case "Поточний контроль" -> this.gradeCategoryEnum = GradeCategory.CURRENT_CONTROL;
                case "Підсумковий контроль" -> this.gradeCategoryEnum = GradeCategory.FINAL_CONTROL;
                case "Перездача" -> this.gradeCategoryEnum = GradeCategory.RETAKE;
                case "Відпрацювання" -> this.gradeCategoryEnum = GradeCategory.MAKEUP;
                default -> this.gradeCategoryEnum = GradeCategory.CURRENT_CONTROL;
            }
        }
    }

    public Integer getGradeValue() {
        return gradeValue;
    }

    public void setGradeValue(Integer gradeValue) {
        this.gradeValue = gradeValue;
    }

    public GradeType getGradeType() {
        return gradeType;
    }

    public void setGradeType(GradeType gradeType) {
        this.gradeType = gradeType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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
