package com.kursova.bll.services;

import com.kursova.bll.dto.GradeDto;
import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.dto.SubjectDto;
import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.dto.UserDto;
import com.kursova.bll.mappers.GradeMapper;
import com.kursova.bll.mappers.SubjectMapper;
import com.kursova.bll.mappers.TeacherMapper;
import com.kursova.bll.services.impl.TeacherServiceImpl;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TeacherService
 * Tests all teacher operations without Spring context
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Teacher Service Tests")
@SuppressWarnings("null") // Suppress null pointer warnings in tests where we control the mock behavior
class TeacherServiceTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private StudentService studentService;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private GradeMapper gradeMapper;

    @InjectMocks
    private TeacherServiceImpl teacherService;

    private Teacher teacher;
    private TeacherDto teacherDto;
    private User user;
    private UserDto userDto;
    private Subject subject;
    private SubjectDto subjectDto;
    private Grade grade;
    private GradeDto gradeDto;
    private Student student;
    private StudentDto studentDto;

    @BeforeEach
    void setUp() {
        // Setup User for Teacher
        user = new User();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@university.edu");
        user.setUsername("jdoe");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setFirstName("John");
        userDto.setLastName("Doe");
        userDto.setEmail("john.doe@university.edu");
        userDto.setUsername("jdoe");

        // Setup test data
        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setUser(user);
        teacher.setAcademicTitle("Professor");
        teacher.setDepartmentPosition("Head of Department");
        teacher.setIsActive(true);
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());

        teacherDto = new TeacherDto();
        teacherDto.setId(1L);
        teacherDto.setUser(userDto);
        teacherDto.setAcademicTitle("Professor");
        teacherDto.setDepartmentPosition("Head of Department");
        teacherDto.setIsActive(true);

        subject = new Subject();
        subject.setId(1L);
        subject.setSubjectName("Mathematics");
        subject.setSubjectCode("MATH101");
        subject.setCredits(5);
        subject.setSemester(1);

        subjectDto = new SubjectDto();
        subjectDto.setId(1L);
        subjectDto.setSubjectName("Mathematics");
        subjectDto.setSubjectCode("MATH101");
        subjectDto.setCredits(5);
        subjectDto.setSemester(1);

        grade = new Grade();
        grade.setId(1L);
        grade.setGradeValue(85);
        grade.setGradeDate(LocalDateTime.now());

        gradeDto = new GradeDto();
        gradeDto.setId(1L);
        gradeDto.setGradeValue(85);

        // Setup User for Student
        User studentUser = new User();
        studentUser.setId(2L);
        studentUser.setFirstName("Jane");
        studentUser.setLastName("Smith");
        studentUser.setEmail("jane.smith@university.edu");

        UserDto studentUserDto = new UserDto();
        studentUserDto.setId(2L);
        studentUserDto.setFirstName("Jane");
        studentUserDto.setLastName("Smith");
        studentUserDto.setEmail("jane.smith@university.edu");

        student = new Student();
        student.setId(1L);
        student.setUser(studentUser);
        student.setStudentNumber("ST12345");

        studentDto = new StudentDto();
        studentDto.setId(1L);
        studentDto.setUser(studentUserDto);
        studentDto.setStudentNumber("ST12345");
    }

    @Test
    @DisplayName("Should find teacher by ID successfully")
    void shouldFindTeacherById() {
        // Given
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.findById(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUser().getFirstName()).isEqualTo("John");
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findById(1L);
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    @DisplayName("Should throw exception when teacher not found by ID")
    void shouldThrowExceptionWhenTeacherNotFoundById() {
        // Given
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teacherService.findById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Teacher not found with id: 1");
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findById(1L);
    }

    @Test
    @DisplayName("Should find all teachers")
    void shouldFindAllTeachers() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // When
        List<TeacherDto> result = teacherService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findAll();
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    @DisplayName("Should find active teachers")
    void shouldFindActiveTeachers() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // When
        List<TeacherDto> result = teacherService.findActiveTeachers();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findAll();
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    @DisplayName("Should create teacher successfully")
    void shouldCreateTeacher() {
        // Given
        TeacherDto inputDto = new TeacherDto();
        inputDto.setUser(userDto);
        inputDto.setAcademicTitle("Professor");
        inputDto.setDepartmentPosition("Lecturer");

        Teacher savedTeacher = new Teacher();
        savedTeacher.setId(2L);
        savedTeacher.setUser(user);
        savedTeacher.setAcademicTitle("Professor");
        savedTeacher.setDepartmentPosition("Lecturer");

        when(teacherMapper.toEntity(inputDto)).thenReturn(savedTeacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.save(savedTeacher)).thenReturn(savedTeacher);
        when(teacherMapper.toDto(savedTeacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.create(inputDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAcademicTitle()).isEqualTo("Professor");
        verify(teacherMapper).toEntity(inputDto);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).save(savedTeacher);
        verify(teacherMapper).toDto(savedTeacher);
    }

    @Test
    @DisplayName("Should update teacher successfully")
    void shouldUpdateTeacher() {
        // Given
        TeacherDto updateDto = new TeacherDto();
        updateDto.setId(1L);
        updateDto.setUser(userDto);
        updateDto.setAcademicTitle("Associate Professor");
        updateDto.setDepartmentPosition("Senior Lecturer");

        Teacher existingTeacher = new Teacher();
        existingTeacher.setId(1L);
        existingTeacher.setUser(user);
        existingTeacher.setAcademicTitle("Professor");
        existingTeacher.setDepartmentPosition("Head of Department");

        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(1L);
        updatedTeacher.setUser(user);
        updatedTeacher.setAcademicTitle("Associate Professor");
        updatedTeacher.setDepartmentPosition("Senior Lecturer");

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(existingTeacher));
        doNothing().when(teacherMapper).updateEntityFromDto(updateDto, existingTeacher);
        when(teacherRepository.save(any(Teacher.class))).thenReturn(updatedTeacher);
        when(teacherMapper.toDto(updatedTeacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.update(1L, updateDto);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAcademicTitle()).isEqualTo("Professor");
        verify(unitOfWork, times(2)).getTeacherRepository();
        verify(teacherRepository).findById(1L);
        verify(teacherMapper).updateEntityFromDto(updateDto, existingTeacher);
        verify(teacherRepository).save(any(Teacher.class));
        verify(teacherMapper).toDto(updatedTeacher);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent teacher")
    void shouldThrowExceptionWhenUpdatingNonExistentTeacher() {
        // Given
        TeacherDto updateDto = new TeacherDto();
        updateDto.setId(1L);

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teacherService.update(1L, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Teacher not found with id: 1");
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findById(1L);
    }

    @Test
    @DisplayName("Should delete teacher successfully")
    void shouldDeleteTeacher() {
        // Given
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(teacherRepository.existsById(1L)).thenReturn(true);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        
        // When
        teacherService.delete(1L);

        // Then
        verify(unitOfWork, atLeastOnce()).getTeacherRepository();
        verify(teacherRepository).existsById(1L);
        verify(teacherRepository).findById(1L);
        verify(teacherRepository).save(teacher);
        verify(teacherRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent teacher")
    void shouldThrowExceptionWhenDeletingNonExistentTeacher() {
        // Given
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> teacherService.delete(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Teacher not found with id: 1");
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should search teachers by name")
    void shouldSearchTeachersByName() {
        // Given
        String searchName = "John";
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.searchByNameOrEmail(searchName)).thenReturn(teachers);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // When
        List<TeacherDto> result = teacherService.searchByName(searchName);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getFirstName()).isEqualTo("John");
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).searchByNameOrEmail(searchName);
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    @DisplayName("Should return all active teachers when search name is null")
    void shouldReturnAllActiveTeachersWhenSearchNameIsNull() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // When
        List<TeacherDto> result = teacherService.searchByName(null);

        // Then
        assertThat(result).hasSize(1);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findAll();
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    @DisplayName("Should return all active teachers when search name is empty")
    void shouldReturnAllActiveTeachersWhenSearchNameIsEmpty() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findAll()).thenReturn(teachers);
        when(teacherMapper.toDto(teacher)).thenReturn(teacherDto);

        // When
        List<TeacherDto> result = teacherService.searchByName("");

        // Then
        assertThat(result).hasSize(1);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findAll();
        verify(teacherMapper).toDto(teacher);
    }

    @Test
    @DisplayName("Should assign subject to teacher")
    void shouldAssignSubjectToTeacher() {
        // Given
        teacher.setSubjects(new java.util.HashSet<>());
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(1L);
        updatedTeacher.setUser(user);
        updatedTeacher.setAcademicTitle("Professor");

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(unitOfWork.getSubjectRepository()).thenReturn(subjectRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.save(teacher)).thenReturn(updatedTeacher);
        when(teacherMapper.toDto(updatedTeacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.assignSubject(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(teacher.getSubjects()).contains(subject);
        verify(unitOfWork, times(2)).getTeacherRepository();
        verify(unitOfWork).getSubjectRepository();
        verify(teacherRepository).findById(1L);
        verify(subjectRepository).findById(1L);
        verify(teacherRepository).save(teacher);
        verify(teacherMapper).toDto(updatedTeacher);
    }

    @Test
    @DisplayName("Should throw exception when assigning subject to non-existent teacher")
    void shouldThrowExceptionWhenAssigningSubjectToNonExistentTeacher() {
        // Given
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teacherService.assignSubject(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Teacher not found with id: 1");
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when assigning non-existent subject to teacher")
    void shouldThrowExceptionWhenAssigningNonExistentSubjectToTeacher() {
        // Given
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(unitOfWork.getSubjectRepository()).thenReturn(subjectRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> teacherService.assignSubject(1L, 1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Subject not found with id: 1");
        verify(unitOfWork).getTeacherRepository();
        verify(unitOfWork).getSubjectRepository();
        verify(teacherRepository).findById(1L);
        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should remove subject from teacher")
    void shouldRemoveSubjectFromTeacher() {
        // Given
        teacher.setSubjects(new java.util.HashSet<>(Arrays.asList(subject)));
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(1L);
        updatedTeacher.setUser(user);

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(unitOfWork.getSubjectRepository()).thenReturn(subjectRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.save(teacher)).thenReturn(updatedTeacher);
        when(teacherMapper.toDto(updatedTeacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.removeSubject(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(teacher.getSubjects()).doesNotContain(subject);
        verify(unitOfWork, times(2)).getTeacherRepository();
        verify(unitOfWork).getSubjectRepository();
        verify(teacherRepository).findById(1L);
        verify(subjectRepository).findById(1L);
        verify(teacherRepository).save(teacher);
        verify(teacherMapper).toDto(updatedTeacher);
    }

    @Test
    @DisplayName("Should activate teacher")
    void shouldActivateTeacher() {
        // Given
        teacher.setIsActive(false);
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(1L);
        updatedTeacher.setUser(user);
        updatedTeacher.setIsActive(true);

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(teacher)).thenReturn(updatedTeacher);
        when(teacherMapper.toDto(updatedTeacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.activateTeacher(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(teacher.getIsActive()).isTrue();
        verify(unitOfWork, times(2)).getTeacherRepository();
        verify(teacherRepository).findById(1L);
        verify(teacherRepository).save(teacher);
        verify(teacherMapper).toDto(updatedTeacher);
    }

    @Test
    @DisplayName("Should deactivate teacher")
    void shouldDeactivateTeacher() {
        // Given
        teacher.setIsActive(true);
        Teacher updatedTeacher = new Teacher();
        updatedTeacher.setId(1L);
        updatedTeacher.setUser(user);
        updatedTeacher.setIsActive(false);

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(teacherRepository.save(teacher)).thenReturn(updatedTeacher);
        when(teacherMapper.toDto(updatedTeacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.deactivateTeacher(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(teacher.getIsActive()).isFalse();
        verify(unitOfWork, times(2)).getTeacherRepository();
        verify(teacherRepository).findById(1L);
        verify(teacherRepository).save(teacher);
        verify(teacherMapper).toDto(updatedTeacher);
    }

    @Test
    @DisplayName("Should find subjects by teacher ID")
    void shouldFindSubjectsByTeacherId() {
        // Given
        teacher.setSubjects(new java.util.HashSet<>(Arrays.asList(subject)));

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(subjectMapper.toDtoList(anyList())).thenReturn(Arrays.asList(subjectDto));

        // When
        List<SubjectDto> result = teacherService.findSubjectsByTeacherId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSubjectName()).isEqualTo("Mathematics");
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findById(1L);
        verify(subjectMapper).toDtoList(anyList());
    }

    @Test
    @DisplayName("Should find grades by teacher ID")
    void shouldFindGradesByTeacherId() {
        // Given
        List<Grade> grades = Arrays.asList(grade);

        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(gradeRepository.findByTeacherIdOrderByGradeDateDesc(1L)).thenReturn(grades);
        when(gradeMapper.toDtoList(grades)).thenReturn(Arrays.asList(gradeDto));

        // When
        List<GradeDto> result = teacherService.findGradesByTeacherId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGradeValue()).isEqualTo(85);
        verify(unitOfWork).getTeacherRepository();
        verify(unitOfWork).getGradeRepository();
        verify(teacherRepository).findById(1L);
        verify(gradeRepository).findByTeacherIdOrderByGradeDateDesc(1L);
        verify(gradeMapper).toDtoList(grades);
    }

    @Test
    @DisplayName("Should find students by teacher ID")
    void shouldFindStudentsByTeacherId() {
        // Given
        List<Student> students = Arrays.asList(student);

        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentRepository.findStudentsByTeacherId(1L)).thenReturn(students);
        when(studentService.findById(1L)).thenReturn(studentDto);

        // When
        List<StudentDto> result = teacherService.findStudentsByTeacherId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUser().getFirstName()).isEqualTo("Jane");
        verify(unitOfWork).getStudentRepository();
        verify(studentRepository).findStudentsByTeacherId(1L);
        verify(studentService).findById(1L);
    }

    @Test
    @DisplayName("Should create teacher with user")
    void shouldCreateTeacherWithUser() {
        // Given
        UserDto newUserDto = new UserDto();
        newUserDto.setEmail("test@university.edu");
        String password = "password123";

        Teacher savedTeacher = new Teacher();
        savedTeacher.setId(2L);
        savedTeacher.setUser(user);
        savedTeacher.setAcademicTitle("Professor");
        savedTeacher.setDepartmentPosition("Lecturer");

        when(teacherMapper.toEntity(teacherDto)).thenReturn(savedTeacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.save(savedTeacher)).thenReturn(savedTeacher);
        when(teacherMapper.toDto(savedTeacher)).thenReturn(teacherDto);

        // When
        TeacherDto result = teacherService.createWithUser(newUserDto, password, teacherDto);

        // Then
        assertThat(result).isNotNull();
        verify(teacherMapper).toEntity(teacherDto);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).save(savedTeacher);
        verify(teacherMapper).toDto(savedTeacher);
    }

    @Test
    @DisplayName("Should find teachers by academic title")
    void shouldFindTeachersByAcademicTitle() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<TeacherDto> result = teacherService.findByAcademicTitle("Professor");

        // Then
        assertThat(result).hasSize(1);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Should find teachers by department position")
    void shouldFindTeachersByDepartmentPosition() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<TeacherDto> result = teacherService.findByDepartmentPosition("Head of Department");

        // Then
        assertThat(result).hasSize(1);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findAll();
    }

    @Test
    @DisplayName("Should find teachers by subject ID")
    void shouldFindTeachersBySubjectId() {
        // Given
        List<Teacher> teachers = Arrays.asList(teacher);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(teacherRepository.findAll()).thenReturn(teachers);

        // When
        List<TeacherDto> result = teacherService.findBySubjectId(1L);

        // Then
        assertThat(result).hasSize(1);
        verify(unitOfWork).getTeacherRepository();
        verify(teacherRepository).findAll();
    }
}
