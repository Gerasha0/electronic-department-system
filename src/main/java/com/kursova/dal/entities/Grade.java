package com.kursova.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Grade entity representing student grades
 */
@Entity
@Table(name = "grades", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "subject_id", "grade_type"})
})
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "grade_value", nullable = false)
    private Integer gradeValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_type", nullable = false)
    private GradeType gradeType;

    @Enumerated(EnumType.STRING)
    @Column(name = "grade_category_enum", nullable = false)
    private GradeCategory gradeCategoryEnum;

    @Column(name = "grade_date", nullable = false)
    private LocalDateTime gradeDate;

    @Lob
    @Column(name = "comments")
    private String comments;

    @Column(name = "is_final")
    private Boolean isFinal = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    // Constructors
    public Grade() {}

    public Grade(Student student, Teacher teacher, Subject subject, Integer gradeValue, GradeType gradeType) {
        this.student = student;
        this.teacher = teacher;
        this.subject = subject;
        this.gradeValue = gradeValue;
        this.gradeType = gradeType;
        this.gradeCategoryEnum = getCategoryFromGradeType(gradeType); // Auto-set category
        this.gradeDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (gradeDate == null) {
            gradeDate = LocalDateTime.now();
        }
        // Auto-set category if not set
        if (gradeCategoryEnum == null && gradeType != null) {
            gradeCategoryEnum = getCategoryFromGradeType(gradeType);
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public GradeCategory getGradeCategoryEnum() {
        return gradeCategoryEnum;
    }

    public void setGradeCategoryEnum(GradeCategory gradeCategoryEnum) {
        this.gradeCategoryEnum = gradeCategoryEnum;
    }

    // Backward compatibility method
    public String getGradeCategory() {
        return gradeCategoryEnum != null ? gradeCategoryEnum.getDisplayName() : null;
    }

    // Backward compatibility method
    public void setGradeCategory(String gradeCategory) {
        // This method can be used for migration purposes
        if (gradeCategory != null) {
            switch (gradeCategory) {
                case "Поточний контроль" -> this.gradeCategoryEnum = GradeCategory.CURRENT_CONTROL;
                case "Підсумковий контроль" -> this.gradeCategoryEnum = GradeCategory.FINAL_CONTROL;
                case "Перездача" -> this.gradeCategoryEnum = GradeCategory.RETAKE;
                case "Відпрацювання" -> this.gradeCategoryEnum = GradeCategory.MAKEUP;
                default -> {
                    // Try to determine category from grade type for backward compatibility
                    if (this.gradeType != null) {
                        this.gradeCategoryEnum = getCategoryFromGradeType(this.gradeType);
                    }
                }
            }
        }
    }

    public LocalDateTime getGradeDate() {
        return gradeDate;
    }

    public void setGradeDate(LocalDateTime gradeDate) {
        this.gradeDate = gradeDate;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Boolean getIsFinal() {
        return isFinal;
    }

    public void setIsFinal(Boolean isFinal) {
        this.isFinal = isFinal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    // Helper methods
    public String getGradeLetter() {
        if (gradeValue >= 90) return "A";
        if (gradeValue >= 80) return "B";
        if (gradeValue >= 70) return "C";
        if (gradeValue >= 60) return "D";
        return "F";
    }

    public String getGradeStatus() {
        return gradeValue >= 60 ? "Зараховано" : "Не зараховано";
    }

    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder();
        info.append(gradeValue).append(" (").append(getGradeLetter()).append(")");
        if (gradeType != null) {
            info.append(" - ").append(gradeType.getDisplayName());
        }
        return info.toString();
    }

    /**
     * Helper method to determine grade category from grade type for backward compatibility
     */
    private GradeCategory getCategoryFromGradeType(GradeType gradeType) {
        if (gradeType == null) return GradeCategory.CURRENT_CONTROL;
        
        return switch (gradeType) {
            case LABORATORY_WORK, PRACTICAL_WORK, SEMINAR, CONTROL_WORK, 
                 MODULE_WORK, HOMEWORK, INDIVIDUAL_WORK, CURRENT_MAKEUP -> 
                 GradeCategory.CURRENT_CONTROL;
            
            case EXAM, CREDIT, DIFFERENTIATED_CREDIT, COURSE_WORK, 
                 QUALIFICATION_WORK, STATE_EXAM, ATTESTATION -> 
                 GradeCategory.FINAL_CONTROL;
            
            case RETAKE_EXAM, RETAKE_CREDIT, RETAKE_WORK -> 
                 GradeCategory.RETAKE;
            
            case MAKEUP_LESSON, MAKEUP_WORK, ADDITIONAL_TASK -> 
                 GradeCategory.MAKEUP;
        };
    }
}
