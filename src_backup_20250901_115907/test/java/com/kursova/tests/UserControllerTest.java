package com.kursova.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kursova.bll.dto.UserDto;
import com.kursova.bll.services.UserService;
import com.kursova.dal.entities.UserRole;
import com.kursova.pl.controllers.UserController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("User Controller Tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setFirstName("Test");
        testUserDto.setLastName("User");
        testUserDto.setRole(UserRole.STUDENT);
        testUserDto.setIsActive(true);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create user successfully")
    void createUser_ShouldReturnCreatedUser_WhenValidRequest() throws Exception {
        // Arrange
        when(userService.create(any(UserDto.class))).thenReturn(testUserDto);

        // Act & Assert
        mockMvc.perform(post("/api/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should create user with password successfully")
    void createUserWithPassword_ShouldReturnCreatedUser_WhenValidRequest() throws Exception {
        // Arrange
        when(userService.createWithPassword(any(UserDto.class), eq("password123"))).thenReturn(testUserDto);

        // Act & Assert
        mockMvc.perform(post("/api/users/with-password")
                .with(csrf())
                .param("password", "password123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "STUDENT")
    @DisplayName("Should get user by ID successfully")
    void getUserById_ShouldReturnUser_WhenUserExists() throws Exception {
        // Arrange
        when(userService.findById(1L)).thenReturn(testUserDto);

        // Act & Assert
        mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get all users successfully")
    void getAllUsers_ShouldReturnUsersList_WhenUsersExist() throws Exception {
        // Arrange
        List<UserDto> users = Arrays.asList(testUserDto);
        when(userService.findAll()).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get user by username successfully")
    void getUserByUsername_ShouldReturnUser_WhenUserExists() throws Exception {
        // Arrange
        when(userService.findByUsername("testuser")).thenReturn(testUserDto);

        // Act & Assert
        mockMvc.perform(get("/api/users/by-username/testuser")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Should get users by role successfully")
    void getUsersByRole_ShouldReturnUsersList_WhenUsersExist() throws Exception {
        // Arrange
        List<UserDto> students = Arrays.asList(testUserDto);
        when(userService.findByRole(UserRole.STUDENT)).thenReturn(students);

        // Act & Assert
        mockMvc.perform(get("/api/users/by-role/STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].role").value("STUDENT"));
    }

    @Test
    @WithMockUser(roles = "MANAGER")
    @DisplayName("Should get active users by role successfully")
    void getActiveUsersByRole_ShouldReturnActiveUsersList_WhenUsersExist() throws Exception {
        // Arrange
        List<UserDto> activeStudents = Arrays.asList(testUserDto);
        when(userService.findActiveByRole(UserRole.STUDENT)).thenReturn(activeStudents);

        // Act & Assert
        mockMvc.perform(get("/api/users/active/by-role/STUDENT")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].isActive").value(true));
    }
}