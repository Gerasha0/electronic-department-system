package com.kursova.pl.controllers;

import com.kursova.bll.dto.GradeDto;
import com.kursova.bll.services.GradeService;
import com.kursova.dal.entities.*;
import com.kursova.dal.repositories.*;
import com.kursova.dal.uow.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GradeController
 * Tests all endpoints and business logic without Spring context
 */
@ExtendWith(MockitoExtension.class)
class GradeControllerTest {

    @Mock
    private GradeService gradeService;

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private GradeController gradeController;

    private GradeDto sampleGradeDto;
    private User sampleUser;
    private Student sampleStudent;
    private Grade sampleGrade;

    @BeforeEach
    void setUp() {
        // Setup sample data
        setupSampleData();
    }

    private void setupSampleData() {
        // Sample User
        sampleUser = new User();
        sampleUser.setId(1L);
        sampleUser.setUsername("testuser");
        sampleUser.setEmail("test@example.com");

        // Sample Student
        sampleStudent = new Student();
        sampleStudent.setId(1L);
        sampleStudent.setUser(sampleUser);

        // Sample Grade
        sampleGrade = new Grade();
        sampleGrade.setId(1L);
        sampleGrade.setGradeValue(85);
        sampleGrade.setStudent(sampleStudent);
        sampleGrade.setGradeType(GradeType.CURRENT);

        // Sample GradeDto
        sampleGradeDto = new GradeDto();
        sampleGradeDto.setId(1L);
        sampleGradeDto.setGradeValue(85);
        sampleGradeDto.setStudentId(1L);
        sampleGradeDto.setGradeType(GradeType.CURRENT);
    }

    // ===============================
    // CREATE GRADE TESTS
    // ===============================

    @Test
    void createGrade_Success() {
        // Arrange
        when(gradeService.create(any(GradeDto.class))).thenReturn(sampleGradeDto);

        // Act
        ResponseEntity<GradeDto> response = gradeController.createGrade(sampleGradeDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleGradeDto, response.getBody());
        verify(gradeService).create(sampleGradeDto);
    }

    @Test
    void createGradeByIds_Success() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("studentId", 1L);
        payload.put("teacherId", 1L);
        payload.put("subjectId", 1L);
        payload.put("gradeValue", 85);
        payload.put("gradeType", "CURRENT");
        payload.put("comments", "Good work");

