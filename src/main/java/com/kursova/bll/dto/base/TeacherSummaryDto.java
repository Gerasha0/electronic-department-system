package com.kursova.bll.dto.base;

/**
 * Lightweight DTO for teacher information in other DTOs
 * Reduces complexity of main DTOs
 */
public class TeacherSummaryDto {
    private Long id;
    private String name;
    private String academicTitle;

    public TeacherSummaryDto() {}

    public TeacherSummaryDto(Long id, String name, String academicTitle) {
        this.id = id;
        this.name = name;
        this.academicTitle = academicTitle;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAcademicTitle() { return academicTitle; }
    public void setAcademicTitle(String academicTitle) { this.academicTitle = academicTitle; }
}
