package com.kursova.dal.entities;

/**
 * Enumeration for study forms
 */
public enum StudyForm {
    FULL_TIME("Денна"),
    PART_TIME("Заочна"),
    EVENING("Вечірня"),
    DISTANCE("Дистанційна");
    
    private final String displayName;
    
    StudyForm(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
