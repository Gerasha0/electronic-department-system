package com.kursova.pl.controllers;

import com.kursova.bll.dto.GradeDto;
import com.kursova.bll.services.GradeService;
import com.kursova.dal.entities.GradeType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Grade management
 */
@RestController
@RequestMapping("/api/grades")
@Tag(name = "Grade Management", description = "Operations for managing student grades")
public class GradeController {
    
    private final GradeService gradeService;
    
    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }
    
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Create new grade", description = "Creates a new grade for a student")
    public ResponseEntity<GradeDto> createGrade(@Valid @RequestBody GradeDto gradeDto) {
        GradeDto createdGrade = gradeService.create(gradeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGrade);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get grade by ID", description = "Retrieves grade information by ID")
    public ResponseEntity<GradeDto> getGradeById(
            @PathVariable @Parameter(description = "Grade ID") Long id) {
        GradeDto grade = gradeService.findById(id);
        return ResponseEntity.ok(grade);
    }
    
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER') or #studentId == authentication.principal.studentId")
    @Operation(summary = "Get grades by student", description = "Retrieves all grades for a specific student")
    public ResponseEntity<List<GradeDto>> getGradesByStudent(
            @PathVariable @Parameter(description = "Student ID") Long studentId) {
        List<GradeDto> grades = gradeService.findByStudentId(studentId);
        return ResponseEntity.ok(grades);
    }
    
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER') or #teacherId == authentication.principal.teacherId")
    @Operation(summary = "Get grades by teacher", description = "Retrieves all grades assigned by a specific teacher")
    public ResponseEntity<List<GradeDto>> getGradesByTeacher(
            @PathVariable @Parameter(description = "Teacher ID") Long teacherId) {
        List<GradeDto> grades = gradeService.findByTeacherId(teacherId);
        return ResponseEntity.ok(grades);
    }
    
    @GetMapping("/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get grades by subject", description = "Retrieves all grades for a specific subject")
    public ResponseEntity<List<GradeDto>> getGradesBySubject(
            @PathVariable @Parameter(description = "Subject ID") Long subjectId) {
        List<GradeDto> grades = gradeService.findBySubjectId(subjectId);
        return ResponseEntity.ok(grades);
    }
    
    @GetMapping("/student/{studentId}/subject/{subjectId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER') or #studentId == authentication.principal.studentId")
    @Operation(summary = "Get grades by student and subject", description = "Retrieves grades for a specific student in a specific subject")
    public ResponseEntity<List<GradeDto>> getGradesByStudentAndSubject(
            @PathVariable @Parameter(description = "Student ID") Long studentId,
            @PathVariable @Parameter(description = "Subject ID") Long subjectId) {
        List<GradeDto> grades = gradeService.findByStudentAndSubject(studentId, subjectId);
        return ResponseEntity.ok(grades);
    }
    
    @GetMapping("/student/{studentId}/final")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER') or #studentId == authentication.principal.studentId")
    @Operation(summary = "Get final grades by student", description = "Retrieves final grades for a specific student")
    public ResponseEntity<List<GradeDto>> getFinalGradesByStudent(
            @PathVariable @Parameter(description = "Student ID") Long studentId) {
        List<GradeDto> grades = gradeService.findFinalGradesByStudent(studentId);
        return ResponseEntity.ok(grades);
    }
    
    @GetMapping("/type/{gradeType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get grades by type", description = "Retrieves grades by grade type")
    public ResponseEntity<List<GradeDto>> getGradesByType(
            @PathVariable @Parameter(description = "Grade type") GradeType gradeType) {
        List<GradeDto> grades = gradeService.findByGradeType(gradeType);
        return ResponseEntity.ok(grades);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Update grade", description = "Updates grade information")
    public ResponseEntity<GradeDto> updateGrade(
            @PathVariable @Parameter(description = "Grade ID") Long id,
            @Valid @RequestBody GradeDto gradeDto) {
        GradeDto updatedGrade = gradeService.update(id, gradeDto);
        return ResponseEntity.ok(updatedGrade);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Operation(summary = "Delete grade", description = "Deletes a grade")
    public ResponseEntity<Void> deleteGrade(
            @PathVariable @Parameter(description = "Grade ID") Long id) {
        gradeService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/student/{studentId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER') or #studentId == authentication.principal.studentId")
    @Operation(summary = "Get student average grade", description = "Calculates overall average grade for a student")
    public ResponseEntity<Double> getStudentAverageGrade(
            @PathVariable @Parameter(description = "Student ID") Long studentId) {
        Double averageGrade = gradeService.getOverallAverageGradeForStudent(studentId);
        return ResponseEntity.ok(averageGrade);
    }
    
    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER') or #studentId == authentication.principal.studentId")
    @Operation(summary = "Get student subject average", description = "Calculates average grade for a student in a specific subject")
    public ResponseEntity<Double> getStudentSubjectAverage(
            @PathVariable @Parameter(description = "Student ID") Long studentId,
            @PathVariable @Parameter(description = "Subject ID") Long subjectId) {
        Double averageGrade = gradeService.getAverageGradeForStudentInSubject(studentId, subjectId);
        return ResponseEntity.ok(averageGrade);
    }
}
