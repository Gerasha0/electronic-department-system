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
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.findActiveSubjects();
        return ResponseEntity.ok(subjects);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get subject by ID", description = "Retrieve a specific subject by its ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
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
}
