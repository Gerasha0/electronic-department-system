package com.kursova.pl.controllers;

import com.kursova.bll.services.ArchiveService;
import com.kursova.dal.entities.ArchivedGrade;
import com.kursova.dal.entities.ArchivedStudent;
import com.kursova.dal.entities.ArchivedStudentGroup;
import com.kursova.dal.entities.GradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ArchiveController
 * Tests all archive management endpoints for admin operations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Archive Controller Tests")
@SuppressWarnings("null") // Suppress null pointer warnings in tests where we control the mock behavior
class ArchiveControllerTest {

    @Mock
    private ArchiveService archiveService;

    @InjectMocks
    private ArchiveController archiveController;

    private ArchivedStudentGroup testArchivedGroup;
    private ArchivedStudent testArchivedStudent;
    private ArchivedGrade testArchivedGrade;
    private ArchiveService.ArchiveStatistics testStatistics;

    @BeforeEach
    void setUp() {
        setupTestData();
    }

    private void setupTestData() {
        // Setup test archived group
        testArchivedGroup = new ArchivedStudentGroup();
        testArchivedGroup.setId(1L);
        testArchivedGroup.setOriginalGroupId(101L);
        testArchivedGroup.setGroupName("BZ-121-23");
        testArchivedGroup.setGroupCode("BZ121");
        testArchivedGroup.setEnrollmentYear(2023);
        testArchivedGroup.setArchivedAt(LocalDateTime.now());
        testArchivedGroup.setArchivedBy("ADMIN");
        testArchivedGroup.setArchiveReason("Test archiving");

        // Setup test archived student
        testArchivedStudent = new ArchivedStudent();
        testArchivedStudent.setId(1L);
        testArchivedStudent.setOriginalStudentId(201L);
        testArchivedStudent.setStudentNumber("STU001");
        testArchivedStudent.setGroupName("BZ-121-23");
        testArchivedStudent.setArchivedAt(LocalDateTime.now());
        testArchivedStudent.setArchivedBy("ADMIN");

        // Setup test archived grade
        testArchivedGrade = new ArchivedGrade();
        testArchivedGrade.setId(1L);
        testArchivedGrade.setOriginalGradeId(301L);
        testArchivedGrade.setGradeValue(85);
        testArchivedGrade.setGradeType(GradeType.EXAM);
        testArchivedGrade.setOriginalStudentId(201L);
        testArchivedGrade.setStudentName("Test Student");
        testArchivedGrade.setSubjectName("Mathematics");
        testArchivedGrade.setArchivedAt(LocalDateTime.now());
        testArchivedGrade.setArchivedBy("ADMIN");

        // Setup test statistics
        testStatistics = new ArchiveService.ArchiveStatistics(5L, 50L, 500L, LocalDateTime.now());
    }

    // ===============================
    // GET ALL ARCHIVED DATA TESTS
    // ===============================

    @Test
    @DisplayName("Should get all archived groups successfully")
    void getAllArchivedGroups_Success() {
        // Arrange
        List<ArchivedStudentGroup> expectedGroups = Arrays.asList(testArchivedGroup);
        when(archiveService.getAllArchivedGroups()).thenReturn(expectedGroups);

        // Act
        ResponseEntity<List<ArchivedStudentGroup>> response = archiveController.getAllArchivedGroups();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull().hasSize(1);
        assertThat(response.getBody().get(0).getGroupName()).isEqualTo("BZ-121-23");
        verify(archiveService).getAllArchivedGroups();
    }

