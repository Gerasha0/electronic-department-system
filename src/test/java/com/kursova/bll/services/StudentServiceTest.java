package com.kursova.bll.services;

import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.dto.UserDto;
import com.kursova.bll.mappers.StudentMapper;
import com.kursova.bll.services.impl.StudentServiceImpl;
import com.kursova.dal.entities.*;
import com.kursova.dal.repositories.*;
import com.kursova.dal.uow.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for StudentService
 * Tests all student operations without Spring context
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Student Service Tests")
@SuppressWarnings("null") // Suppress null pointer warnings in tests where we control the mock behavior
class StudentServiceTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentGroupRepository studentGroupRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentMapper studentMapper;

    @Mock
    private ArchiveService archiveService;

    @InjectMocks
    private StudentServiceImpl studentService;

    private Student student;
    private StudentDto studentDto;
    private User user;
    private StudentGroup group;
    private Grade grade;

    @BeforeEach
    void setUp() {
        // Setup test data
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        group = new StudentGroup();
        group.setId(1L);
        group.setGroupName("CS-101");

        student = new Student();
        student.setId(1L);
        student.setStudentNumber("STU001");
        student.setEnrollmentYear(2023);
        student.setCourseYear(2);
        student.setIsActive(true);
        student.setUser(user);
        student.setGroup(group);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@example.com");

        studentDto = new StudentDto();
        studentDto.setId(1L);
        studentDto.setStudentNumber("STU001");
        studentDto.setEnrollmentYear(2023);
        studentDto.setCourse(2);
        studentDto.setIsActive(true);
        studentDto.setUser(userDto);

        grade = new Grade();
        grade.setId(1L);
        grade.setGradeValue(85);
        grade.setStudent(student);
    }

    @Test
    @DisplayName("Should find all students")
    void shouldFindAllStudents() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
        verify(studentRepository).findAll();
    }

    @Test
    @DisplayName("Should find student by id")
    void shouldFindStudentById() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.findById(1L);

        // Then
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when student not found by id")
    void shouldThrowExceptionWhenStudentNotFoundById() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.findById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Student not found with id: 1");
    }

    @Test
    @DisplayName("Should create student successfully")
    void shouldCreateStudentSuccessfully() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentMapper.toEntity(studentDto)).thenReturn(student);
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.create(studentDto);

        // Then
        assertThat(result).isEqualTo(studentDto);
        assertThat(student.getIsActive()).isTrue();
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should update student successfully")
    void shouldUpdateStudentSuccessfully() {
        // Given
        StudentDto updateDto = new StudentDto();
        updateDto.setStudentNumber("STU002");

        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.update(1L, updateDto);

        // Then
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent student")
    void shouldThrowExceptionWhenUpdatingNonExistentStudent() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.update(1L, studentDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Student not found with id: 1");
    }

    @Test
    @DisplayName("Should delete student successfully")
    void shouldDeleteStudentSuccessfully() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.existsById(1L)).thenReturn(true);

        // When
        studentService.delete(1L);

        // Then
        verify(archiveService).archiveStudent(1L, "ADMIN", "Student deleted via admin interface");
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent student")
    void shouldThrowExceptionWhenDeletingNonExistentStudent() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> studentService.delete(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Student not found with id: 1");
    }

    @Test
    @DisplayName("Should check if student exists by id")
    void shouldCheckIfStudentExistsById() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = studentService.existsById(1L);

        // Then
        assertThat(result).isTrue();
        verify(studentRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should find active students")
    void shouldFindActiveStudents() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.findActiveStudents();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
    }

    @Test
    @DisplayName("Should find student by user id")
    void shouldFindStudentByUserId() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.findByUserId(1L);

        // Then
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when student not found by user id")
    void shouldThrowExceptionWhenStudentNotFoundByUserId() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.findByUserId(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Student not found for user id: 1");
    }

    @Test
    @DisplayName("Should search students by name")
    void shouldSearchStudentsByName() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.searchByNameOrEmail("John")).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.searchByName("John");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
    }

    @Test
    @DisplayName("Should return active students when search name is empty")
    void shouldReturnActiveStudentsWhenSearchNameIsEmpty() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.searchByName("");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
    }

    @Test
    @DisplayName("Should find students by group")
    void shouldFindStudentsByGroup() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findByGroupIdAndIsActiveTrueOrderByUserLastNameAsc(1L)).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.findByGroup(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
    }

    @Test
    @DisplayName("Should find students without group")
    void shouldFindStudentsWithoutGroup() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findByGroupIsNullAndIsActiveTrueOrderByUserLastNameAsc()).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.findStudentsWithoutGroup();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
    }

    @Test
    @DisplayName("Should find students by enrollment year")
    void shouldFindStudentsByEnrollmentYear() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findByEnrollmentYearAndIsActiveTrueOrderByUserLastNameAsc(2023)).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.findByEnrollmentYear(2023);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
    }

    @Test
    @DisplayName("Should calculate average grade")
    void shouldCalculateAverageGrade() {
        // Given
        List<Grade> grades = Arrays.asList(grade);
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findByStudentIdOrderByGradeDateDesc(1L)).thenReturn(grades);

        // When
        Double result = studentService.calculateAverageGrade(1L);

        // Then
        assertThat(result).isEqualTo(85.0);
    }

    @Test
    @DisplayName("Should return 0.0 when no grades found")
    void shouldReturnZeroWhenNoGradesFound() {
        // Given
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findByStudentIdOrderByGradeDateDesc(1L)).thenReturn(Collections.emptyList());

        // When
        Double result = studentService.calculateAverageGrade(1L);

        // Then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should return 0.0 when exception occurs in average grade calculation")
    void shouldReturnZeroWhenExceptionOccursInAverageGradeCalculation() {
        // Given
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findByStudentIdOrderByGradeDateDesc(1L)).thenThrow(new RuntimeException("Database error"));

        // When
        Double result = studentService.calculateAverageGrade(1L);

        // Then
        assertThat(result).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should find student by id with calculated data")
    void shouldFindStudentByIdWithCalculatedData() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.findByIdWithCalculatedData(1L);

        // Then
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find students by group id")
    void shouldFindStudentsByGroupId() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findByGroupId(1L)).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<StudentDto> result = studentService.findByGroupId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(studentDto);
    }

    @Test
    @DisplayName("Should activate student")
    void shouldActivateStudent() {
        // Given
        student.setIsActive(false);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.activateStudent(1L);

        // Then
        assertThat(student.getIsActive()).isTrue();
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should deactivate student")
    void shouldDeactivateStudent() {
        // Given
        student.setIsActive(true);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.deactivateStudent(1L);

        // Then
        assertThat(student.getIsActive()).isFalse();
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should assign student to group")
    void shouldAssignStudentToGroup() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.assignToGroup(1L, 1L);

        // Then
        assertThat(student.getGroup()).isEqualTo(group);
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should throw exception when assigning to non-existent group")
    void shouldThrowExceptionWhenAssigningToNonExistentGroup() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.assignToGroup(1L, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Group not found with id: 1");
    }

    @Test
    @DisplayName("Should remove student from group")
    void shouldRemoveStudentFromGroup() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(studentRepository.save(student)).thenReturn(student);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.removeFromGroup(1L);

        // Then
        assertThat(student.getGroup()).isNull();
        assertThat(result).isEqualTo(studentDto);
        verify(studentRepository).save(student);
    }

    @Test
    @DisplayName("Should search students for group")
    void shouldSearchStudentsForGroup() {
        // Given
        List<Student> students = Arrays.asList(student);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findAll()).thenReturn(students);
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        List<Object> result = studentService.searchStudentsForGroup("John", 1L);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should return empty list when exception occurs in search students for group")
    void shouldReturnEmptyListWhenExceptionOccursInSearchStudentsForGroup() {
        // Given
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        // When
        List<Object> result = studentService.searchStudentsForGroup("John", 1L);

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should find student by email (username)")
    void shouldFindStudentByEmail() {
        // Given
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(studentDto);

        // When
        StudentDto result = studentService.findByEmail("testuser");

        // Then
        assertThat(result).isEqualTo(studentDto);
        verify(userRepository).findByUsername("testuser");
        verify(studentRepository).findByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by username")
    void shouldThrowExceptionWhenUserNotFoundByUsername() {
        // Given
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.findByEmail("testuser"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("User not found with username: testuser");
    }

    @Test
    @DisplayName("Should throw exception when student not found for user")
    void shouldThrowExceptionWhenStudentNotFoundForUser() {
        // Given
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(studentRepository.findByUserId(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentService.findByEmail("testuser"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Student not found for user with username: testuser");
    }
}
