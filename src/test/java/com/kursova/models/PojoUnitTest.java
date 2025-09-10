package com.kursova.models;

import com.kursova.bll.dto.GradeDto;
import com.kursova.bll.dto.UserDto;
import com.kursova.bll.dto.base.BaseDto;
import com.kursova.dal.entities.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PojoUnitTest {

    // -------------------- GradeDto tests --------------------
    @Test
    void gradeDto_gettersSetters_and_helpers() {
        GradeDto dto = new GradeDto();
        dto.setId(10L);
        dto.setGradeValue(92);
        dto.setGradeType(GradeType.EXAM);
        dto.setStudentId(5L);
        dto.setStudentName("Ivan Ivanov");
        dto.setSubjectName("Mathematics");
        dto.setIsFinal(true);

        assertEquals(10L, dto.getId());
        assertEquals(92, dto.getGradeValue());
        assertEquals(GradeType.EXAM, dto.getGradeType());
        assertEquals(5L, dto.getStudentId());
        assertTrue(dto.getIsFinal());

        assertEquals("A", dto.getGradeLetter());
        assertEquals("Зараховано", dto.getGradeStatus());
        String display = dto.getDisplayInfo();
        assertTrue(display.contains("92"));
        assertTrue(display.contains("A"));
        assertTrue(display.contains(GradeType.EXAM.getDisplayName()));

        String s = dto.toString();
        assertTrue(s.contains("gradeValue=92") || s.contains("92"));
        assertTrue(s.contains("Mathematics"));
    }

    @Test
    void gradeDto_letter_and_status_edge_cases() {
        GradeDto dto = new GradeDto();
        dto.setGradeValue(null);
        assertNull(dto.getGradeLetter());
        assertNull(dto.getGradeStatus());
        assertTrue(dto.getDisplayInfo().startsWith("N/A"));

        dto.setGradeValue(59);
        assertEquals("F", dto.getGradeLetter());
        assertEquals("Не зараховано", dto.getGradeStatus());
    }

    // -------------------- UserDto tests --------------------
    @Test
    void userDto_gettersSetters_and_helpers() {
        UserDto u = new UserDto();
        u.setId(3L);
        u.setUsername("jdoe");
        u.setEmail("jdoe@example.com");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setRole(UserRole.TEACHER);
        u.setIsActive(false);

        assertEquals(3L, u.getId());
        assertEquals("jdoe", u.getUsername());
        assertEquals("jdoe@example.com", u.getEmail());
        assertEquals("John", u.getFirstName());
        assertEquals("Doe", u.getLastName());
        assertEquals(UserRole.TEACHER, u.getRole());
        assertFalse(u.getIsActive());

        assertEquals("John Doe", u.getFullName());
        String s = u.toString();
        assertTrue(s.contains("jdoe"));
        assertTrue(s.contains("John Doe"));
    }

    // -------------------- Grade (entity) tests --------------------
    @Test
    void grade_entity_helpers_and_fields() {
        User user = new User();
        user.setFirstName("Petro");
        user.setLastName("Shevchenko");

        Student student = new Student();
        student.setId(11L);
        student.setStudentNumber("S-11");
        student.setUser(user);

        User tUser = new User();
        tUser.setFirstName("Anna");
        tUser.setLastName("Koval");

        Teacher teacher = new Teacher();
        teacher.setUser(tUser);

        Subject subject = new Subject();
        subject.setSubjectName("Physics");
        subject.setSubjectCode("PH-101");

        Grade grade = new Grade(student, teacher, subject, 75, GradeType.CONTROL_WORK);
        assertEquals(75, grade.getGradeValue());
        assertEquals(GradeType.CONTROL_WORK, grade.getGradeType());
        assertEquals(student, grade.getStudent());
        assertEquals(teacher, grade.getTeacher());
        assertEquals(subject, grade.getSubject());

        assertEquals("C", grade.getGradeLetter());
        assertEquals("Зараховано", grade.getGradeStatus());
        String info = grade.getDisplayInfo();
        assertTrue(info.contains("75"));
        assertTrue(info.contains(GradeType.CONTROL_WORK.getDisplayName()));
    }

    // -------------------- Student tests --------------------
    @Test
    void student_getters_and_helpers() {
        User u = new User();
        u.setFirstName("Oksana");
        u.setLastName("Lysenko");

        Student s = new Student();
        s.setUser(u);
        s.setStudentNumber("ST-100");

        StudentGroup g = new StudentGroup();
        g.setGroupName("CS-1");
        s.setGroup(g);

        assertEquals("Oksana Lysenko", s.getFullName());
        assertTrue(s.getDisplayInfo().contains("ST-100"));
        assertTrue(s.getDisplayInfo().contains("CS-1"));

        s.setCourse(2);
        assertEquals(2, s.getCourse());
    }

    // -------------------- User entity tests --------------------
    @Test
    void user_entity_fullName_and_fields() {
        User u = new User();
        u.setFirstName("Ivan");
        u.setLastName("Franko");
        u.setUsername("ifranko");
        u.setEmail("ivan@example.com");
        u.setPassword("secret");
        u.setRole(UserRole.MANAGER);

        assertEquals("Ivan Franko", u.getFullName());
        assertEquals("ifranko", u.getUsername());
        assertEquals("ivan@example.com", u.getEmail());
        assertEquals(UserRole.MANAGER, u.getRole());
    }

    // -------------------- Teacher tests --------------------
    @Test
    void teacher_getters_and_helpers() {
        User u = new User();
        u.setFirstName("Mykola");
        u.setLastName("Havryliv");

        Teacher t = new Teacher();
        t.setUser(u);
        t.setAcademicTitle("Dr.");

        assertEquals("Mykola Havryliv", t.getFullName());
        assertTrue(t.getDisplayTitle().contains("Dr."));
        assertTrue(t.getDisplayTitle().contains("Mykola Havryliv"));
    }

    // -------------------- Subject tests --------------------
    @Test
    void subject_getters_and_helpers() {
        Subject s = new Subject();
        s.setSubjectName("Algorithms");
        s.setSubjectCode("CS-201");
        s.setCredits(4);
        s.setHoursTotal(120);
        s.setHoursLectures(40);
        s.setHoursPractical(40);
        s.setHoursLaboratory(40);

        assertTrue(s.getDisplayName().contains("Algorithms"));
        assertTrue(s.getDisplayName().contains("CS-201"));
        assertTrue(s.getDisplayName().contains("4"));

        String workload = s.getWorkloadInfo();
        assertTrue(workload.contains("Всього годин") || workload.contains("120"));
        assertTrue(workload.contains("Лекції") || workload.contains("40"));
    }

    // -------------------- Enum tests --------------------
    @Test
    void enums_displayNames() {
        assertEquals("Екзамен", GradeType.EXAM.getDisplayName());
        assertEquals("Студент", UserRole.STUDENT.getDisplayName());
        assertEquals("Денна", StudyForm.FULL_TIME.getDisplayName());
        assertEquals("Екзамен", AssessmentType.EXAM.getDisplayName());
        assertEquals("Бакалавр", EducationLevel.BACHELOR.getDisplayName());
    }

    // -------------------- BaseDto tests --------------------
    /**
     * Concrete subclass for testing BaseDto since it's abstract
     */
    private static class TestBaseDto extends BaseDto {
        // Empty concrete implementation for testing
    }

    @Test
    void baseDto_gettersAndSetters() {
        TestBaseDto dto = new TestBaseDto();

        // Test ID
        dto.setId(123L);
        assertEquals(123L, dto.getId());

        // Test isActive
        dto.setIsActive(true);
        assertTrue(dto.getIsActive());

        dto.setIsActive(false);
        assertFalse(dto.getIsActive());

        dto.setIsActive(null);
        assertNull(dto.getIsActive());

        // Test createdAt
        String createdAt = "2023-01-01T10:00:00";
        dto.setCreatedAt(createdAt);
        assertEquals(createdAt, dto.getCreatedAt());

        // Test updatedAt
        String updatedAt = "2023-01-02T11:00:00";
        dto.setUpdatedAt(updatedAt);
        assertEquals(updatedAt, dto.getUpdatedAt());
    }

    @Test
    void baseDto_nullValues() {
        TestBaseDto dto = new TestBaseDto();

        // Test null ID
        dto.setId(null);
        assertNull(dto.getId());

        // Test null isActive
        dto.setIsActive(null);
        assertNull(dto.getIsActive());

        // Test null createdAt
        dto.setCreatedAt(null);
        assertNull(dto.getCreatedAt());

        // Test null updatedAt
        dto.setUpdatedAt(null);
        assertNull(dto.getUpdatedAt());
    }

    @Test
    void baseDto_defaultValues() {
        TestBaseDto dto = new TestBaseDto();

        // Test default values (should be null)
        assertNull(dto.getId());
        assertNull(dto.getIsActive());
        assertNull(dto.getCreatedAt());
        assertNull(dto.getUpdatedAt());
    }
}

