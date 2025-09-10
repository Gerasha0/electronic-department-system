package com.kursova.bll.services;

import com.kursova.bll.services.impl.ArchiveServiceImpl;
import com.kursova.dal.entities.*;
import com.kursova.dal.repositories.ArchivedGradeRepository;
import com.kursova.dal.repositories.ArchivedStudentGroupRepository;
import com.kursova.dal.repositories.ArchivedStudentRepository;
import com.kursova.dal.uow.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.lenient;

/**
 * Unit tests for ArchiveService
 * Tests all archive operations without Spring context
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Archive Service Tests")
@SuppressWarnings("null") // Suppress null pointer warnings in tests where we control the mock behavior
class ArchiveServiceTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private ArchivedStudentGroupRepository archivedStudentGroupRepository;

    @Mock
    private ArchivedStudentRepository archivedStudentRepository;

    @Mock
    private ArchivedGradeRepository archivedGradeRepository;

    @InjectMocks
    private ArchiveServiceImpl archiveService;

    private ArchivedStudentGroup testArchivedGroup;
    private ArchivedStudent testArchivedStudent;
    private ArchivedGrade testArchivedGrade;

    @BeforeEach
    void setUp() {
        setupTestData();
        setupMockRepositories();
    }

    private void setupTestData() {
        // Setup test archived group
        testArchivedGroup = new ArchivedStudentGroup();
        testArchivedGroup.setId(1L);
        testArchivedGroup.setOriginalGroupId(101L);
        testArchivedGroup.setGroupName("BZ-121-23");
        testArchivedGroup.setGroupCode("BZ121");
        testArchivedGroup.setArchivedAt(LocalDateTime.now());
        testArchivedGroup.setArchivedBy("ADMIN");

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
    }

    private void setupMockRepositories() {
        // Setup with lenient stubs to avoid unnecessary stubbing errors
        lenient().when(unitOfWork.getArchivedStudentGroupRepository()).thenReturn(archivedStudentGroupRepository);
        lenient().when(unitOfWork.getArchivedStudentRepository()).thenReturn(archivedStudentRepository);
        lenient().when(unitOfWork.getArchivedGradeRepository()).thenReturn(archivedGradeRepository);
    }

    // ===============================
    // GET ALL TESTS
    // ===============================

    @Test
    @DisplayName("Should get all archived groups successfully")
    void getAllArchivedGroups_ShouldReturnAllGroups() {
        // Arrange
        List<ArchivedStudentGroup> expectedGroups = Arrays.asList(testArchivedGroup);
        when(archivedStudentGroupRepository.findAllByOrderByArchivedAtDesc()).thenReturn(expectedGroups);

        // Act
        List<ArchivedStudentGroup> result = archiveService.getAllArchivedGroups();

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getGroupName()).isEqualTo("BZ-121-23");
        verify(archivedStudentGroupRepository).findAllByOrderByArchivedAtDesc();
    }

    @Test
    @DisplayName("Should get all archived students successfully")
    void getAllArchivedStudents_ShouldReturnAllStudents() {
        // Arrange
        List<ArchivedStudent> expectedStudents = Arrays.asList(testArchivedStudent);
        when(archivedStudentRepository.findAllByOrderByArchivedAtDesc()).thenReturn(expectedStudents);

        // Act
        List<ArchivedStudent> result = archiveService.getAllArchivedStudents();

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getStudentNumber()).isEqualTo("STU001");
        verify(archivedStudentRepository).findAllByOrderByArchivedAtDesc();
    }

    @Test
    @DisplayName("Should get all archived grades successfully")
    void getAllArchivedGrades_ShouldReturnAllGrades() {
        // Arrange
        List<ArchivedGrade> expectedGrades = Arrays.asList(testArchivedGrade);
        when(archivedGradeRepository.findAllByOrderByArchivedAtDesc()).thenReturn(expectedGrades);

        // Act
        List<ArchivedGrade> result = archiveService.getAllArchivedGrades();

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getGradeValue()).isEqualTo(85);
        verify(archivedGradeRepository).findAllByOrderByArchivedAtDesc();
    }

    // ===============================
    // SEARCH TESTS
    // ===============================

    @Test
    @DisplayName("Should search archived groups by term")
    void searchArchivedGroups_ShouldReturnMatchingGroups() {
        // Arrange
        String searchTerm = "BZ-121";
        List<ArchivedStudentGroup> groupsByCode = Arrays.asList(testArchivedGroup);
        List<ArchivedStudentGroup> groupsByName = Collections.emptyList();
        
        when(archivedStudentGroupRepository.findByGroupCodeContainingIgnoreCase(searchTerm))
            .thenReturn(groupsByCode);
        when(archivedStudentGroupRepository.findByGroupNameContainingIgnoreCase(searchTerm))
            .thenReturn(groupsByName);

        // Act
        List<ArchivedStudentGroup> result = archiveService.searchArchivedGroups(searchTerm);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getGroupCode()).contains("BZ121");
        verify(archivedStudentGroupRepository).findByGroupCodeContainingIgnoreCase(searchTerm);
        verify(archivedStudentGroupRepository).findByGroupNameContainingIgnoreCase(searchTerm);
    }

    @Test
    @DisplayName("Should search archived students by term")
    void searchArchivedStudents_ShouldReturnMatchingStudents() {
        // Arrange
        String searchTerm = "STU001";
        List<ArchivedStudent> expectedStudents = Arrays.asList(testArchivedStudent);
        
        when(archivedStudentRepository.findByStudentNumberContainingIgnoreCase(searchTerm))
            .thenReturn(expectedStudents);

        // Act
        List<ArchivedStudent> result = archiveService.searchArchivedStudents(searchTerm);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getStudentNumber()).isEqualTo("STU001");
        verify(archivedStudentRepository).findByStudentNumberContainingIgnoreCase(searchTerm);
    }

    @Test
    @DisplayName("Should return all groups when search term is empty")
    void searchArchivedGroups_ShouldReturnAllWhenEmptyTerm() {
        // Arrange
        List<ArchivedStudentGroup> allGroups = Arrays.asList(testArchivedGroup);
        when(archivedStudentGroupRepository.findAllByOrderByArchivedAtDesc()).thenReturn(allGroups);

        // Act
        List<ArchivedStudentGroup> result = archiveService.searchArchivedGroups("");

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        verify(archivedStudentGroupRepository).findAllByOrderByArchivedAtDesc();
        verify(archivedStudentGroupRepository, never()).findByGroupCodeContainingIgnoreCase(anyString());
    }

    // ===============================
    // FILTER BY ID TESTS
    // ===============================

    @Test
    @DisplayName("Should get archived students by group ID")
    void getArchivedStudentsByGroupId_ShouldReturnStudentsFromGroup() {
        // Arrange
        Long groupId = 101L;
        List<ArchivedStudent> expectedStudents = Arrays.asList(testArchivedStudent);
        when(archivedStudentRepository.findByOriginalGroupId(groupId)).thenReturn(expectedStudents);

        // Act
        List<ArchivedStudent> result = archiveService.getArchivedStudentsByGroupId(groupId);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getOriginalStudentId()).isEqualTo(201L);
        verify(archivedStudentRepository).findByOriginalGroupId(groupId);
    }

    @Test
    @DisplayName("Should get archived grades by student ID")
    void getArchivedGradesByStudentId_ShouldReturnGradesForStudent() {
        // Arrange
        Long studentId = 201L;
        List<ArchivedGrade> expectedGrades = Arrays.asList(testArchivedGrade);
        when(archivedGradeRepository.findByOriginalStudentId(studentId)).thenReturn(expectedGrades);

        // Act
        List<ArchivedGrade> result = archiveService.getArchivedGradesByStudentId(studentId);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        assertThat(result.get(0).getOriginalStudentId()).isEqualTo(201L);
        verify(archivedGradeRepository).findByOriginalStudentId(studentId);
    }

    // ===============================
    // DATE RANGE TESTS
    // ===============================

    @Test
    @DisplayName("Should get archived groups by date range")
    void getArchivedGroupsByDateRange_ShouldReturnGroupsInRange() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        List<ArchivedStudentGroup> expectedGroups = Arrays.asList(testArchivedGroup);
        
        when(archivedStudentGroupRepository.findByArchivedAtBetween(startDate, endDate))
            .thenReturn(expectedGroups);

        // Act
        List<ArchivedStudentGroup> result = archiveService.getArchivedGroupsByDateRange(startDate, endDate);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        verify(archivedStudentGroupRepository).findByArchivedAtBetween(startDate, endDate);
    }

    // ===============================
    // STATISTICS TESTS
    // ===============================

    @Test
    @DisplayName("Should get archive statistics")
    void getArchiveStatistics_ShouldReturnCorrectStats() {
        // Arrange
        when(archivedStudentGroupRepository.count()).thenReturn(5L);
        when(archivedStudentRepository.count()).thenReturn(50L);
        when(archivedGradeRepository.count()).thenReturn(500L);
        when(archivedStudentGroupRepository.findAllByOrderByArchivedAtDesc())
            .thenReturn(Arrays.asList(testArchivedGroup));

        // Act
        ArchiveService.ArchiveStatistics result = archiveService.getArchiveStatistics();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.totalArchivedGroups()).isEqualTo(5L);
        assertThat(result.totalArchivedStudents()).isEqualTo(50L);
        assertThat(result.totalArchivedGrades()).isEqualTo(500L);
        assertThat(result.lastArchiveDate()).isNotNull();
        
        verify(archivedStudentGroupRepository).count();
        verify(archivedStudentRepository).count();
        verify(archivedGradeRepository).count();
        verify(archivedStudentGroupRepository).findAllByOrderByArchivedAtDesc();
    }

    @Test
    @DisplayName("Should handle empty statistics")
    void getArchiveStatistics_ShouldHandleEmptyData() {
        // Arrange
        when(archivedStudentGroupRepository.count()).thenReturn(0L);
        when(archivedStudentRepository.count()).thenReturn(0L);
        when(archivedGradeRepository.count()).thenReturn(0L);
        when(archivedStudentGroupRepository.findAllByOrderByArchivedAtDesc())
            .thenReturn(Collections.emptyList());

        // Act
        ArchiveService.ArchiveStatistics result = archiveService.getArchiveStatistics();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.totalArchivedGroups()).isZero();
        assertThat(result.totalArchivedStudents()).isZero();
        assertThat(result.totalArchivedGrades()).isZero();
        assertThat(result.lastArchiveDate()).isNull();
    }

    // ===============================
    // DELETE TESTS
    // ===============================

    @Test
    @DisplayName("Should delete archived group successfully")
    void deleteArchivedGroup_ShouldDeleteWhenExists() {
        // Arrange
        Long groupId = 1L;
        when(archivedStudentGroupRepository.existsById(groupId)).thenReturn(true);

        // Act
        archiveService.deleteArchivedGroup(groupId);

        // Assert
        verify(archivedStudentGroupRepository).existsById(groupId);
        verify(archivedStudentGroupRepository).deleteById(groupId);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent archived group")
    void deleteArchivedGroup_ShouldThrowWhenNotExists() {
        // Arrange
        Long groupId = 999L;
        when(archivedStudentGroupRepository.existsById(groupId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> archiveService.deleteArchivedGroup(groupId))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Archived group not found with id: 999");
        
        verify(archivedStudentGroupRepository).existsById(groupId);
        verify(archivedStudentGroupRepository, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("Should delete archived student successfully")
    void deleteArchivedStudent_ShouldDeleteWhenExists() {
        // Arrange
        Long studentId = 1L;
        when(archivedStudentRepository.existsById(studentId)).thenReturn(true);

        // Act
        archiveService.deleteArchivedStudent(studentId);

        // Assert
        verify(archivedStudentRepository).existsById(studentId);
        verify(archivedStudentRepository).deleteById(studentId);
    }

    @Test
    @DisplayName("Should delete archived grade successfully")
    void deleteArchivedGrade_ShouldDeleteWhenExists() {
        // Arrange
        Long gradeId = 1L;
        when(archivedGradeRepository.existsById(gradeId)).thenReturn(true);

        // Act
        archiveService.deleteArchivedGrade(gradeId);

        // Assert
        verify(archivedGradeRepository).existsById(gradeId);
        verify(archivedGradeRepository).deleteById(gradeId);
    }

    // ===============================
    // EDGE CASES
    // ===============================

    @Test
    @DisplayName("Should handle empty search results gracefully")
    void searchArchivedGroups_ShouldHandleEmptyResults() {
        // Arrange
        String searchTerm = "nonexistent";
        when(archivedStudentGroupRepository.findByGroupCodeContainingIgnoreCase(searchTerm))
            .thenReturn(Collections.emptyList());
        when(archivedStudentGroupRepository.findByGroupNameContainingIgnoreCase(searchTerm))
            .thenReturn(Collections.emptyList());

        // Act
        List<ArchivedStudentGroup> result = archiveService.searchArchivedGroups(searchTerm);

        // Assert
        assertThat(result).isNotNull().isEmpty();
    }

    @Test
    @DisplayName("Should handle null search term")
    void searchArchivedGroups_ShouldReturnAllWhenNullTerm() {
        // Arrange
        List<ArchivedStudentGroup> allGroups = Arrays.asList(testArchivedGroup);
        when(archivedStudentGroupRepository.findAllByOrderByArchivedAtDesc()).thenReturn(allGroups);

        // Act
        List<ArchivedStudentGroup> result = archiveService.searchArchivedGroups(null);

        // Assert
        assertThat(result).isNotNull().hasSize(1);
        verify(archivedStudentGroupRepository).findAllByOrderByArchivedAtDesc();
    }
}