    @Test
    @DisplayName("Should get all archived students successfully")
    void getAllArchivedStudents_Success() {
        // Arrange
        List<ArchivedStudent> expectedStudents = Arrays.asList(testArchivedStudent);
        when(archiveService.getAllArchivedStudents()).thenReturn(expectedStudents);

        // Act
        ResponseEntity<List<ArchivedStudent>> response = archiveController.getAllArchivedStudents();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getStudentNumber()).isEqualTo("STU001");
        verify(archiveService).getAllArchivedStudents();
    }

    @Test
    @DisplayName("Should get all archived grades successfully")
    void getAllArchivedGrades_Success() {
        // Arrange
        List<ArchivedGrade> expectedGrades = Arrays.asList(testArchivedGrade);
        when(archiveService.getAllArchivedGrades()).thenReturn(expectedGrades);

        // Act
        ResponseEntity<List<ArchivedGrade>> response = archiveController.getAllArchivedGrades();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getGradeValue()).isEqualTo(85);
        verify(archiveService).getAllArchivedGrades();
    }

    // ===============================
    // SEARCH TESTS
    // ===============================

    @Test
    @DisplayName("Should search archived groups successfully")
    void searchArchivedGroups_Success() {
        // Arrange
        String searchTerm = "BZ-121";
        List<ArchivedStudentGroup> expectedGroups = Arrays.asList(testArchivedGroup);
        when(archiveService.searchArchivedGroups(searchTerm)).thenReturn(expectedGroups);

        // Act
        ResponseEntity<List<ArchivedStudentGroup>> response = archiveController.searchArchivedGroups(searchTerm);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(archiveService).searchArchivedGroups(searchTerm);
    }

    @Test
    @DisplayName("Should search archived students successfully")
    void searchArchivedStudents_Success() {
        // Arrange
        String searchTerm = "STU001";
        List<ArchivedStudent> expectedStudents = Arrays.asList(testArchivedStudent);
        when(archiveService.searchArchivedStudents(searchTerm)).thenReturn(expectedStudents);

        // Act
        ResponseEntity<List<ArchivedStudent>> response = archiveController.searchArchivedStudents(searchTerm);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(archiveService).searchArchivedStudents(searchTerm);
    }

    // ===============================
    // GET BY ID TESTS
    // ===============================

    @Test
    @DisplayName("Should get archived students by group ID successfully")
    void getArchivedStudentsByGroup_Success() {
        // Arrange
        Long groupId = 101L;
        List<ArchivedStudent> expectedStudents = Arrays.asList(testArchivedStudent);
        when(archiveService.getArchivedStudentsByGroupId(groupId)).thenReturn(expectedStudents);

        // Act
        ResponseEntity<List<ArchivedStudent>> response = archiveController.getArchivedStudentsByGroup(groupId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(archiveService).getArchivedStudentsByGroupId(groupId);
    }

    @Test
    @DisplayName("Should get archived grades by student ID successfully")
    void getArchivedGradesByStudent_Success() {
        // Arrange
        Long studentId = 201L;
        List<ArchivedGrade> expectedGrades = Arrays.asList(testArchivedGrade);
        when(archiveService.getArchivedGradesByStudentId(studentId)).thenReturn(expectedGrades);

        // Act
        ResponseEntity<List<ArchivedGrade>> response = archiveController.getArchivedGradesByStudent(studentId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(archiveService).getArchivedGradesByStudentId(studentId);
    }

    // ===============================
    // DATE RANGE TESTS
    // ===============================

    @Test
    @DisplayName("Should get archived groups by date range successfully")
    void getArchivedGroupsByDateRange_Success() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<ArchivedStudentGroup> expectedGroups = Arrays.asList(testArchivedGroup);
        when(archiveService.getArchivedGroupsByDateRange(startDate, endDate)).thenReturn(expectedGroups);

        // Act
        ResponseEntity<List<ArchivedStudentGroup>> response = archiveController.getArchivedGroupsByDateRange(startDate, endDate);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(1);
        verify(archiveService).getArchivedGroupsByDateRange(startDate, endDate);
    }

    // ===============================
    // STATISTICS TESTS
    // ===============================

    @Test
    @DisplayName("Should get archive statistics successfully")
    void getArchiveStatistics_Success() {
        // Arrange
        when(archiveService.getArchiveStatistics()).thenReturn(testStatistics);

        // Act
        ResponseEntity<ArchiveService.ArchiveStatistics> response = archiveController.getArchiveStatistics();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalArchivedGroups()).isEqualTo(5L);
        assertThat(response.getBody().totalArchivedStudents()).isEqualTo(50L);
        assertThat(response.getBody().totalArchivedGrades()).isEqualTo(500L);
        verify(archiveService).getArchiveStatistics();
    }

    // ===============================
    // MANUAL ARCHIVING TESTS
    // ===============================

    @Test
    @DisplayName("Should archive student manually successfully")
    void archiveStudent_Success() {
        // Arrange
        Long studentId = 201L;
        String reason = "Manual archiving";
        doNothing().when(archiveService).archiveStudent(studentId, "ADMIN", reason);

        // Act
        ResponseEntity<String> response = archiveController.archiveStudent(studentId, reason);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Student archived successfully");
        verify(archiveService).archiveStudent(studentId, "ADMIN", reason);
    }

    @Test
    @DisplayName("Should handle error when archiving student")
    void archiveStudent_Error() {
        // Arrange
        Long studentId = 999L;
        String reason = "Manual archiving";
        String errorMessage = "Student not found";
        doThrow(new RuntimeException(errorMessage)).when(archiveService).archiveStudent(studentId, "ADMIN", reason);

        // Act
        ResponseEntity<String> response = archiveController.archiveStudent(studentId, reason);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error archiving student: " + errorMessage);
        verify(archiveService).archiveStudent(studentId, "ADMIN", reason);
    }

    @Test
    @DisplayName("Should archive group manually successfully")
    void archiveGroup_Success() {
        // Arrange
        Long groupId = 101L;
        String reason = "Manual archiving";
        doNothing().when(archiveService).archiveStudentGroup(groupId, "ADMIN", reason);

        // Act
        ResponseEntity<String> response = archiveController.archiveGroup(groupId, reason);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Group archived successfully");
        verify(archiveService).archiveStudentGroup(groupId, "ADMIN", reason);
    }

    // ===============================
    // DELETE TESTS
    // ===============================

    @Test
    @DisplayName("Should delete archived group successfully")
    void deleteArchivedGroup_Success() {
        // Arrange
        Long groupId = 1L;
        doNothing().when(archiveService).deleteArchivedGroup(groupId);

        // Act
        ResponseEntity<String> response = archiveController.deleteArchivedGroup(groupId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Archived group deleted permanently");
        verify(archiveService).deleteArchivedGroup(groupId);
    }

    @Test
    @DisplayName("Should delete archived student successfully")
    void deleteArchivedStudent_Success() {
        // Arrange
        Long studentId = 1L;
        doNothing().when(archiveService).deleteArchivedStudent(studentId);

        // Act
        ResponseEntity<String> response = archiveController.deleteArchivedStudent(studentId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Archived student deleted permanently");
        verify(archiveService).deleteArchivedStudent(studentId);
    }

    @Test
    @DisplayName("Should delete archived grade successfully")
    void deleteArchivedGrade_Success() {
        // Arrange
        Long gradeId = 1L;
        doNothing().when(archiveService).deleteArchivedGrade(gradeId);

        // Act
        ResponseEntity<String> response = archiveController.deleteArchivedGrade(gradeId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("Archived grade deleted permanently");
        verify(archiveService).deleteArchivedGrade(gradeId);
    }

    @Test
    @DisplayName("Should handle error when deleting archived grade")
    void deleteArchivedGrade_Error() {
        // Arrange
        Long gradeId = 999L;
        String errorMessage = "Archived grade not found";
        doThrow(new RuntimeException(errorMessage)).when(archiveService).deleteArchivedGrade(gradeId);

        // Act
        ResponseEntity<String> response = archiveController.deleteArchivedGrade(gradeId);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo("Error deleting archived grade: " + errorMessage);
        verify(archiveService).deleteArchivedGrade(gradeId);
    }

    // ===============================
    // EDGE CASE TESTS
    // ===============================

    @Test
    @DisplayName("Should handle empty results gracefully")
    void getAllArchivedGroups_EmptyResults() {
        // Arrange
        when(archiveService.getAllArchivedGroups()).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<ArchivedStudentGroup>> response = archiveController.getAllArchivedGroups();

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
        verify(archiveService).getAllArchivedGroups();
    }

    @Test
    @DisplayName("Should use default reason when archiving student without reason")
    void archiveStudent_DefaultReason() {
        // Arrange
        Long studentId = 201L;
        String defaultReason = "Manual archiving";
        doNothing().when(archiveService).archiveStudent(studentId, "ADMIN", defaultReason);

        // Act
        ResponseEntity<String> response = archiveController.archiveStudent(studentId, defaultReason);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(archiveService).archiveStudent(studentId, "ADMIN", defaultReason);
    }
}
