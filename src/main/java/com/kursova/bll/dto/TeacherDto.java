package com.kursova.bll.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * DTO for Teacher entity
 */
public class TeacherDto {
    
    private Long id;
    
    @NotNull(message = "User information is required")
    private UserDto user;
    
    @Size(max = 100, message = "Academic title must not exceed 100 characters")
    private String academicTitle;
    
    @Size(max = 100, message = "Scientific degree must not exceed 100 characters")
    private String scientificDegree;
    
    @Size(max = 100, message = "Department position must not exceed 100 characters")
    private String departmentPosition;
    
    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;
    
    @Size(max = 20, message = "Office number must not exceed 20 characters")
    private String officeNumber;
    
    private String biography;
    
    private String hireDate;
    private Boolean isActive;
    private String createdAt;
    private String updatedAt;
    
    private List<SubjectDto> subjects;
    
    // Constructors
    public TeacherDto() {}
    
    public TeacherDto(UserDto user, String academicTitle, String departmentPosition) {
        this.user = user;
        this.academicTitle = academicTitle;
        this.departmentPosition = departmentPosition;
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
    
    public String getAcademicTitle() {
        return academicTitle;
    }
    
    public void setAcademicTitle(String academicTitle) {
        this.academicTitle = academicTitle;
    }
    
    public String getScientificDegree() {
        return scientificDegree;
    }
    
    public void setScientificDegree(String scientificDegree) {
        this.scientificDegree = scientificDegree;
    }
    
    public String getDepartmentPosition() {
        return departmentPosition;
    }
    
    public void setDepartmentPosition(String departmentPosition) {
        this.departmentPosition = departmentPosition;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getOfficeNumber() {
        return officeNumber;
    }
    
    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }
    
    public String getBiography() {
        return biography;
    }
    
    public void setBiography(String biography) {
        this.biography = biography;
    }
    
    public String getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(String hireDate) {
        this.hireDate = hireDate;
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
    
    public List<SubjectDto> getSubjects() {
        return subjects;
    }
    
    public void setSubjects(List<SubjectDto> subjects) {
        this.subjects = subjects;
    }
    
    // Helper methods
    public String getFullName() {
        return user != null ? user.getFullName() : "";
    }
    
    public String getDisplayTitle() {
        StringBuilder title = new StringBuilder();
        if (academicTitle != null && !academicTitle.isEmpty()) {
            title.append(academicTitle).append(" ");
        }
        title.append(getFullName());
        return title.toString();
    }
    
    @Override
    public String toString() {
        return "TeacherDto{" +
                "id=" + id +
                ", fullName='" + getFullName() + '\'' +
                ", academicTitle='" + academicTitle + '\'' +
                ", departmentPosition='" + departmentPosition + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
