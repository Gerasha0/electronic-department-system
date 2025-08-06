package com.kursova.pl.controllers;

import com.kursova.bll.dto.UserDto;
import com.kursova.bll.services.UserService;
import com.kursova.dal.entities.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User management
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Operations for managing users")
public class UserController {
    
    private final UserService userService;
    
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new user", description = "Creates a new user with the provided information")
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        UserDto createdUser = userService.create(userDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @PostMapping("/with-password")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new user with password", description = "Creates a new user with password")
    public ResponseEntity<UserDto> createUserWithPassword(
            @Valid @RequestBody UserDto userDto,
            @RequestParam @Parameter(description = "Password for the new user") String password) {
        UserDto createdUser = userService.createWithPassword(userDto, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get user by ID", description = "Retrieves user information by ID")
    public ResponseEntity<UserDto> getUserById(
            @PathVariable @Parameter(description = "User ID") Long id) {
        UserDto user = userService.findById(id);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get all users", description = "Retrieves all users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/by-username/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get user by username", description = "Retrieves user by username")
    public ResponseEntity<UserDto> getUserByUsername(
            @PathVariable @Parameter(description = "Username") String username) {
        UserDto user = userService.findByUsername(username);
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/by-role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get users by role", description = "Retrieves users by role")
    public ResponseEntity<List<UserDto>> getUsersByRole(
            @PathVariable @Parameter(description = "User role") UserRole role) {
        List<UserDto> users = userService.findByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/active/by-role/{role}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get active users by role", description = "Retrieves active users by role")
    public ResponseEntity<List<UserDto>> getActiveUsersByRole(
            @PathVariable @Parameter(description = "User role") UserRole role) {
        List<UserDto> users = userService.findActiveByRole(role);
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get active users", description = "Retrieves all active users")
    public ResponseEntity<List<UserDto>> getActiveUsers() {
        List<UserDto> users = userService.findActiveUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Search users by name", description = "Searches users by name")
    public ResponseEntity<List<UserDto>> searchUsersByName(
            @RequestParam @Parameter(description = "Search term") String name) {
        List<UserDto> users = userService.searchByName(name);
        return ResponseEntity.ok(users);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update user", description = "Updates user information")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable @Parameter(description = "User ID") Long id,
            @Valid @RequestBody UserDto userDto) {
        UserDto updatedUser = userService.update(id, userDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @PutMapping("/{id}/password")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #id == authentication.principal.id")
    @Operation(summary = "Update user password", description = "Updates user password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable @Parameter(description = "User ID") Long id,
            @RequestParam @Parameter(description = "Current password") String oldPassword,
            @RequestParam @Parameter(description = "New password") String newPassword) {
        userService.updatePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok().build();
    }
    
    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Activate user", description = "Activates user account")
    public ResponseEntity<UserDto> activateUser(
            @PathVariable @Parameter(description = "User ID") Long id) {
        UserDto activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(activatedUser);
    }
    
    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Deactivate user", description = "Deactivates user account")
    public ResponseEntity<UserDto> deactivateUser(
            @PathVariable @Parameter(description = "User ID") Long id) {
        UserDto deactivatedUser = userService.deactivateUser(id);
        return ResponseEntity.ok(deactivatedUser);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Deletes user account")
    public ResponseEntity<Void> deleteUser(
            @PathVariable @Parameter(description = "User ID") Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Check if user exists", description = "Checks if user exists by ID")
    public ResponseEntity<Boolean> userExists(
            @PathVariable @Parameter(description = "User ID") Long id) {
        boolean exists = userService.existsById(id);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/username/{username}/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Check if username exists", description = "Checks if username exists")
    public ResponseEntity<Boolean> usernameExists(
            @PathVariable @Parameter(description = "Username") String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }
    
    @GetMapping("/email/{email}/exists")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Check if email exists", description = "Checks if email exists")
    public ResponseEntity<Boolean> emailExists(
            @PathVariable @Parameter(description = "Email") String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}
