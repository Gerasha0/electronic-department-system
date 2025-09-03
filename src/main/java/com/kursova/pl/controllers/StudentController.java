package com.kursova.pl.controllers;

import com.kursova.bll.dto.StudentDto;
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

/**
 * REST Controller for Student management
 */
@RestController
@RequestMapping("/api/students")
@Tag(name = "Student Management", description = "Operations for managing students")
public class StudentController {

    private final StudentService studentService;

    @Autowired
    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new student", description = "Creates a new student")
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        StudentDto createdStudent = studentService.create(studentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStudent);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER') or #id == authentication.principal.studentId")
    @Operation(summary = "Get student by ID", description = "Retrieves student information by ID")
    public ResponseEntity<StudentDto> getStudentById(
            @PathVariable @Parameter(description = "Student ID") Long id) {
        StudentDto student = studentService.findById(id);
        return ResponseEntity.ok(student);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get all students", description = "Retrieves all students")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.findAll();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get active students", description = "Retrieves all active students")
    public ResponseEntity<List<StudentDto>> getActiveStudents() {
        List<StudentDto> students = studentService.findActiveStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/without-group")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Get students without group", description = "Retrieves all students that are not assigned to any group")
    public ResponseEntity<List<StudentDto>> getStudentsWithoutGroup() {
        List<StudentDto> students = studentService.findStudentsWithoutGroup();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/group/{groupId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get students by group", description = "Retrieves students by group ID")
    public ResponseEntity<List<StudentDto>> getStudentsByGroup(
            @PathVariable @Parameter(description = "Group ID") Long groupId) {
        List<StudentDto> students = studentService.findByGroupId(groupId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Search students by name", description = "Searches students by first or last name")
    public ResponseEntity<List<StudentDto>> searchStudentsByName(
            @RequestParam @Parameter(description = "Search term") String name) {
        List<StudentDto> students = studentService.searchByName(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search-without-group")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search students without group by name", description = "Searches students without group by first or last name")
    public ResponseEntity<List<StudentDto>> searchStudentsWithoutGroupByName(
            @RequestParam @Parameter(description = "Search term") String name) {
        List<StudentDto> allStudentsWithoutGroup = studentService.findStudentsWithoutGroup();
        
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.ok(allStudentsWithoutGroup);
        }
        
        String searchTerm = name.trim().toLowerCase();
        List<StudentDto> filteredStudents = allStudentsWithoutGroup.stream()
                .filter(student -> {
                    String fullName = student.getFullName().toLowerCase();
                    String email = student.getUser() != null ? student.getUser().getEmail().toLowerCase() : "";
                    return fullName.contains(searchTerm) || email.contains(searchTerm);
                })
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(filteredStudents);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update student", description = "Updates student information")
    public ResponseEntity<StudentDto> updateStudent(
            @PathVariable @Parameter(description = "Student ID") Long id,
            @Valid @RequestBody StudentDto studentDto) {
        StudentDto updatedStudent = studentService.update(id, studentDto);
        return ResponseEntity.ok(updatedStudent);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Activate student", description = "Activates student account")
    public ResponseEntity<StudentDto> activateStudent(
            @PathVariable @Parameter(description = "Student ID") Long id) {
        StudentDto activatedStudent = studentService.activateStudent(id);
        return ResponseEntity.ok(activatedStudent);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Deactivate student", description = "Deactivates student account")
    public ResponseEntity<StudentDto> deactivateStudent(
            @PathVariable @Parameter(description = "Student ID") Long id) {
        StudentDto deactivatedStudent = studentService.deactivateStudent(id);
        return ResponseEntity.ok(deactivatedStudent);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete student", description = "Deletes student account")
    public ResponseEntity<Void> deleteStudent(
            @PathVariable @Parameter(description = "Student ID") Long id) {
        studentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search-for-group")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Search students for group assignment", description = "Search students with group information for group assignment")
    public ResponseEntity<List<Object>> searchStudentsForGroup(
            @RequestParam @Parameter(description = "Search query") String query,
            @RequestParam @Parameter(description = "Target group ID") Long groupId) {
        List<Object> students = studentService.searchStudentsForGroup(query, groupId);
        return ResponseEntity.ok(students);
    }
}
