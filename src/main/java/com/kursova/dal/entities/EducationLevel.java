package com.kursova.dal.entities;

/**
 * Enumeration for education levels
 */
public enum EducationLevel {
    BACHELOR("Бакалавр", 1, 5),
    MASTER("Магістр", 1, 2),
    PHD("Аспірант", 1, 4);

    private final String displayName;
    private final int minCourse;
    private final int maxCourse;

    EducationLevel(String displayName, int minCourse, int maxCourse) {
        this.displayName = displayName;
        this.minCourse = minCourse;
        this.maxCourse = maxCourse;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinCourse() {
        return minCourse;
    }

    public int getMaxCourse() {
        return maxCourse;
    }

    public boolean isValidCourse(int course) {
        return course >= minCourse && course <= maxCourse;
    }
}
