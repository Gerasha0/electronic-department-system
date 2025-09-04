package com.kursova.dal.entities;

/**
 * Enumeration for grade types
 */
public enum GradeType {
    // Legacy types (for backward compatibility)
    CURRENT("Поточна"),
    MODULE("Модульна"),
    MIDTERM("Проміжна"),
    FINAL("Підсумкова"),
    RETAKE("Перездача"),
    MAKEUP("Відпрацювання"),
    
    // Current control types
    LABORATORY("Лабораторна робота"),
    PRACTICAL("Практична робота"),
    SEMINAR("Семінар"),
    CONTROL_WORK("Контрольна робота"),
    MODULE_WORK("Модульна робота"),
    HOMEWORK("Домашнє завдання"),
    INDIVIDUAL_WORK("Індивідуальне завдання"),
    MAKEUP_WORK("Відпрацювання"),
    
    // Final control types
    EXAM("Екзамен"),
    CREDIT("Залік"),
    DIFF_CREDIT("Диференційований залік"),
    COURSEWORK("Курсова робота"),
    QUALIFICATION_WORK("Кваліфікаційна робота"),
    STATE_EXAM("Державний іспит"),
    ATTESTATION("Атестація"),
    
    // Retake types
    RETAKE_EXAM("Перездача екзамену"),
    RETAKE_CREDIT("Перездача заліку"),
    RETAKE_WORK("Перездача роботи"),
    
    // Makeup types
    MAKEUP_LESSON("Відпрацювання заняття"),
    ADDITIONAL_TASK("Додаткове завдання");

    private final String displayName;

    GradeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
