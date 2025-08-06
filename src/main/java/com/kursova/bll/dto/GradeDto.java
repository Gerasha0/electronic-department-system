package com.kursova.bll.dto;

import com.kursova.dal.entities.GradeType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for Grade entity
 */
public class GradeDto {
    
    private Long id;
    
    @NotNull(message = "Grade value is required")
    @Min(value = 0, message = "Grade value must be at least 0")
    @Max(value = 100, message = "Grade value must not exceed 100")
    private Integer gradeValue;
    
    @NotNull(message = "Grade type is required")
    private GradeType gradeType;
    
    private String gradeDate;
    private String comments;
    private Boolean isFinal;
    private String createdAt;
    private String updatedAt;
    
    // Related entities as simple references
    private Long studentId;
    private String studentName;
    private String studentNumber;
    
    private Long teacherId;
    private String teacherName;
    
    private Long subjectId;
    private String subjectName;
    private String subjectCode;
    
    // Constructors
    public GradeDto() {}
    
    public GradeDto(Integer gradeValue, GradeType gradeType, Long studentId, Long teacherId, Long subjectId) {
        this.gradeValue = gradeValue;
        this.gradeType = gradeType;
        this.studentId = studentId;
        this.teacherId = teacherId;
        this.subjectId = subjectId;
        this.isFinal = false;
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
    
    public String getGradeDate() {
        return gradeDate;
    }
    
    public void setGradeDate(String gradeDate) {
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
    
    public Long getStudentId() {
        return studentId;
    }
    
    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }
    
    public String getStudentName() {
        return studentName;
    }
    
    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }
    
    public String getStudentNumber() {
        return studentNumber;
    }
    
    public void setStudentNumber(String studentNumber) {
        this.studentNumber = studentNumber;
    }
    
    public Long getTeacherId() {
        return teacherId;
    }
    
    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }
    
    public String getTeacherName() {
        return teacherName;
    }
    
    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }
    
    public Long getSubjectId() {
        return subjectId;
    }
    
    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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
    
    @Override
    public String toString() {
        return "GradeDto{" +
                "id=" + id +
                ", gradeValue=" + gradeValue +
                ", gradeType=" + gradeType +
                ", studentName='" + studentName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", isFinal=" + isFinal +
                '}';
    }
}
