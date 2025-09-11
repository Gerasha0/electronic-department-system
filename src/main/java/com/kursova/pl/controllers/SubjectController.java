package com.kursova.pl.controllers;

import com.kursova.bll.dto.SubjectDto;
import com.kursova.bll.services.SubjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for managing subjects
 */
@RestController
@RequestMapping("/api/subjects")
@Tag(name = "Subject Management", description = "Operations related to subject management")
public class SubjectController {

    private final SubjectService subjectService;

    @Autowired
    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    @Operation(summary = "Get all subjects", description = "Retrieve all active subjects")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.findActiveSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID", description = "Retrieve a specific subject by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<SubjectDto> getSubjectById(@PathVariable Long id) {
        SubjectDto subject = subjectService.findById(id);
        return ResponseEntity.ok(subject);
    }

    @PostMapping
    @Operation(summary = "Create new subject", description = "Create a new subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SubjectDto> createSubject(@Valid @RequestBody SubjectDto subjectDto) {
        SubjectDto createdSubject = subjectService.create(subjectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSubject);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update subject", description = "Update an existing subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<SubjectDto> updateSubject(@PathVariable Long id, @Valid @RequestBody SubjectDto subjectDto) {
        SubjectDto updatedSubject = subjectService.update(id, subjectDto);
        return ResponseEntity.ok(updatedSubject);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete subject", description = "Soft delete a subject by setting isActive to false")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteSubject(@PathVariable Long id) {
        subjectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{subjectId}/teachers/{teacherId}")
    @Operation(summary = "Assign teacher to subject", description = "Assign a teacher to a subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> assignTeacherToSubject(@PathVariable Long subjectId, @PathVariable Long teacherId) {
        subjectService.assignTeacher(subjectId, teacherId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{subjectId}/teachers/{teacherId}")
    @Operation(summary = "Remove teacher from subject", description = "Remove a teacher from a subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> removeTeacherFromSubject(@PathVariable Long subjectId, @PathVariable Long teacherId) {
        subjectService.removeTeacher(subjectId, teacherId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/teacher/{teacherId}")
    @Operation(summary = "Get subjects by teacher", description = "Retrieve subjects assigned to a specific teacher")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #teacherId == authentication.principal.teacherId")
    public ResponseEntity<List<SubjectDto>> getSubjectsByTeacher(@PathVariable Long teacherId) {
        List<SubjectDto> subjects = subjectService.findByTeacherId(teacherId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/group/{groupId}")
    @Operation(summary = "Get subjects by group", description = "Retrieve subjects studied by a specific group")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<SubjectDto>> getSubjectsByGroup(@PathVariable Long groupId) {
        List<SubjectDto> subjects = subjectService.findByGroupId(groupId);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/public")
    @Operation(summary = "Get public subjects info", description = "Get basic info about subjects for public view")
    public ResponseEntity<List<SubjectDto>> getPublicSubjects() {
        List<SubjectDto> subjects = subjectService.findActiveSubjects();
        return ResponseEntity.ok(subjects);
    }

    // Subject Groups Management
    @GetMapping("/{subjectId}/groups")
    @Operation(summary = "Get groups assigned to subject", description = "Retrieve all groups assigned to a specific subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    public ResponseEntity<List<Object>> getSubjectGroups(@PathVariable Long subjectId) {
        List<Object> groups = subjectService.getAssignedGroups(subjectId);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{subjectId}/available-groups")
    @Operation(summary = "Get available groups for subject", description = "Retrieve groups that can be assigned to a subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Object>> getAvailableGroupsForSubject(@PathVariable Long subjectId) {
        List<Object> groups = subjectService.getAvailableGroups(subjectId);
        return ResponseEntity.ok(groups);
    }

    @PostMapping("/{subjectId}/groups/{groupId}")
    @Operation(summary = "Add group to subject", description = "Assign a group to study a subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> addGroupToSubject(@PathVariable Long subjectId, @PathVariable Long groupId) {
        try {
            subjectService.addGroupToSubject(subjectId, groupId);
            return ResponseEntity.ok("Group successfully added to subject");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error adding group to subject: " + e.getMessage());
        }
    }

    @DeleteMapping("/{subjectId}/groups/{groupId}")
    @Operation(summary = "Remove group from subject", description = "Remove a group from studying a subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> removeGroupFromSubject(@PathVariable Long subjectId, @PathVariable Long groupId) {
        try {
            subjectService.removeGroupFromSubject(subjectId, groupId);
            return ResponseEntity.ok("Group successfully removed from subject");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing group from subject: " + e.getMessage());
        }
    }

    // Subject Teachers Management (Admin/Manager only)
    @GetMapping("/{subjectId}/teachers")
    @Operation(summary = "Get teachers assigned to subject", description = "Retrieve all teachers assigned to a specific subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Object>> getSubjectTeachers(@PathVariable Long subjectId) {
        List<Object> teachers = subjectService.getAssignedTeachers(subjectId);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/{subjectId}/available-teachers")
    @Operation(summary = "Get available teachers for subject", description = "Retrieve teachers that can be assigned to a subject")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<Object>> getAvailableTeachersForSubject(@PathVariable Long subjectId) {
        List<Object> teachers = subjectService.getAvailableTeachers(subjectId);
        return ResponseEntity.ok(teachers);
    }
}
