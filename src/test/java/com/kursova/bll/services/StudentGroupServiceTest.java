package com.kursova.bll.services;

import com.kursova.bll.dto.StudentGroupDto;
import com.kursova.bll.mappers.StudentGroupMapper;
import com.kursova.bll.services.impl.StudentGroupServiceImpl;
import com.kursova.dal.entities.StudentGroup;
import com.kursova.dal.repositories.StudentGroupRepository;
import com.kursova.dal.repositories.StudentRepository;
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
 * Unit tests for StudentGroupService
 * Tests all student group operations without Spring context
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Student Group Service Tests")
@SuppressWarnings("null") // Suppress null pointer warnings in tests where we control the mock behavior
class StudentGroupServiceTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private StudentGroupRepository studentGroupRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentGroupMapper groupMapper;

    @Mock
    private ArchiveService archiveService;

    @InjectMocks
    private StudentGroupServiceImpl studentGroupService;

    private StudentGroup studentGroup;
    private StudentGroupDto studentGroupDto;

    @BeforeEach
    void setUp() {
        // Setup test data
        studentGroup = new StudentGroup();
        studentGroup.setId(1L);
        studentGroup.setGroupName("CS-101");
        studentGroup.setGroupCode("CS101");
        studentGroup.setCourseYear(1);
        studentGroup.setEnrollmentYear(2023);
        studentGroup.setIsActive(true);

        studentGroupDto = new StudentGroupDto();
        studentGroupDto.setId(1L);
        studentGroupDto.setGroupName("CS-101");
        studentGroupDto.setGroupCode("CS101");
        studentGroupDto.setCourseYear(1);
        studentGroupDto.setEnrollmentYear(2023);
        studentGroupDto.setIsActive(true);
    }

    @Test
    @DisplayName("Should find all student groups")
    void shouldFindAll() {
        // Given
        List<StudentGroup> groups = Arrays.asList(studentGroup);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findAll()).thenReturn(groups);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        List<StudentGroupDto> result = studentGroupService.findAll();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getGroupName()).isEqualTo("CS-101");
        verify(unitOfWork.getStudentGroupRepository()).findAll();
        verify(groupMapper).toDto(studentGroup);
    }

    @Test
    @DisplayName("Should find student group by id")
    void shouldFindById() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.of(studentGroup));
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        StudentGroupDto result = studentGroupService.findById(1L);

        // Then
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getGroupName()).isEqualTo("CS-101");
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
        verify(groupMapper).toDto(studentGroup);
    }

    @Test
    @DisplayName("Should throw exception when student group not found by id")
    void shouldThrowExceptionWhenGroupNotFoundById() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentGroupService.findById(1L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("StudentGroup not found with id: 1");
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
    }

    @Test
    @DisplayName("Should create student group successfully")
    void shouldCreateStudentGroup() {
        // Given
        StudentGroupDto newGroupDto = new StudentGroupDto();
        newGroupDto.setGroupName("CS-102");
        newGroupDto.setGroupCode("CS102");
        newGroupDto.setCourseYear(1);

        StudentGroup newGroup = new StudentGroup();
        newGroup.setId(2L);
        newGroup.setGroupName("CS-102");
        newGroup.setGroupCode("CS102");
        newGroup.setCourseYear(1);

        StudentGroupDto savedGroupDto = new StudentGroupDto();
        savedGroupDto.setId(2L);
        savedGroupDto.setGroupName("CS-102");
        savedGroupDto.setGroupCode("CS102");
        savedGroupDto.setCourseYear(1);

        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.existsByGroupCode("CS102")).thenReturn(false);
        when(groupMapper.toEntity(newGroupDto)).thenReturn(newGroup);
        when(studentGroupRepository.save(newGroup)).thenReturn(newGroup);
        when(groupMapper.toDto(newGroup)).thenReturn(savedGroupDto);

        // When
        StudentGroupDto result = studentGroupService.create(newGroupDto);

        // Then
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getGroupName()).isEqualTo("CS-102");
        verify(unitOfWork.getStudentGroupRepository()).existsByGroupCode("CS102");
        verify(studentGroupRepository).save(newGroup);
        verify(groupMapper).toEntity(newGroupDto);
        verify(groupMapper).toDto(newGroup);
    }

    @Test
    @DisplayName("Should throw exception when creating group with existing code")
    void shouldThrowExceptionWhenCreatingGroupWithExistingCode() {
        // Given
        StudentGroupDto newGroupDto = new StudentGroupDto();
        newGroupDto.setGroupName("CS-102");
        newGroupDto.setGroupCode("CS102");

        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.existsByGroupCode("CS102")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> studentGroupService.create(newGroupDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Group code already exists: CS102");
        verify(unitOfWork.getStudentGroupRepository()).existsByGroupCode("CS102");
        verify(studentGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update student group successfully")
    void shouldUpdateStudentGroup() {
        // Given
        StudentGroupDto updateDto = new StudentGroupDto();
        updateDto.setGroupName("CS-101-Updated");
        updateDto.setGroupCode("CS102"); // Different code to trigger existsByGroupCode check
        updateDto.setCourseYear(2);

        StudentGroup updatedGroup = new StudentGroup();
        updatedGroup.setId(1L);
        updatedGroup.setGroupName("CS-101-Updated");
        updatedGroup.setGroupCode("CS102");
        updatedGroup.setCourseYear(2);

        StudentGroupDto updatedGroupDto = new StudentGroupDto();
        updatedGroupDto.setId(1L);
        updatedGroupDto.setGroupName("CS-101-Updated");
        updatedGroupDto.setGroupCode("CS102");
        updatedGroupDto.setCourseYear(2);

        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.of(studentGroup));
        when(studentGroupRepository.existsByGroupCode("CS102")).thenReturn(false);
        when(studentGroupRepository.save(studentGroup)).thenReturn(updatedGroup);
        when(groupMapper.toDto(updatedGroup)).thenReturn(updatedGroupDto);

        // When
        StudentGroupDto result = studentGroupService.update(1L, updateDto);

        // Then
        assertThat(result.getGroupName()).isEqualTo("CS-101-Updated");
        assertThat(result.getGroupCode()).isEqualTo("CS102");
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
        verify(studentGroupRepository).existsByGroupCode("CS102");
        verify(groupMapper).updateEntityFromDto(updateDto, studentGroup);
        verify(studentGroupRepository).save(studentGroup);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent group")
    void shouldThrowExceptionWhenUpdatingNonExistentGroup() {
        // Given
        StudentGroupDto updateDto = new StudentGroupDto();
        updateDto.setGroupName("CS-101-Updated");

        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentGroupService.update(1L, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("StudentGroup not found with id: 1");
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
        verify(studentGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw exception when updating group with existing code")
    void shouldThrowExceptionWhenUpdatingGroupWithExistingCode() {
        // Given
        StudentGroupDto updateDto = new StudentGroupDto();
        updateDto.setGroupName("CS-101-Updated");
        updateDto.setGroupCode("CS102"); // Different code

        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.of(studentGroup));
        when(studentGroupRepository.existsByGroupCode("CS102")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> studentGroupService.update(1L, updateDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Group code already exists: CS102");
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
        verify(studentGroupRepository).existsByGroupCode("CS102");
        verify(studentGroupRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete student group")
    void shouldDeleteStudentGroup() {
        // When
        studentGroupService.delete(1L);

        // Then
        verify(archiveService).archiveStudentGroup(1L, "SYSTEM", "Group deleted by user");
    }

    @Test
    @DisplayName("Should check if group exists by id")
    void shouldCheckIfGroupExistsById() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.existsById(1L)).thenReturn(true);

        // When
        boolean result = studentGroupService.existsById(1L);

        // Then
        assertThat(result).isTrue();
        verify(unitOfWork.getStudentGroupRepository()).existsById(1L);
    }

    @Test
    @DisplayName("Should find active groups")
    void shouldFindActiveGroups() {
        // Given
        List<StudentGroup> activeGroups = Arrays.asList(studentGroup);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findByIsActiveTrue()).thenReturn(activeGroups);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        List<StudentGroupDto> result = studentGroupService.findActiveGroups();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getIsActive()).isTrue();
        verify(unitOfWork.getStudentGroupRepository()).findByIsActiveTrue();
    }

    @Test
    @DisplayName("Should search groups by name")
    void shouldSearchGroupsByName() {
        // Given
        List<StudentGroup> foundGroups = Arrays.asList(studentGroup);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findByGroupNameContainingIgnoreCase("CS")).thenReturn(foundGroups);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        List<StudentGroupDto> result = studentGroupService.searchByName("CS");

        // Then
        assertThat(result).hasSize(1);
        verify(unitOfWork.getStudentGroupRepository()).findByGroupNameContainingIgnoreCase("CS");
    }

    @Test
    @DisplayName("Should search groups by name or code")
    void shouldSearchGroupsByNameOrCode() {
        // Given
        List<StudentGroup> foundGroups = Arrays.asList(studentGroup);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findByGroupNameContainingIgnoreCaseOrGroupCodeContainingIgnoreCase("CS", "CS")).thenReturn(foundGroups);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        List<StudentGroupDto> result = studentGroupService.searchByNameOrCode("CS");

        // Then
        assertThat(result).hasSize(1);
        verify(unitOfWork.getStudentGroupRepository()).findByGroupNameContainingIgnoreCaseOrGroupCodeContainingIgnoreCase("CS", "CS");
    }

    @Test
    @DisplayName("Should find group by group code")
    void shouldFindGroupByGroupCode() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findByGroupCode("CS101")).thenReturn(Optional.of(studentGroup));
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        StudentGroupDto result = studentGroupService.findByGroupCode("CS101");

        // Then
        assertThat(result.getGroupCode()).isEqualTo("CS101");
        verify(unitOfWork.getStudentGroupRepository()).findByGroupCode("CS101");
    }

    @Test
    @DisplayName("Should throw exception when group not found by code")
    void shouldThrowExceptionWhenGroupNotFoundByCode() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findByGroupCode("INVALID")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> studentGroupService.findByGroupCode("INVALID"))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("StudentGroup not found with code: INVALID");
        verify(unitOfWork.getStudentGroupRepository()).findByGroupCode("INVALID");
    }

    @Test
    @DisplayName("Should check if group code exists")
    void shouldCheckIfGroupCodeExists() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.existsByGroupCode("CS101")).thenReturn(true);

        // When
        boolean result = studentGroupService.existsByGroupCode("CS101");

        // Then
        assertThat(result).isTrue();
        verify(unitOfWork.getStudentGroupRepository()).existsByGroupCode("CS101");
    }

    @Test
    @DisplayName("Should find groups by enrollment year")
    void shouldFindGroupsByEnrollmentYear() {
        // Given
        List<StudentGroup> groups = Arrays.asList(studentGroup);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findByEnrollmentYear(2023)).thenReturn(groups);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        List<StudentGroupDto> result = studentGroupService.findByEnrollmentYear(2023);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEnrollmentYear()).isEqualTo(2023);
        verify(unitOfWork.getStudentGroupRepository()).findByEnrollmentYear(2023);
    }

    @Test
    @DisplayName("Should activate group")
    void shouldActivateGroup() {
        // Given
        StudentGroup inactiveGroup = new StudentGroup();
        inactiveGroup.setId(1L);
        inactiveGroup.setGroupName("CS-101");
        inactiveGroup.setIsActive(false);

        StudentGroup activatedGroup = new StudentGroup();
        activatedGroup.setId(1L);
        activatedGroup.setGroupName("CS-101");
        activatedGroup.setIsActive(true);

        StudentGroupDto activatedGroupDto = new StudentGroupDto();
        activatedGroupDto.setId(1L);
        activatedGroupDto.setGroupName("CS-101");
        activatedGroupDto.setIsActive(true);

        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.of(inactiveGroup));
        when(studentGroupRepository.save(inactiveGroup)).thenReturn(activatedGroup);
        when(groupMapper.toDto(activatedGroup)).thenReturn(activatedGroupDto);

        // When
        StudentGroupDto result = studentGroupService.activateGroup(1L);

        // Then
        assertThat(result.getIsActive()).isTrue();
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
        verify(studentGroupRepository).save(inactiveGroup);
    }

    @Test
    @DisplayName("Should deactivate group")
    void shouldDeactivateGroup() {
        // Given
        StudentGroup activeGroup = new StudentGroup();
        activeGroup.setId(1L);
        activeGroup.setGroupName("CS-101");
        activeGroup.setIsActive(true);

        StudentGroup deactivatedGroup = new StudentGroup();
        deactivatedGroup.setId(1L);
        deactivatedGroup.setGroupName("CS-101");
        deactivatedGroup.setIsActive(false);

        StudentGroupDto deactivatedGroupDto = new StudentGroupDto();
        deactivatedGroupDto.setId(1L);
        deactivatedGroupDto.setGroupName("CS-101");
        deactivatedGroupDto.setIsActive(false);

        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.of(activeGroup));
        when(studentGroupRepository.save(activeGroup)).thenReturn(deactivatedGroup);
        when(groupMapper.toDto(deactivatedGroup)).thenReturn(deactivatedGroupDto);

        // When
        StudentGroupDto result = studentGroupService.deactivateGroup(1L);

        // Then
        assertThat(result.getIsActive()).isFalse();
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
        verify(studentGroupRepository).save(activeGroup);
    }

    @Test
    @DisplayName("Should find group by id with student count")
    void shouldFindGroupByIdWithStudentCount() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentGroupRepository.findById(1L)).thenReturn(Optional.of(studentGroup));
        when(studentRepository.countByGroupId(1L)).thenReturn(25L);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        StudentGroupDto result = studentGroupService.findByIdWithStudentCount(1L);

        // Then
        assertThat(result.getCurrentStudentCount()).isEqualTo(25);
        verify(unitOfWork.getStudentGroupRepository()).findById(1L);
        verify(unitOfWork.getStudentRepository()).countByGroupId(1L);
        verify(groupMapper).toDto(studentGroup);
    }

    @Test
    @DisplayName("Should find groups with students")
    void shouldFindGroupsWithStudents() {
        // Given
        List<StudentGroup> groups = Arrays.asList(studentGroup);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentGroupRepository.findGroupsWithStudents()).thenReturn(groups);
        when(studentRepository.countByGroupId(1L)).thenReturn(25L);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        List<StudentGroupDto> result = studentGroupService.findGroupsWithStudents();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrentStudentCount()).isEqualTo(25);
        verify(unitOfWork.getStudentGroupRepository()).findGroupsWithStudents();
        verify(unitOfWork.getStudentRepository()).countByGroupId(1L);
    }

    @Test
    @DisplayName("Should find groups by teacher id")
    void shouldFindGroupsByTeacherId() {
        // Given
        List<StudentGroup> groups = Arrays.asList(studentGroup);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(studentGroupRepository.findGroupsByTeacherId(1L)).thenReturn(groups);
        when(studentRepository.countByGroupId(1L)).thenReturn(25L);
        when(groupMapper.toDto(studentGroup)).thenReturn(studentGroupDto);

        // When
        List<StudentGroupDto> result = studentGroupService.findGroupsByTeacherId(1L);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCurrentStudentCount()).isEqualTo(25);
        verify(unitOfWork.getStudentGroupRepository()).findGroupsByTeacherId(1L);
        verify(unitOfWork.getStudentRepository()).countByGroupId(1L);
    }

    @Test
    @DisplayName("Should return empty list when no groups found")
    void shouldReturnEmptyListWhenNoGroupsFound() {
        // Given
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(studentGroupRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<StudentGroupDto> result = studentGroupService.findAll();

        // Then
        assertThat(result).isEmpty();
        verify(unitOfWork.getStudentGroupRepository()).findAll();
        verify(groupMapper, never()).toDto(any());
    }
}
