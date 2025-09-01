package com.kursova.bll.dto;

import com.kursova.dal.entities.AssessmentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for Subject entity
 */
public class SubjectDto {

    private Long id;

    @NotBlank(message = "Subject name is required")
    @Size(max = 200, message = "Subject name must not exceed 200 characters")
    private String subjectName;

    @Size(max = 20, message = "Subject code must not exceed 20 characters")
    private String subjectCode;

    private String description;

    @Positive(message = "Credits must be positive")
    private Integer credits;

    @Positive(message = "Total hours must be positive")
    private Integer hoursTotal;

    private Integer hoursLectures;
    private Integer hoursPractical;
    private Integer hoursLaboratory;

    @NotNull(message = "Assessment type is required")
    private AssessmentType assessmentType;

    private Integer semester;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    private List<TeacherDto> teachers;

    // Constructors
    public SubjectDto() {}

    public SubjectDto(String subjectName, String subjectCode, Integer credits, AssessmentType assessmentType) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.credits = credits;
        this.assessmentType = assessmentType;
        this.isActive = true;
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

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<TeacherDto> getTeachers() {
        return teachers;
    }

    public void setTeachers(List<TeacherDto> teachers) {
        this.teachers = teachers;
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

    @Override
    public String toString() {
        return "SubjectDto{" +
                "id=" + id +
                ", subjectName='" + subjectName + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", credits=" + credits +
                ", assessmentType=" + assessmentType +
                ", isActive=" + isActive +
                '}';
    }
}
