package com.kursova.bll.services;

import com.kursova.bll.dto.GradeDto;
import com.kursova.bll.mappers.GradeMapper;
import com.kursova.bll.services.impl.GradeServiceImpl;
import com.kursova.dal.entities.Grade;
import com.kursova.dal.entities.GradeType;
import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.Subject;
import com.kursova.dal.entities.Teacher;
import com.kursova.dal.repositories.GradeRepository;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for GradeService
 * Tests follow the Triple A pattern (Arrange-Act-Assert)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Grade Service Tests")
class GradeServiceTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private GradeRepository gradeRepository;
    
    @Mock
    private com.kursova.dal.repositories.StudentRepository studentRepository;
    
    @Mock
    private com.kursova.dal.repositories.TeacherRepository teacherRepository;
    
    @Mock
    private com.kursova.dal.repositories.SubjectRepository subjectRepository;
    
    @Mock
    private com.kursova.dal.repositories.ArchivedGradeRepository archivedGradeRepository;

    @Mock
    private GradeMapper gradeMapper;

    @InjectMocks
    private GradeServiceImpl gradeService;

    private Grade testGrade;
    private GradeDto testGradeDto;
    private Student testStudent;
    private Teacher testTeacher;
    private Subject testSubject;

    @BeforeEach
    void setUp() {
        // Arrange - Setup test data
        testStudent = new Student();
        testStudent.setId(1L);

        testTeacher = new Teacher();
        testTeacher.setId(1L);

        testSubject = new Subject();
        testSubject.setId(1L);

        testGrade = new Grade();
        testGrade.setId(1L);
        testGrade.setGradeValue(85);
        testGrade.setGradeType(GradeType.MIDTERM);
        testGrade.setStudent(testStudent);
        testGrade.setTeacher(testTeacher);
        testGrade.setSubject(testSubject);
        testGrade.setGradeDate(LocalDateTime.now());

        testGradeDto = new GradeDto();
        testGradeDto.setId(1L);
        testGradeDto.setGradeValue(85);
        testGradeDto.setGradeType(GradeType.MIDTERM);
        testGradeDto.setStudentId(1L);
        testGradeDto.setTeacherId(1L);
        testGradeDto.setSubjectId(1L);
    }

    @Test
    @DisplayName("Should find grade by ID successfully")
    void findById_ShouldReturnGrade_WhenGradeExists() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(testGrade));
        when(gradeMapper.toDto(testGrade)).thenReturn(testGradeDto);

        // Act
        GradeDto result = gradeService.findById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getGradeValue()).isEqualTo(85);
        verify(gradeRepository).findById(1L);
        verify(gradeMapper).toDto(testGrade);
    }

    @Test
    @DisplayName("Should throw exception when grade not found by ID")
    void findById_ShouldThrowException_WhenGradeNotExists() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gradeService.findById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Grade not found with id: 999");
    }

    @Test
    @DisplayName("Should find all grades by student ID")
    void findByStudentId_ShouldReturnGrades_WhenStudentHasGrades() {
        // Arrange
        List<Grade> grades = Collections.singletonList(testGrade);

        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findByStudentIdOrderByGradeDateDesc(1L)).thenReturn(grades);
        when(gradeMapper.toDto(testGrade)).thenReturn(testGradeDto);

        // Act
        List<GradeDto> result = gradeService.findByStudentId(1L);

        // Assert
        assertThat(result)
            .isNotNull()
            .hasSize(1);
        assertThat(result.get(0).getStudentId()).isEqualTo(1L);
        verify(gradeRepository).findByStudentIdOrderByGradeDateDesc(1L);
    }

    @Test
    @DisplayName("Should find all grades by teacher ID")
    void findByTeacherId_ShouldReturnGrades_WhenTeacherHasGrades() {
        // Arrange
        List<Grade> grades = Collections.singletonList(testGrade);

        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findGradesByTeacherId(1L)).thenReturn(grades);
        when(gradeMapper.toDto(testGrade)).thenReturn(testGradeDto);

        // Act
        List<GradeDto> result = gradeService.findByTeacherId(1L);

        // Assert
        assertThat(result)
            .isNotNull()
            .hasSize(1);
        assertThat(result.get(0).getTeacherId()).isEqualTo(1L);
        verify(gradeRepository).findGradesByTeacherId(1L);
    }

    @Test
    @DisplayName("Should create new grade successfully")
    void create_ShouldReturnCreatedGrade_WhenValidGradeProvided() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(unitOfWork.getSubjectRepository()).thenReturn(subjectRepository);
        
        when(gradeMapper.toEntity(testGradeDto)).thenReturn(testGrade);
        when(studentRepository.findById(1L)).thenReturn(Optional.of(testStudent));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(testTeacher));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(testSubject));
        when(gradeRepository.save(any(Grade.class))).thenReturn(testGrade);
        when(gradeRepository.findByIdWithRelations(1L)).thenReturn(Optional.of(testGrade));
        when(gradeMapper.toDto(testGrade)).thenReturn(testGradeDto);

        // Act
        GradeDto result = gradeService.create(testGradeDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(gradeRepository).save(any(Grade.class));
        verify(gradeMapper).toEntity(testGradeDto);
        verify(gradeMapper).toDto(testGrade);
    }

    @Test
    @DisplayName("Should update existing grade successfully")
    void update_ShouldReturnUpdatedGrade_WhenValidGradeProvided() {
        // Arrange
        GradeDto updatedGradeDto = new GradeDto();
        updatedGradeDto.setId(1L);
        updatedGradeDto.setGradeValue(90);

        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(unitOfWork.getArchivedGradeRepository()).thenReturn(archivedGradeRepository);
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(testGrade));
        when(gradeRepository.save(any(Grade.class))).thenReturn(testGrade);
        when(archivedGradeRepository.save(any())).thenReturn(null); // We don't care about archived result
        when(gradeMapper.toDto(testGrade)).thenReturn(updatedGradeDto);

        // Act
        GradeDto result = gradeService.update(1L, updatedGradeDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getGradeValue()).isEqualTo(90);
        verify(gradeRepository).findById(1L);
        verify(gradeRepository).save(any(Grade.class));
        verify(archivedGradeRepository).save(any());
    }

    @Test
    @DisplayName("Should delete grade successfully")
    void delete_ShouldDeleteGrade_WhenGradeExists() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(unitOfWork.getArchivedGradeRepository()).thenReturn(archivedGradeRepository);
        when(gradeRepository.findById(1L)).thenReturn(Optional.of(testGrade));
        when(archivedGradeRepository.save(any())).thenReturn(null); // We don't care about archived result

        // Act
        gradeService.delete(1L);

        // Assert
        verify(gradeRepository).findById(1L);
        verify(archivedGradeRepository).save(any());
        verify(gradeRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent grade")
    void delete_ShouldThrowException_WhenGradeNotExists() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> gradeService.delete(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Grade not found with id: 999");
        
        verify(gradeRepository).findById(999L);
        verify(gradeRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should find grades by student and subject")
    void findByStudentAndSubject_ShouldReturnGrades_WhenGradesExist() {
        // Arrange
        List<Grade> grades = Collections.singletonList(testGrade);

        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findByStudentIdAndSubjectIdOrderByGradeDateDesc(1L, 1L)).thenReturn(grades);
        when(gradeMapper.toDto(testGrade)).thenReturn(testGradeDto);

        // Act
        List<GradeDto> result = gradeService.findByStudentAndSubject(1L, 1L);

        // Assert
        assertThat(result)
            .isNotNull()
            .hasSize(1);
        assertThat(result.get(0))
            .extracting(GradeDto::getStudentId, GradeDto::getSubjectId)
            .containsExactly(1L, 1L);
    }

    @Test
    @DisplayName("Should calculate average grade for student in subject")
    void getAverageGradeForStudentInSubject_ShouldReturnAverage_WhenGradesExist() {
        // Arrange
        Double expectedAverage = 85.5;

        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.getAverageGradeForStudentInSubject(1L, 1L)).thenReturn(expectedAverage);

        // Act
        Double result = gradeService.getAverageGradeForStudentInSubject(1L, 1L);

        // Assert
        assertThat(result).isEqualTo(expectedAverage);
        verify(gradeRepository).getAverageGradeForStudentInSubject(1L, 1L);
    }

    @Test
    @DisplayName("Should calculate overall average grade for student")
    void getOverallAverageGradeForStudent_ShouldReturnAverage_WhenGradesExist() {
        // Arrange
        Double expectedAverage = 82.7;

        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.getOverallAverageGradeForStudent(1L)).thenReturn(expectedAverage);

        // Act
        Double result = gradeService.getOverallAverageGradeForStudent(1L);

        // Assert
        assertThat(result).isEqualTo(expectedAverage);
        verify(gradeRepository).getOverallAverageGradeForStudent(1L);
    }

    @Test
    @DisplayName("Should find final grades by student")
    void findFinalGradesByStudent_ShouldReturnFinalGrades_WhenFinalGradesExist() {
        // Arrange
        testGrade.setIsFinal(true);
        testGradeDto.setIsFinal(true);
        List<Grade> finalGrades = Collections.singletonList(testGrade);

        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.findByStudentIdAndIsFinalTrueOrderBySubjectSubjectNameAsc(1L)).thenReturn(finalGrades);
        when(gradeMapper.toDto(testGrade)).thenReturn(testGradeDto);

        // Act
        List<GradeDto> result = gradeService.findFinalGradesByStudent(1L);

        // Assert
        assertThat(result)
            .isNotNull()
            .hasSize(1);
        assertThat(result.get(0).getIsFinal()).isTrue();
    }

    @Test
    @DisplayName("Should check if grade exists by ID")
    void existsById_ShouldReturnTrue_WhenGradeExists() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean result = gradeService.existsById(1L);

        // Assert
        assertThat(result).isTrue();
        verify(gradeRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should check if grade exists by ID - not found")
    void existsById_ShouldReturnFalse_WhenGradeNotExists() {
        // Arrange
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);
        when(gradeRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean result = gradeService.existsById(999L);

        // Assert
        assertThat(result).isFalse();
        verify(gradeRepository).existsById(999L);
    }
}
