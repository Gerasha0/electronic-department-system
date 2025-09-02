package com.kursova.pl.controllers;

import com.kursova.bll.dto.StudentGroupDto;
import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.services.StudentGroupService;
import com.kursova.bll.services.StudentService;
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
import java.util.Map;

/**
 * REST Controller for Student Group management
 */
@RestController
@RequestMapping("/api/groups")
@Tag(name = "Group Management", description = "Operations for managing student groups")
public class GroupController {

    private final StudentGroupService groupService;
    private final StudentService studentService;

    @Autowired
    public GroupController(StudentGroupService groupService, StudentService studentService) {
        this.groupService = groupService;
        this.studentService = studentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new group", description = "Creates a new student group")
    public ResponseEntity<StudentGroupDto> createGroup(@Valid @RequestBody StudentGroupDto groupDto) {
        StudentGroupDto createdGroup = groupService.create(groupDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get group by ID", description = "Retrieves group information by ID")
    public ResponseEntity<StudentGroupDto> getGroupById(
            @PathVariable @Parameter(description = "Group ID") Long id) {
        StudentGroupDto group = groupService.findById(id);
        return ResponseEntity.ok(group);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get all groups", description = "Retrieves all student groups")
    public ResponseEntity<List<StudentGroupDto>> getAllGroups() {
        List<StudentGroupDto> groups = groupService.findAll();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get active groups", description = "Retrieves all active student groups")
    public ResponseEntity<List<StudentGroupDto>> getActiveGroups() {
        List<StudentGroupDto> groups = groupService.findActiveGroups();
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Search groups by name", description = "Searches groups by name")
    public ResponseEntity<List<StudentGroupDto>> searchGroupsByName(
            @RequestParam @Parameter(description = "Search term") String name) {
        List<StudentGroupDto> groups = groupService.searchByName(name);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{id}/students")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get students in group", description = "Retrieves all students in a specific group")
    public ResponseEntity<List<StudentDto>> getGroupStudents(
            @PathVariable @Parameter(description = "Group ID") Long id) {
        List<StudentDto> students = studentService.findByGroupId(id);
        return ResponseEntity.ok(students);
    }

    @PostMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Add student to group", description = "Adds a student to a specific group")
    public ResponseEntity<?> addStudentToGroup(
            @PathVariable @Parameter(description = "Group ID") Long groupId,
            @PathVariable @Parameter(description = "Student ID") Long studentId) {
        try {
            StudentDto student = studentService.assignToGroup(studentId, groupId);
            return ResponseEntity.ok(Map.of("success", true, "data", student));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @DeleteMapping("/{groupId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Remove student from group", description = "Removes a student from a specific group")
    public ResponseEntity<?> removeStudentFromGroup(
            @PathVariable @Parameter(description = "Group ID") Long groupId,
            @PathVariable @Parameter(description = "Student ID") Long studentId) {
        try {
            StudentDto student = studentService.removeFromGroup(studentId);
            return ResponseEntity.ok(Map.of("success", true, "data", student));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update group", description = "Updates group information")
    public ResponseEntity<StudentGroupDto> updateGroup(
            @PathVariable @Parameter(description = "Group ID") Long id,
            @Valid @RequestBody StudentGroupDto groupDto) {
        StudentGroupDto updatedGroup = groupService.update(id, groupDto);
        return ResponseEntity.ok(updatedGroup);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Activate group", description = "Activates student group")
    public ResponseEntity<StudentGroupDto> activateGroup(
            @PathVariable @Parameter(description = "Group ID") Long id) {
        StudentGroupDto activatedGroup = groupService.activateGroup(id);
        return ResponseEntity.ok(activatedGroup);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Deactivate group", description = "Deactivates student group")
    public ResponseEntity<StudentGroupDto> deactivateGroup(
            @PathVariable @Parameter(description = "Group ID") Long id) {
        StudentGroupDto deactivatedGroup = groupService.deactivateGroup(id);
        return ResponseEntity.ok(deactivatedGroup);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete group", description = "Deletes student group")
    public ResponseEntity<Void> deleteGroup(
            @PathVariable @Parameter(description = "Group ID") Long id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
