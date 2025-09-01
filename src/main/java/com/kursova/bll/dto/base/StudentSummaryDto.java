package com.kursova.bll.dto.base;

/**
 * Lightweight DTO for student information in other DTOs
 * Reduces complexity of main DTOs
 */
public class StudentSummaryDto {
    private Long id;
    private Long userId;
    private String name;
    private String studentNumber;

    public StudentSummaryDto() {}

    public StudentSummaryDto(Long id, String name, String studentNumber) {
        this.id = id;
        this.name = name;
        this.studentNumber = studentNumber;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStudentNumber() { return studentNumber; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }
}
