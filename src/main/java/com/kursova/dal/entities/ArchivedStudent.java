package com.kursova.dal.entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Archived Student entity for storing deleted students
 */
@Entity
@Table(name = "archived_students")
public class ArchivedStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_student_id", nullable = false)
    private Long originalStudentId;

    @Column(name = "student_number", nullable = false)
    private String studentNumber;

    @Column(name = "enrollment_year")
    private Integer enrollmentYear;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "study_form")
    private StudyForm studyForm;

    @Column(name = "original_group_id")
    private Long originalGroupId;

    @Column(name = "group_code")
    private String groupCode;

    @Column(name = "group_name")
    private String groupName;

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
    public ArchivedStudent() {}

    public ArchivedStudent(Student originalStudent, String archivedBy, String archiveReason) {
        this.originalStudentId = originalStudent.getId();
        this.studentNumber = originalStudent.getStudentNumber();
        this.enrollmentYear = originalStudent.getEnrollmentYear();
        this.phoneNumber = originalStudent.getPhoneNumber();
        this.address = originalStudent.getAddress();
        this.studyForm = originalStudent.getStudyForm();
        
        if (originalStudent.getGroup() != null) {
            this.originalGroupId = originalStudent.getGroup().getId();
            this.groupCode = originalStudent.getGroup().getGroupCode();
            this.groupName = originalStudent.getGroup().getGroupName();
        }
        
        this.originalCreatedAt = originalStudent.getCreatedAt();
        this.originalUpdatedAt = originalStudent.getUpdatedAt();
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

    public Integer getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(Integer enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
