package com.kursova.dal.entities;

/**
 * Enumeration for user roles in the Electronic Department System
 */
public enum UserRole {
    ADMIN("Адміністратор"),
    MANAGER("Менеджер"),
    TEACHER("Викладач"),
    STUDENT("Студент"),
    GUEST("Гість");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
