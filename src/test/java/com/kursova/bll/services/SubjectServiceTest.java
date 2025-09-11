package com.kursova.bll.services;

import com.kursova.bll.dto.SubjectDto;
import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.mappers.SubjectMapper;
import com.kursova.bll.mappers.TeacherMapper;
import com.kursova.bll.mappers.StudentGroupMapper;
import com.kursova.bll.services.impl.SubjectServiceImpl;
import com.kursova.dal.entities.Subject;
import com.kursova.dal.entities.Teacher;
import com.kursova.dal.entities.StudentGroup;
import com.kursova.dal.entities.AssessmentType;
import com.kursova.dal.repositories.SubjectRepository;
import com.kursova.dal.repositories.TeacherRepository;
import com.kursova.dal.repositories.StudentGroupRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for SubjectService
 * Tests all subject operations without Spring context
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Subject Service Tests")
@SuppressWarnings("null") // Suppress null pointer warnings in tests where we control the mock behavior
class SubjectServiceTest {

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private StudentGroupRepository groupRepository;

    @Mock
    private SubjectMapper subjectMapper;

    @Mock
    private TeacherMapper teacherMapper;

    @Mock
    private StudentGroupMapper groupMapper;

    @InjectMocks
    private SubjectServiceImpl subjectService;

    private Subject subject;
    private SubjectDto subjectDto;
    private Teacher teacher;
    private StudentGroup group;

    @BeforeEach
    void setUp() {
        // Setup test data
        subject = new Subject("Mathematics", "MATH101", 5, AssessmentType.EXAM);
        subject.setId(1L);
        subject.setIsActive(true);
        subject.setTeachers(new HashSet<>());
        subject.setGroups(new HashSet<>());

        subjectDto = new SubjectDto("Mathematics", "MATH101", 5, AssessmentType.EXAM);
        subjectDto.setId(1L);
        subjectDto.setIsActive(true);

        teacher = new Teacher();
        teacher.setId(1L);
        teacher.setIsActive(true);

        group = new StudentGroup();
        group.setId(1L);
        group.setIsActive(true);
    }

    @Test
    @DisplayName("Should create subject successfully")
    void shouldCreateSubjectSuccessfully() {
        // Given
        when(subjectMapper.toEntity(subjectDto)).thenReturn(subject);
        when(subjectRepository.existsBySubjectCode(subjectDto.getSubjectCode())).thenReturn(false);
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        SubjectDto result = subjectService.create(subjectDto);

        // Then
        assertThat(result).isEqualTo(subjectDto);
        verify(subjectRepository).save(subject);
        verify(subjectMapper).toDto(subject);
    }

    @Test
    @DisplayName("Should throw exception when creating subject with existing code")
    void shouldThrowExceptionWhenCreatingSubjectWithExistingCode() {
        // Given
        when(subjectRepository.existsBySubjectCode(subjectDto.getSubjectCode())).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> subjectService.create(subjectDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Subject code already exists: " + subjectDto.getSubjectCode());
    }

    @Test
    @DisplayName("Should find subject by id")
    void shouldFindSubjectById() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        SubjectDto result = subjectService.findById(1L);

        // Then
        assertThat(result).isEqualTo(subjectDto);
        verify(subjectRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when subject not found by id")
    void shouldThrowExceptionWhenSubjectNotFoundById() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subjectService.findById(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Subject not found with id: 1");
    }

    @Test
    @DisplayName("Should find all subjects")
    void shouldFindAllSubjects() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findAll()).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should update subject successfully")
    void shouldUpdateSubjectSuccessfully() {
        // Given
        SubjectDto updateDto = new SubjectDto();
        updateDto.setSubjectName("Advanced Mathematics");
        updateDto.setCredits(6);

        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        SubjectDto result = subjectService.update(1L, updateDto);

        // Then
        assertThat(result).isEqualTo(subjectDto);
        assertThat(subject.getSubjectName()).isEqualTo("Advanced Mathematics");
        assertThat(subject.getCredits()).isEqualTo(6);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent subject")
    void shouldThrowExceptionWhenUpdatingNonExistentSubject() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subjectService.update(1L, subjectDto))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Subject not found with id: 1");
    }

