package com.kursova.dal.entities;

/**
 * Enumeration for grade categories (main types)
 */
public enum GradeCategory {
    CURRENT_CONTROL("Поточний контроль"),
    FINAL_CONTROL("Підсумковий контроль"),
    RETAKE("Перездача"),
    MAKEUP("Відпрацювання");

    private final String displayName;

    GradeCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
