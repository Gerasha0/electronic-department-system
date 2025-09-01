package com.kursova.bll.dto.base;

/**
 * Lightweight DTO for subject information in other DTOs
 * Reduces complexity of main DTOs
 */
public class SubjectSummaryDto {
    private Long id;
    private String name;
    private String code;

    public SubjectSummaryDto() {}

    public SubjectSummaryDto(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
