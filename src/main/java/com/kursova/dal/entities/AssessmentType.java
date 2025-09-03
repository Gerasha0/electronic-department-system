package com.kursova.dal.entities;

/**
 * Enumeration for assessment types
 */
public enum AssessmentType {
    EXAM("Екзамен"),
    TEST("Залік"),
    DIFFERENTIATED_CREDIT("Диференційований залік"),
    COURSE_WORK("Курсова робота"),
    QUALIFICATION_WORK("Кваліфікаційна робота"),
    ATTESTATION("Атестація"),
    STATE_EXAM("Державний іспит");

    private final String displayName;

    AssessmentType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
