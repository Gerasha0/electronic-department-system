package com.kursova.bll.dto.base;

/**
 * Base DTO class containing common fields for all DTOs
 * Reduces code duplication across DTO classes
 */
public abstract class BaseDto {
    protected Long id;
    protected Boolean isActive;
    protected String createdAt;
    protected String updatedAt;

    // Common getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
}