        when(gradeService.createGradeWithValidation(any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleGradeDto);

        // Act
        ResponseEntity<?> response = gradeController.createGradeByIds(payload);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleGradeDto, response.getBody());
    }

    @Test
    void createGradeByIds_ExceptionHandling() {
        // Arrange
        Map<String, Object> payload = new HashMap<>();
        payload.put("studentId", 1L);
        payload.put("teacherId", 1L);
        payload.put("subjectId", 1L);
        payload.put("gradeValue", 85);

        when(gradeService.createGradeWithValidation(any(), any(), any(), any(), any(), any()))
                .thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<?> response = gradeController.createGradeByIds(payload);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("InternalServerError", errorResponse.get("error"));
        assertEquals("Failed to create grade", errorResponse.get("message"));
    }

    @Test
    void createGradeByUserIds_Success() {
        // Arrange
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("studentId", 1L); // This is actually userId
        payload.put("teacherId", 1L);
        payload.put("subjectId", 1L);
        payload.put("gradeValue", 85);

        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(sampleStudent));
        when(gradeService.createGradeWithValidation(any(), any(), any(), any(), any(), any()))
                .thenReturn(sampleGradeDto);

        // Act
        ResponseEntity<?> response = gradeController.createGradeByUserIds(payload);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleGradeDto, response.getBody());
    }

    @Test
    void createGradeByUserIds_StudentNotFound() {
        // Arrange
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        
        Map<String, Object> payload = new HashMap<>();
        payload.put("studentId", 1L); // This is actually userId

        when(studentRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = gradeController.createGradeByUserIds(payload);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> errorResponse = (Map<String, Object>) response.getBody();
        assertNotNull(errorResponse);
        assertEquals("StudentNotFound", errorResponse.get("error"));
    }

    // ===============================
    // READ GRADE TESTS
    // ===============================

    @Test
    void getGradeById_Success() {
        // Arrange
        when(gradeService.findById(1L)).thenReturn(sampleGradeDto);

        // Act
        ResponseEntity<GradeDto> response = gradeController.getGradeById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleGradeDto, response.getBody());
        verify(gradeService).findById(1L);
    }

    @Test
    void getAllGrades_Success() {
        // Arrange
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findAll()).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getAllGrades();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
        verify(gradeService).findAll();
    }

    @Test
    void getGradesByStudent_Success() {
        // Arrange
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findByStudentId(1L)).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getGradesByStudent(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
        verify(gradeService).findByStudentId(1L);
    }

    @Test
    void getMyGrades_Success() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(sampleStudent));
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findByStudentId(1L)).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getMyGrades(authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
    }

    @Test
    void getMyGrades_AuthenticationNull() {
        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getMyGrades(null);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void getMyGrades_UserNotFound() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getMyGrades(authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getMyGrades_StudentNotFound() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getMyGrades(authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getGradesByTeacher_Success() {
        // Arrange
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findByTeacherId(1L)).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getGradesByTeacher(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
    }

    @Test
    void getGradesBySubject_Success() {
        // Arrange
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findBySubjectId(1L)).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getGradesBySubject(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
    }

    @Test
    void getGradesByStudentAndSubject_Success() {
        // Arrange
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
            Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findByStudentAndSubject(1L, 1L)).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getGradesByStudentAndSubject(1L, 1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
    }

    @Test
    void getGradesByStudentAndSubject_ForbiddenForStudent() {
        // Arrange - Student trying to access other student's grades
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
            Arrays.asList(new SimpleGrantedAuthority("ROLE_STUDENT")));
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(sampleUser));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(sampleStudent));
        
        // Student trying to access grades for different student (studentId = 2L)
        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getGradesByStudentAndSubject(2L, 1L, authentication);

        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void getFinalGradesByStudent_Success() {
        // Arrange
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
            Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findFinalGradesByStudent(1L)).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getFinalGradesByStudent(1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
    }

    @Test
    void getGradesByType_Success() {
        // Arrange
        List<GradeDto> grades = Arrays.asList(sampleGradeDto);
        when(gradeService.findByGradeType(GradeType.CURRENT)).thenReturn(grades);

        // Act
        ResponseEntity<List<GradeDto>> response = gradeController.getGradesByType(GradeType.CURRENT);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(grades, response.getBody());
    }

    // ===============================
    // UPDATE GRADE TESTS
    // ===============================

    @Test
    void updateGrade_Success() {
        // Arrange
        GradeDto updatedGrade = new GradeDto();
        updatedGrade.setId(1L);
        updatedGrade.setGradeValue(90);
        when(gradeService.update(1L, sampleGradeDto)).thenReturn(updatedGrade);

        // Act
        ResponseEntity<GradeDto> response = gradeController.updateGrade(1L, sampleGradeDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedGrade, response.getBody());
        verify(gradeService).update(1L, sampleGradeDto);
    }

    // ===============================
    // DELETE GRADE TESTS
    // ===============================

    @Test
    void deleteGrade_Success() {
        // Arrange
        doNothing().when(gradeService).delete(1L);

        // Act
        ResponseEntity<Void> response = gradeController.deleteGrade(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(gradeService).delete(1L);
    }

    // ===============================
    // AVERAGE CALCULATION TESTS
    // ===============================

    @Test
    void getStudentAverageGrade_Success() {
        // Arrange
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
            Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(gradeService.getOverallAverageGradeForStudent(1L)).thenReturn(85.5);

        // Act
        ResponseEntity<Double> response = gradeController.getStudentAverageGrade(1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(85.5, response.getBody());
        verify(gradeService).getOverallAverageGradeForStudent(1L);
    }

    @Test
    void getStudentSubjectAverage_Success() {
        // Arrange
        when(authentication.getAuthorities()).thenAnswer(invocation -> 
            Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(gradeService.getAverageGradeForStudentInSubject(1L, 1L)).thenReturn(88.0);

        // Act
        ResponseEntity<Double> response = gradeController.getStudentSubjectAverage(1L, 1L, authentication);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(88.0, response.getBody());
        verify(gradeService).getAverageGradeForStudentInSubject(1L, 1L);
    }

    // ===============================
    // DIAGNOSTICS TESTS
    // ===============================

    @Test
    void debugGrades_Success() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        
        List<Grade> grades = Arrays.asList(sampleGrade);
        when(gradeRepository.findAll()).thenReturn(grades);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = gradeController.debugGrades();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Map<String, Object>> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
        Map<String, Object> gradeMap = responseBody.get(0);
        assertEquals(1L, gradeMap.get("id"));
        assertEquals(85, gradeMap.get("gradeValue"));
        assertEquals(1L, gradeMap.get("studentId"));
    }

    @Test
    void debugGrades_EmptyList() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        
        when(gradeRepository.findAll()).thenReturn(new ArrayList<>());

        // Act
        ResponseEntity<List<Map<String, Object>>> response = gradeController.debugGrades();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Map<String, Object>> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());
    }
}