    @Test
    @DisplayName("Should delete subject successfully")
    void shouldDeleteSubjectSuccessfully() {
        // Given
        when(subjectRepository.existsById(1L)).thenReturn(true);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));

        // When
        subjectService.delete(1L);

        // Then
        verify(subjectRepository).findById(1L);
        verify(subjectRepository).save(subject);
        assertThat(subject.getIsActive()).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent subject")
    void shouldThrowExceptionWhenDeletingNonExistentSubject() {
        // Given
        when(subjectRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> subjectService.delete(1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Subject not found with id: 1");
    }

    @Test
    @DisplayName("Should check if subject exists by id")
    void shouldCheckIfSubjectExistsById() {
        // Given
        when(subjectRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = subjectService.existsById(1L);

        // Then
        assertThat(result).isTrue();
        verify(subjectRepository).existsById(1L);
    }

    @Test
    @DisplayName("Should find subject by subject code")
    void shouldFindSubjectBySubjectCode() {
        // Given
        when(subjectRepository.findBySubjectCode("MATH101")).thenReturn(Optional.of(subject));
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        SubjectDto result = subjectService.findBySubjectCode("MATH101");

        // Then
        assertThat(result).isEqualTo(subjectDto);
        verify(subjectRepository).findBySubjectCode("MATH101");
    }

    @Test
    @DisplayName("Should find active subjects")
    void shouldFindActiveSubjects() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findActiveSubjectsWithGroups()).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findActiveSubjects();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should search subjects by name")
    void shouldSearchSubjectsByName() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.searchByName("Math")).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.searchByName("Math");

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should find subjects by assessment type")
    void shouldFindSubjectsByAssessmentType() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findByAssessmentTypeAndIsActiveTrueOrderBySubjectNameAsc(AssessmentType.EXAM))
            .thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findByAssessmentType(AssessmentType.EXAM);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should find subjects by semester")
    void shouldFindSubjectsBySemester() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findBySemesterAndIsActiveTrueOrderBySubjectNameAsc(1)).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findBySemester(1);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should find subjects by credits")
    void shouldFindSubjectsByCredits() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findByCreditsAndIsActiveTrueOrderBySubjectNameAsc(5)).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findByCredits(5);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should find subjects by teacher id")
    void shouldFindSubjectsByTeacherId() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findByTeacherIdWithGroups(1L)).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findByTeacherId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should find subjects by group id")
    void shouldFindSubjectsByGroupId() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findByGroupId(1L)).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findByGroupId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should check if subject code exists")
    void shouldCheckIfSubjectCodeExists() {
        // Given
        when(subjectRepository.existsBySubjectCode("MATH101")).thenReturn(true);

        // When
        boolean result = subjectService.existsBySubjectCode("MATH101");

        // Then
        assertThat(result).isTrue();
        verify(subjectRepository).existsBySubjectCode("MATH101");
    }

    @Test
    @DisplayName("Should find subjects with grades for student")
    void shouldFindSubjectsWithGradesForStudent() {
        // Given
        List<Subject> subjects = Arrays.asList(subject);
        when(subjectRepository.findSubjectsWithGradesForStudent(1L)).thenReturn(subjects);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        List<SubjectDto> result = subjectService.findSubjectsWithGradesForStudent(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(subjectDto);
    }

    @Test
    @DisplayName("Should assign teacher to subject")
    void shouldAssignTeacherToSubject() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(subjectRepository.save(subject)).thenReturn(subject);

        // When
        subjectService.assignTeacher(1L, 1L);

        // Then
        assertThat(subject.getTeachers()).contains(teacher);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("Should throw exception when assigning teacher to non-existent subject")
    void shouldThrowExceptionWhenAssigningTeacherToNonExistentSubject() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subjectService.assignTeacher(1L, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Subject not found with id: 1");
    }

    @Test
    @DisplayName("Should throw exception when assigning non-existent teacher to subject")
    void shouldThrowExceptionWhenAssigningNonExistentTeacherToSubject() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> subjectService.assignTeacher(1L, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Teacher not found with id: 1");
    }

    @Test
    @DisplayName("Should throw exception when assigning inactive teacher to subject")
    void shouldThrowExceptionWhenAssigningInactiveTeacherToSubject() {
        // Given
        teacher.setIsActive(false);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));

        // When & Then
        assertThatThrownBy(() -> subjectService.assignTeacher(1L, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Cannot assign inactive teacher to subject");
    }

    @Test
    @DisplayName("Should remove teacher from subject")
    void shouldRemoveTeacherFromSubject() {
        // Given
        subject.getTeachers().add(teacher);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.findById(1L)).thenReturn(Optional.of(teacher));
        when(subjectRepository.save(subject)).thenReturn(subject);

        // When
        subjectService.removeTeacher(1L, 1L);

        // Then
        assertThat(subject.getTeachers()).doesNotContain(teacher);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("Should activate subject")
    void shouldActivateSubject() {
        // Given
        subject.setIsActive(false);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        SubjectDto result = subjectService.activateSubject(1L);

        // Then
        assertThat(subject.getIsActive()).isTrue();
        assertThat(result).isEqualTo(subjectDto);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("Should deactivate subject")
    void shouldDeactivateSubject() {
        // Given
        subject.setIsActive(true);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.save(subject)).thenReturn(subject);
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        SubjectDto result = subjectService.deactivateSubject(1L);

        // Then
        assertThat(subject.getIsActive()).isFalse();
        assertThat(result).isEqualTo(subjectDto);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("Should get assigned groups")
    void shouldGetAssignedGroups() {
        // Given
        subject.getGroups().add(group);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(groupMapper.toDto(group)).thenReturn(new com.kursova.bll.dto.StudentGroupDto());

        // When
        List<Object> result = subjectService.getAssignedGroups(1L);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should get available groups")
    void shouldGetAvailableGroups() {
        // Given
        List<StudentGroup> allGroups = Arrays.asList(group);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(groupRepository.findAll()).thenReturn(allGroups);
        when(groupMapper.toDto(group)).thenReturn(new com.kursova.bll.dto.StudentGroupDto());

        // When
        List<Object> result = subjectService.getAvailableGroups(1L);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should add group to subject")
    void shouldAddGroupToSubject() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(subjectRepository.save(subject)).thenReturn(subject);

        // When
        subjectService.addGroupToSubject(1L, 1L);

        // Then
        assertThat(subject.getGroups()).contains(group);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("Should remove group from subject")
    void shouldRemoveGroupFromSubject() {
        // Given
        subject.getGroups().add(group);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group));
        when(subjectRepository.save(subject)).thenReturn(subject);

        // When
        subjectService.removeGroupFromSubject(1L, 1L);

        // Then
        assertThat(subject.getGroups()).doesNotContain(group);
        verify(subjectRepository).save(subject);
    }

    @Test
    @DisplayName("Should get assigned teachers")
    void shouldGetAssignedTeachers() {
        // Given
        subject.getTeachers().add(teacher);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherMapper.toDto(teacher)).thenReturn(new TeacherDto());

        // When
        List<Object> result = subjectService.getAssignedTeachers(1L);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should get available teachers")
    void shouldGetAvailableTeachers() {
        // Given
        List<Teacher> allTeachers = Arrays.asList(teacher);
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(teacherRepository.findAll()).thenReturn(allTeachers);
        when(teacherMapper.toDto(teacher)).thenReturn(new TeacherDto());

        // When
        List<Object> result = subjectService.getAvailableTeachers(1L);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("Should handle exception in enrichWithTeachers method")
    void shouldHandleExceptionInEnrichWithTeachers() {
        // Given
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(subject));
        when(subjectRepository.countGroupsBySubjectId(1L)).thenThrow(new RuntimeException("Database error"));
        when(subjectMapper.toDto(subject)).thenReturn(subjectDto);

        // When
        SubjectDto result = subjectService.findById(1L);

        // Then
        assertThat(result).isEqualTo(subjectDto);
        // Should fallback to groups.size() when count query fails
    }
}
