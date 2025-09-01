package com.kursova.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Subject entity representing academic subjects/disciplines
 */
@Entity
@Table(name = "subjects")
public class Subject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subject_name", nullable = false)
    private String subjectName;

    @Column(name = "subject_code", unique = true)
    private String subjectCode;

    @Lob
    @Column(name = "description")
    private String description;

    @Column(name = "credits")
    private Integer credits;

    @Column(name = "hours_total")
    private Integer hoursTotal;

    @Column(name = "hours_lectures")
    private Integer hoursLectures;

    @Column(name = "hours_practical")
    private Integer hoursPractical;

    @Column(name = "hours_laboratory")
    private Integer hoursLaboratory;

    @Enumerated(EnumType.STRING)
    @Column(name = "assessment_type")
    private AssessmentType assessmentType;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "teacher_subjects",
        joinColumns = @JoinColumn(name = "subject_id"),
        inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private Set<Teacher> teachers = new HashSet<>();

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Grade> grades = new HashSet<>();

    // Constructors
    public Subject() {}

    public Subject(String subjectName, String subjectCode, Integer credits, AssessmentType assessmentType) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.credits = credits;
        this.assessmentType = assessmentType;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
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

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Integer getHoursTotal() {
        return hoursTotal;
    }

    public void setHoursTotal(Integer hoursTotal) {
        this.hoursTotal = hoursTotal;
    }

    public Integer getHoursLectures() {
        return hoursLectures;
    }

    public void setHoursLectures(Integer hoursLectures) {
        this.hoursLectures = hoursLectures;
    }

    public Integer getHoursPractical() {
        return hoursPractical;
    }

    public void setHoursPractical(Integer hoursPractical) {
        this.hoursPractical = hoursPractical;
    }

    public Integer getHoursLaboratory() {
        return hoursLaboratory;
    }

    public void setHoursLaboratory(Integer hoursLaboratory) {
        this.hoursLaboratory = hoursLaboratory;
    }

    public AssessmentType getAssessmentType() {
        return assessmentType;
    }

    public void setAssessmentType(AssessmentType assessmentType) {
        this.assessmentType = assessmentType;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    public Set<Teacher> getTeachers() {
        return teachers;
    }

    public void setTeachers(Set<Teacher> teachers) {
        this.teachers = teachers;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    // Helper methods
    public String getDisplayName() {
        StringBuilder name = new StringBuilder(subjectName);
        if (subjectCode != null && !subjectCode.isEmpty()) {
            name.append(" (").append(subjectCode).append(")");
        }
        if (credits != null) {
            name.append(" - ").append(credits).append(" кредитів");
        }
        return name.toString();
    }

    public String getWorkloadInfo() {
        StringBuilder info = new StringBuilder();
        if (hoursTotal != null) {
            info.append("Всього годин: ").append(hoursTotal);
        }
        if (hoursLectures != null && hoursLectures > 0) {
            info.append(", Лекції: ").append(hoursLectures);
        }
        if (hoursPractical != null && hoursPractical > 0) {
            info.append(", Практичні: ").append(hoursPractical);
        }
        if (hoursLaboratory != null && hoursLaboratory > 0) {
            info.append(", Лабораторні: ").append(hoursLaboratory);
        }
        return info.toString();
    }
}
