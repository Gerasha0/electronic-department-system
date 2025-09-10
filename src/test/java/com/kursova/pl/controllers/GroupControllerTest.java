package com.kursova.pl.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.dto.StudentGroupDto;
import com.kursova.bll.dto.UserDto;
import com.kursova.bll.services.StudentGroupService;
import com.kursova.bll.services.StudentService;
import com.kursova.dal.entities.EducationLevel;
import com.kursova.dal.entities.StudyForm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for GroupController
 * Tests REST endpoints for student group management
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@DisplayName("Group Controller Tests")
class GroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StudentGroupService groupService;

    @MockBean
    private StudentService studentService;

    private StudentGroupDto testGroup;
    private StudentDto testStudent;

    @BeforeEach
    void setUp() {
        // Setup test data
        testGroup = createTestGroup();
        testStudent = createTestStudent();
    }

    @Test
    @DisplayName("Should create group successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldCreateGroupSuccessfully() throws Exception {
        // Given
        when(groupService.create(any(StudentGroupDto.class))).thenReturn(testGroup);

        // When & Then
        mockMvc.perform(post("/api/groups")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGroup)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testGroup.getId()))
                .andExpect(jsonPath("$.groupName").value(testGroup.getGroupName()))
                .andExpect(jsonPath("$.groupCode").value(testGroup.getGroupCode()));

        verify(groupService).create(any(StudentGroupDto.class));
    }

    @Test
    @DisplayName("Should return 403 when creating group without proper role")
    @WithMockUser(roles = "STUDENT")
    void shouldReturn403WhenCreatingGroupWithoutProperRole() throws Exception {
        // Given
        StudentGroupDto groupDto = new StudentGroupDto();
        groupDto.setGroupName("Test Group");
        groupDto.setGroupCode("TG001");
        groupDto.setCourseYear(2024);

        // When & Then
        mockMvc.perform(post("/api/groups")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(groupDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Should get group by ID successfully")
    @WithMockUser(roles = "TEACHER")
    void shouldGetGroupByIdSuccessfully() throws Exception {
        // Given
        when(groupService.findById(1L)).thenReturn(testGroup);

        // When & Then
        mockMvc.perform(get("/api/groups/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testGroup.getId()))
                .andExpect(jsonPath("$.groupName").value(testGroup.getGroupName()));

        verify(groupService).findById(1L);
    }

    @Test
    @DisplayName("Should get all groups successfully")
    @WithMockUser(roles = "TEACHER")
    void shouldGetAllGroupsSuccessfully() throws Exception {
        // Given
        List<StudentGroupDto> groups = Arrays.asList(testGroup);
        when(groupService.findAll()).thenReturn(groups);

        // When & Then
        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testGroup.getId()))
                .andExpect(jsonPath("$[0].groupName").value(testGroup.getGroupName()));

        verify(groupService).findAll();
    }

    @Test
    @DisplayName("Should get active groups successfully")
    @WithMockUser(roles = "TEACHER")
    void shouldGetActiveGroupsSuccessfully() throws Exception {
        // Given
        List<StudentGroupDto> groups = Arrays.asList(testGroup);
        when(groupService.findActiveGroups()).thenReturn(groups);

        // When & Then
        mockMvc.perform(get("/api/groups/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testGroup.getId()));

        verify(groupService).findActiveGroups();
    }

    @Test
    @DisplayName("Should search groups by name successfully")
    @WithMockUser(roles = "TEACHER")
    void shouldSearchGroupsByNameSuccessfully() throws Exception {
        // Given
        List<StudentGroupDto> groups = Arrays.asList(testGroup);
        when(groupService.searchByNameOrCode("test")).thenReturn(groups);

        // When & Then
        mockMvc.perform(get("/api/groups/search")
                .param("name", "test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].groupName").value(testGroup.getGroupName()));

        verify(groupService).searchByNameOrCode("test");
    }

    @Test
    @DisplayName("Should get groups by teacher successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldGetGroupsByTeacherSuccessfully() throws Exception {
        // Given
        List<StudentGroupDto> groups = Arrays.asList(testGroup);
        when(groupService.findGroupsByTeacherId(1L)).thenReturn(groups);

        // When & Then
        mockMvc.perform(get("/api/groups/teacher/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testGroup.getId()));

        verify(groupService).findGroupsByTeacherId(1L);
    }

    @Test
    @DisplayName("Should get group students successfully")
    @WithMockUser(roles = "TEACHER")
    void shouldGetGroupStudentsSuccessfully() throws Exception {
        // Given
        List<StudentDto> students = Arrays.asList(testStudent);
        when(studentService.findByGroupId(1L)).thenReturn(students);

        // When & Then
        mockMvc.perform(get("/api/groups/1/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(testStudent.getId()))
                .andExpect(jsonPath("$[0].user.firstName").value(testStudent.getUser().getFirstName()));

        verify(studentService).findByGroupId(1L);
    }

    @Test
    @DisplayName("Should add student to group successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldAddStudentToGroupSuccessfully() throws Exception {
        // Given
        when(studentService.assignToGroup(1L, 1L)).thenReturn(testStudent);

        // When & Then
        mockMvc.perform(post("/api/groups/1/students/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testStudent.getId()));

        verify(studentService).assignToGroup(1L, 1L);
    }

    @Test
    @DisplayName("Should handle error when adding student to group fails")
    @WithMockUser(roles = "ADMIN")
    void shouldHandleErrorWhenAddingStudentToGroupFails() throws Exception {
        // Given
        when(studentService.assignToGroup(1L, 1L)).thenThrow(new RuntimeException("Assignment failed"));

        // When & Then
        mockMvc.perform(post("/api/groups/1/students/1")
                .with(csrf()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").value("Assignment failed"));
    }

    @Test
    @DisplayName("Should remove student from group successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldRemoveStudentFromGroupSuccessfully() throws Exception {
        // Given
        when(studentService.removeFromGroup(1L)).thenReturn(testStudent);

        // When & Then
        mockMvc.perform(delete("/api/groups/1/students/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(testStudent.getId()));

        verify(studentService).removeFromGroup(1L);
    }

    @Test
    @DisplayName("Should update group students successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateGroupStudentsSuccessfully() throws Exception {
        // Given
        List<Long> studentIds = Arrays.asList(1L, 2L);
        List<StudentDto> currentStudents = Arrays.asList(testStudent);
        List<StudentDto> updatedStudents = Arrays.asList(testStudent);

        when(studentService.findByGroupId(1L)).thenReturn(currentStudents).thenReturn(updatedStudents);
        when(studentService.assignToGroup(any(Long.class), eq(1L))).thenReturn(testStudent);

        // When & Then
        mockMvc.perform(put("/api/groups/1/students")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(testStudent.getId()));

        verify(studentService, times(2)).findByGroupId(1L);
        verify(studentService, times(2)).assignToGroup(any(Long.class), eq(1L));
    }

    @Test
    @DisplayName("Should update group successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateGroupSuccessfully() throws Exception {
        // Given
        when(groupService.update(1L, testGroup)).thenReturn(testGroup);

        // When & Then
        mockMvc.perform(put("/api/groups/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGroup)))
                .andExpect(status().isOk());

        verify(groupService).update(eq(1L), any(StudentGroupDto.class));
    }

    @Test
    @DisplayName("Should activate group successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldActivateGroupSuccessfully() throws Exception {
        // Given
        when(groupService.activateGroup(1L)).thenReturn(testGroup);

        // When & Then
        mockMvc.perform(put("/api/groups/1/activate")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testGroup.getId()));

        verify(groupService).activateGroup(1L);
    }

    @Test
    @DisplayName("Should deactivate group successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldDeactivateGroupSuccessfully() throws Exception {
        // Given
        when(groupService.deactivateGroup(1L)).thenReturn(testGroup);

        // When & Then
        mockMvc.perform(put("/api/groups/1/deactivate")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testGroup.getId()));

        verify(groupService).deactivateGroup(1L);
    }

    @Test
    @DisplayName("Should delete group successfully")
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteGroupSuccessfully() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/groups/1")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(groupService).delete(1L);
    }

    private StudentGroupDto createTestGroup() {
        StudentGroupDto group = new StudentGroupDto();
        group.setId(1L);
        group.setGroupName("Test Group");
        group.setGroupCode("TG2023");
        group.setCourseYear(1);
        group.setEducationLevel(EducationLevel.BACHELOR);
        group.setStudyForm(StudyForm.FULL_TIME);
        group.setMaxStudents(30);
        group.setSpecialization("Computer Science");
        group.setStartYear(2023);
        group.setEnrollmentYear(2023);
        group.setIsActive(true);
        return group;
    }

    private StudentDto createTestStudent() {
        StudentDto student = new StudentDto();
        student.setId(1L);
        student.setStudentNumber("ST12345");

        UserDto user = new UserDto();
        user.setId(1L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@test.com");
        student.setUser(user);

        return student;
    }
}
