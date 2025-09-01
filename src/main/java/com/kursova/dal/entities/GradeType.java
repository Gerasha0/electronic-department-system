package com.kursova.dal.entities;

/**
 * Enumeration for grade types
 */
public enum GradeType {
    CURRENT("Поточна"),
    MODULE("Модульна"),
    MIDTERM("Проміжна"),
    FINAL("Підсумкова"),
    RETAKE("Перездача"),
    MAKEUP("Відпрацювання");

    private final String displayName;

    GradeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
