package com.kursova.bll.dto;

import com.kursova.dal.entities.StudyForm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for StudentGroup entity
 */
public class StudentGroupDto {

    private Long id;

    @NotBlank(message = "Group name is required")
    @Size(max = 20, message = "Group name must not exceed 20 characters")
    private String groupName;

    @NotNull(message = "Course year is required")
    private Integer courseYear;

    private StudyForm studyForm;
    private Integer maxStudents;

    @Size(max = 200, message = "Specialization must not exceed 200 characters")
    private String specialization;

    private Integer startYear;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    private List<StudentDto> students;
    private Integer currentStudentCount;

    // Constructors
    public StudentGroupDto() {}

    public StudentGroupDto(String groupName, Integer courseYear, StudyForm studyForm, Integer startYear) {
        this.groupName = groupName;
        this.courseYear = courseYear;
        this.studyForm = studyForm;
        this.startYear = startYear;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(Integer courseYear) {
        this.courseYear = courseYear;
    }

    public StudyForm getStudyForm() {
        return studyForm;
    }

    public void setStudyForm(StudyForm studyForm) {
        this.studyForm = studyForm;
    }

    public Integer getMaxStudents() {
        return maxStudents;
    }

    public void setMaxStudents(Integer maxStudents) {
        this.maxStudents = maxStudents;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public Integer getStartYear() {
        return startYear;
    }

    public void setStartYear(Integer startYear) {
        this.startYear = startYear;
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

    public List<StudentDto> getStudents() {
        return students;
    }

    public void setStudents(List<StudentDto> students) {
        this.students = students;
    }

    public Integer getCurrentStudentCount() {
        return currentStudentCount;
    }

    public void setCurrentStudentCount(Integer currentStudentCount) {
        this.currentStudentCount = currentStudentCount;
    }

    // Helper methods
    public boolean canAddStudent() {
        return maxStudents == null || getCurrentStudentCount() < maxStudents;
    }

    public String getDisplayName() {
        StringBuilder name = new StringBuilder(groupName);
        if (courseYear != null) {
            name.append(" (").append(courseYear).append(" курс)");
        }
        return name.toString();
    }

    @Override
    public String toString() {
        return "StudentGroupDto{" +
                "id=" + id +
                ", groupName='" + groupName + '\'' +
                ", courseYear=" + courseYear +
                ", studyForm=" + studyForm +
                ", currentStudentCount=" + currentStudentCount +
                ", isActive=" + isActive +
                '}';
    }
}
