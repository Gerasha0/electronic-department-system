package com.kursova.pl.controllers;

import com.kursova.bll.dto.UserDto;
import com.kursova.bll.services.UserService;
import com.kursova.config.jwt.JwtUtils;
import com.kursova.dal.entities.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthController
 * Tests all authentication endpoints
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AuthController authController;

    private UserDto sampleUserDto;
    private AuthController.LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Setup sample user
        sampleUserDto = new UserDto();
        sampleUserDto.setId(1L);
        sampleUserDto.setUsername("testuser");
        sampleUserDto.setEmail("test@example.com");
        sampleUserDto.setFirstName("Test");
        sampleUserDto.setLastName("User");
        sampleUserDto.setRole(UserRole.STUDENT);
        sampleUserDto.setIsActive(true);

        // Setup login request
        loginRequest = new AuthController.LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password");

        // Mock SecurityContextHolder
        SecurityContextHolder.setContext(securityContext);
    }

    // ===============================
    // LOGIN TESTS
    // ===============================

    @Test
    void login_Success() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.findByUsername("testuser")).thenReturn(sampleUserDto);
        when(jwtUtils.generateToken("testuser", "STUDENT")).thenReturn("jwt-token");

        // Act
        ResponseEntity<AuthController.LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        AuthController.LoginResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("jwt-token", body.getMessage());
        assertEquals(sampleUserDto, body.getUser());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService).findByUsername("testuser");
        verify(jwtUtils).generateToken("testuser", "STUDENT");
    }

    @Test
    void login_UserNotFound() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.findByUsername("testuser")).thenReturn(null);

        // Act
        ResponseEntity<AuthController.LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        AuthController.LoginResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Invalid credentials or user is not active", body.getMessage());
        assertNull(body.getUser());
    }

    @Test
    void login_UserNotActive() {
        // Arrange
        sampleUserDto.setIsActive(false);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userService.findByUsername("testuser")).thenReturn(sampleUserDto);

        // Act
        ResponseEntity<AuthController.LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        AuthController.LoginResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Invalid credentials or user is not active", body.getMessage());
        assertNull(body.getUser());
    }

    @Test
    void login_InvalidCredentials() {
        // Arrange
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Act
        ResponseEntity<AuthController.LoginResponse> response = authController.login(loginRequest);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        AuthController.LoginResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Invalid credentials", body.getMessage());
        assertNull(body.getUser());
    }

    // ===============================
    // REGISTER TESTS
    // ===============================

    @Test
    void register_Success() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(false);
        when(userService.createWithPassword(any(UserDto.class), anyString())).thenReturn(sampleUserDto);

        // Act
        ResponseEntity<?> response = authController.register(sampleUserDto, "password");

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(sampleUserDto, response.getBody());
        verify(userService).existsByUsername("testuser");
        verify(userService).createWithPassword(sampleUserDto, "password");
    }

    @Test
    void register_UsernameExists() {
        // Arrange
        when(userService.existsByUsername("testuser")).thenReturn(true);

        // Act
        ResponseEntity<?> response = authController.register(sampleUserDto, "password");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username exists", response.getBody());
        verify(userService).existsByUsername("testuser");
        verify(userService, never()).createWithPassword(any(UserDto.class), anyString());
    }

    // ===============================
    // LOGOUT TESTS
    // ===============================

    @Test
    void logout_Success() {
        // Act
        try (MockedStatic<SecurityContextHolder> mockedStatic = mockStatic(SecurityContextHolder.class)) {
            ResponseEntity<Map<String, String>> response = authController.logout();

            // Assert
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            Map<String, String> body = response.getBody();
            assertNotNull(body);
            assertEquals("Logout successful", body.get("message"));
            mockedStatic.verify(SecurityContextHolder::clearContext);
        }
    }

    // ===============================
    // GUEST LOGIN TESTS
    // ===============================

    @Test
    void guestLogin_Success() {
        // Arrange
        when(jwtUtils.generateToken("guest", "GUEST")).thenReturn("guest-jwt-token");

        // Act
        ResponseEntity<AuthController.LoginResponse> response = authController.guestLogin();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        AuthController.LoginResponse body = response.getBody();
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("guest-jwt-token", body.getMessage());
        assertNotNull(body.getUser());
        UserDto user = body.getUser();
        assertNotNull(user);
        assertEquals("guest", user.getUsername());
        assertEquals("Гість", user.getFirstName());
        assertEquals("Система", user.getLastName());
        assertEquals(UserRole.GUEST, user.getRole());
        assertTrue(user.getIsActive());
        verify(jwtUtils).generateToken("guest", "GUEST");
    }

    @Test
    void guestLogin_Exception() {
        // Arrange
        when(jwtUtils.generateToken("guest", "GUEST")).thenThrow(new RuntimeException("Token generation failed"));

        // Act
        ResponseEntity<AuthController.LoginResponse> response = authController.guestLogin();

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        AuthController.LoginResponse body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isSuccess());
        assertEquals("Error creating guest session", body.getMessage());
        assertNull(body.getUser());
    }

    // ===============================
    // GET CURRENT USER TESTS
    // ===============================

    @Test
    void getCurrentUser_AuthenticatedUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(sampleUserDto);

        // Act
        ResponseEntity<UserDto> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleUserDto, response.getBody());
        verify(securityContext).getAuthentication();
        verify(userService).findByUsername("testuser");
    }

    @Test
    void getCurrentUser_GuestUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("guest");

        // Act
        ResponseEntity<UserDto> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        UserDto user = response.getBody();
        assertNotNull(user);
        assertEquals("guest", user.getUsername());
        assertEquals("Гість", user.getFirstName());
        assertEquals("Система", user.getLastName());
        assertEquals(UserRole.GUEST, user.getRole());
        assertTrue(user.getIsActive());
        verify(securityContext).getAuthentication();
        verify(userService, never()).findByUsername(anyString());
    }

    @Test
    void getCurrentUser_Unauthenticated() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);

        // Act
        ResponseEntity<UserDto> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(securityContext).getAuthentication();
    }

    @Test
    void getCurrentUser_AnonymousUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("anonymousUser");

        // Act
        ResponseEntity<UserDto> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(securityContext).getAuthentication();
    }

    @Test
    void getCurrentUser_Exception() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenThrow(new RuntimeException("Database error"));

        // Act
        ResponseEntity<UserDto> response = authController.getCurrentUser();

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNull(response.getBody());
        verify(securityContext).getAuthentication();
        verify(userService).findByUsername("testuser");
    }
}
