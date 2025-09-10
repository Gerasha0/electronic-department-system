package com.kursova.dal.entities;

/**
 * Enumeration for specific grade work types
 */
public enum GradeType {
    // Current control types (Поточний контроль)
    LABORATORY_WORK("Лабораторна робота"),
    PRACTICAL_WORK("Практична робота"),
    SEMINAR("Семінар"),
    CONTROL_WORK("Контрольна робота"),
    MODULE_WORK("Модульна робота"),
    HOMEWORK("Домашнє завдання"),
    INDIVIDUAL_WORK("Індивідуальне завдання"),
    CURRENT_MAKEUP("Відпрацювання"),
    
    // Final control types (Підсумковий контроль)
    EXAM("Екзамен"),
    CREDIT("Залік"),
    DIFFERENTIATED_CREDIT("Диференційований залік"),
    COURSE_WORK("Курсова робота"),
    QUALIFICATION_WORK("Кваліфікаційна робота"),
    STATE_EXAM("Державний іспит"),
    ATTESTATION("Атестація"),
    
    // Retake types (Перездача)
    RETAKE_EXAM("Перездача екзамену"),
    RETAKE_CREDIT("Перездача заліку"),
    RETAKE_WORK("Перездача роботи"),
    
    // Makeup types (Відпрацювання)
    MAKEUP_LESSON("Відпрацювання заняття"),
    MAKEUP_WORK("Відпрацювання роботи"),
    ADDITIONAL_TASK("Додаткове завдання");

    private final String displayName;

    GradeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
