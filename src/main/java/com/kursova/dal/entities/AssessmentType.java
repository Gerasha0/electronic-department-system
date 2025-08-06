package com.kursova.dal.entities;

/**
 * Enumeration for assessment types
 */
public enum AssessmentType {
    EXAM("Екзамен"),
    CREDIT("Залік"),
    DIFFERENTIATED_CREDIT("Диференційований залік"),
    COURSE_WORK("Курсова робота"),
    COURSE_PROJECT("Курсовий проект");
    
    private final String displayName;
    
    AssessmentType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
