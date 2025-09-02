package com.kursova.bll.dto;

import com.kursova.dal.entities.EducationLevel;
import com.kursova.dal.entities.StudyForm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO for Student entity
 */
public class StudentDto {

    private Long id;

    @NotNull(message = "User information is required")
    private UserDto user;

    @NotBlank(message = "Student number is required")
    @Size(max = 20, message = "Student number must not exceed 20 characters")
    private String studentNumber;

    private Integer enrollmentYear;

    private Integer courseYear;
    private EducationLevel educationLevel;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 300, message = "Address must not exceed 300 characters")
    private String address;

    private StudyForm studyForm;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;

    private StudentGroupDto group;

    // Calculated fields
    private Double averageGrade; // Calculated based on grades

    // Constructors
    public StudentDto() {}

    public StudentDto(UserDto user, String studentNumber, Integer enrollmentYear, StudyForm studyForm) {
        this.user = user;
        this.studentNumber = studentNumber;
        this.enrollmentYear = enrollmentYear;
        this.studyForm = studyForm;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public String getStudentNumber() {
        return studentNumber;
    }

    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

    public Integer getCourseYear() {
        return courseYear;
    }

    public void setCourseYear(Integer courseYear) {
        this.courseYear = courseYear;
    }

    public EducationLevel getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(EducationLevel educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public StudyForm getStudyForm() {
        return studyForm;
    }

    public void setStudyForm(StudyForm studyForm) {
        this.studyForm = studyForm;
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

    public StudentGroupDto getGroup() {
        return group;
    }

    public void setGroup(StudentGroupDto group) {
        this.group = group;
    }

    public Integer getCourse() {
        return courseYear;
    }

    public void setCourse(Integer course) {
        this.courseYear = course;
    }

    public Double getAverageGrade() {
        return averageGrade;
    }

    public void setAverageGrade(Double averageGrade) {
        this.averageGrade = averageGrade;
    }

    // Helper methods
    public String getFullName() {
        return user != null ? user.getFullName() : "";
    }

    public String getDisplayInfo() {
        StringBuilder info = new StringBuilder();
        info.append(getFullName());
        if (studentNumber != null) {
            info.append(" (").append(studentNumber).append(")");
        }
        if (group != null) {
            info.append(" - ").append(group.getGroupName());
        }
        return info.toString();
    }

    @Override
    public String toString() {
        return "StudentDto{" +
                "id=" + id +
                ", fullName='" + getFullName() + '\'' +
                ", studentNumber='" + studentNumber + '\'' +
                ", enrollmentYear=" + enrollmentYear +
                ", studyForm=" + studyForm +
                ", isActive=" + isActive +
                '}';
    }
}
