package com.kursova.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Teacher entity representing academic staff
 */
@Entity
@Table(name = "teachers")
public class Teacher {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "academic_title")
    private String academicTitle;
    
    @Column(name = "scientific_degree")
    private String scientificDegree;
    
    @Column(name = "department_position")
    private String departmentPosition;
    
    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Column(name = "office_number")
    private String officeNumber;
    
    @Lob
    @Column(name = "biography")
    private String biography;
    
    @Column(name = "hire_date")
    private LocalDateTime hireDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    private Set<Subject> subjects = new HashSet<>();
    
    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Grade> grades = new HashSet<>();
    
    // Constructors
    public Teacher() {}
    
    public Teacher(User user, String academicTitle, String departmentPosition) {
        this.user = user;
        this.academicTitle = academicTitle;
        this.departmentPosition = departmentPosition;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.hireDate = LocalDateTime.now();
    }
    
    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (hireDate == null) {
            hireDate = LocalDateTime.now();
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
    
    public LocalDateTime getHireDate() {
        return hireDate;
    }
    
    public void setHireDate(LocalDateTime hireDate) {
        this.hireDate = hireDate;
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
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public Set<Subject> getSubjects() {
        return subjects;
    }
    
    public void setSubjects(Set<Subject> subjects) {
        this.subjects = subjects;
    }
    
    public Set<Grade> getGrades() {
        return grades;
    }
    
    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
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
}
