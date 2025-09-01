package com.kursova.pl.controllers;

import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.services.TeacherService;
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
 * REST Controller for Teacher management
 */
@RestController
@RequestMapping("/api/teachers")
@Tag(name = "Teacher Management", description = "Operations for managing teachers")
public class TeacherController {

    private final TeacherService teacherService;

    @Autowired
    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Create new teacher", description = "Creates a new teacher")
    public ResponseEntity<TeacherDto> createTeacher(@Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto createdTeacher = teacherService.create(teacherDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTeacher);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER') or #id == authentication.principal.teacherId")
    @Operation(summary = "Get teacher by ID", description = "Retrieves teacher information by ID")
    public ResponseEntity<TeacherDto> getTeacherById(
            @PathVariable @Parameter(description = "Teacher ID") Long id) {
        TeacherDto teacher = teacherService.findById(id);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get all teachers", description = "Retrieves all teachers")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        List<TeacherDto> teachers = teacherService.findAll();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/public")
    @Operation(summary = "Get teachers public info", description = "Get basic teacher info for public view")
    public ResponseEntity<List<TeacherDto>> getPublicTeachers() {
        List<TeacherDto> teachers = teacherService.findActiveTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get active teachers", description = "Retrieves all active teachers")
    public ResponseEntity<List<TeacherDto>> getActiveTeachers() {
        List<TeacherDto> teachers = teacherService.findActiveTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get teachers by subject", description = "Retrieves teachers assigned to a specific subject")
    public ResponseEntity<List<TeacherDto>> getTeachersBySubject(
            @PathVariable @Parameter(description = "Subject ID") Long subjectId) {
        List<TeacherDto> teachers = teacherService.findBySubjectId(subjectId);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Search teachers by name", description = "Searches teachers by first or last name")
    public ResponseEntity<List<TeacherDto>> searchTeachersByName(
            @RequestParam @Parameter(description = "Search term") String name) {
        List<TeacherDto> teachers = teacherService.searchByName(name);
        return ResponseEntity.ok(teachers);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Update teacher", description = "Updates teacher information")
    public ResponseEntity<TeacherDto> updateTeacher(
            @PathVariable @Parameter(description = "Teacher ID") Long id,
            @Valid @RequestBody TeacherDto teacherDto) {
        TeacherDto updatedTeacher = teacherService.update(id, teacherDto);
        return ResponseEntity.ok(updatedTeacher);
    }

    @PutMapping("/{id}/activate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Activate teacher", description = "Activates teacher account")
    public ResponseEntity<TeacherDto> activateTeacher(
            @PathVariable @Parameter(description = "Teacher ID") Long id) {
        TeacherDto activatedTeacher = teacherService.activateTeacher(id);
        return ResponseEntity.ok(activatedTeacher);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Deactivate teacher", description = "Deactivates teacher account")
    public ResponseEntity<TeacherDto> deactivateTeacher(
            @PathVariable @Parameter(description = "Teacher ID") Long id) {
        TeacherDto deactivatedTeacher = teacherService.deactivateTeacher(id);
        return ResponseEntity.ok(deactivatedTeacher);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete teacher", description = "Deletes teacher account")
    public ResponseEntity<Void> deleteTeacher(
            @PathVariable @Parameter(description = "Teacher ID") Long id) {
        teacherService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
