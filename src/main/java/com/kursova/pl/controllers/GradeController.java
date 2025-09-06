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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.kursova.dal.uow.UnitOfWork;
import com.kursova.dal.entities.Grade;

/**
 * REST Controller for Grade management
 */
@RestController
@RequestMapping("/api/grades")
@Tag(name = "Grade Management", description = "Operations for managing student grades")
public class GradeController {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GradeController.class);
    
    private final GradeService gradeService;
    private final UnitOfWork unitOfWork;

    public GradeController(GradeService gradeService, UnitOfWork unitOfWork) {
        this.gradeService = gradeService;
        this.unitOfWork = unitOfWork;
    }

    /**
     * Helper method to check if current user is a student and can access the given studentId
     */
    private boolean canStudentAccessStudentId(Long studentId, Authentication authentication) {
        if (!authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_STUDENT"))) {
            return true; // Not a student, let @PreAuthorize handle it
        }
        
        var currentUser = unitOfWork.getUserRepository().findByUsername(authentication.getName());
        if (currentUser.isEmpty()) {
            logger.debug("User not found for username: {}", authentication.getName());
            return false;
        }
        
        var studentOpt = unitOfWork.getStudentRepository().findByUserId(currentUser.get().getId());
        if (studentOpt.isEmpty()) {
            logger.debug("Student not found for userId: {}", currentUser.get().getId());
            return false;
        }
        
        Long currentStudentId = studentOpt.get().getId();
        logger.debug("Current student ID: {}, Requested student ID: {}", currentStudentId, studentId);
        return currentStudentId.equals(studentId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Create new grade", description = "Creates a new grade for a student")
    public ResponseEntity<GradeDto> createGrade(@Valid @RequestBody GradeDto gradeDto) {
        GradeDto createdGrade = gradeService.create(gradeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGrade);
    }

    @PostMapping("/by-ids")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Create grade by ids", description = "Creates a new grade given studentId, teacherId, subjectId and value")
    public ResponseEntity<?> createGradeByIds(@RequestBody Map<String, Object> payload) {
        Long studentId = payload.get("studentId") == null ? null : Long.valueOf(payload.get("studentId").toString());
        Long teacherId = payload.get("teacherId") == null ? null : Long.valueOf(payload.get("teacherId").toString());
        Long subjectId = payload.get("subjectId") == null ? null : Long.valueOf(payload.get("subjectId").toString());
        Integer gradeValue = payload.get("gradeValue") == null ? null : Integer.valueOf(payload.get("gradeValue").toString());
        String gradeTypeStr = payload.get("gradeType") == null ? null : payload.get("gradeType").toString();
        String comments = payload.get("comments") == null ? null : payload.get("comments").toString();
        com.kursova.dal.entities.GradeType gradeType = gradeTypeStr == null ? com.kursova.dal.entities.GradeType.CURRENT : com.kursova.dal.entities.GradeType.valueOf(gradeTypeStr);

        try {
            GradeDto result = gradeService.createGradeWithValidation(studentId, teacherId, subjectId, gradeValue, gradeType, comments);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception ex) {
        // log internal error and return a minimal, non-sensitive response
        logger.error("Error creating grade by ids", ex);
        java.util.Map<String, Object> err = java.util.Map.of(
            "error", "InternalServerError",
            "message", "Failed to create grade"
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
    }
    }

    @PostMapping("/by-user-ids")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Create grade by user ids", description = "Creates a new grade given userId (not studentId), teacherId, subjectId and value")
    public ResponseEntity<?> createGradeByUserIds(@RequestBody Map<String, Object> payload) {
        Long userId = payload.get("studentId") == null ? null : Long.valueOf(payload.get("studentId").toString()); // Frontend sends userId as "studentId"
        Long teacherId = payload.get("teacherId") == null ? null : Long.valueOf(payload.get("teacherId").toString());
        Long subjectId = payload.get("subjectId") == null ? null : Long.valueOf(payload.get("subjectId").toString());
        Integer gradeValue = payload.get("gradeValue") == null ? null : Integer.valueOf(payload.get("gradeValue").toString());
        String gradeTypeStr = payload.get("gradeType") == null ? null : payload.get("gradeType").toString();
        String comments = payload.get("comments") == null ? null : payload.get("comments").toString();
        com.kursova.dal.entities.GradeType gradeType = gradeTypeStr == null ? com.kursova.dal.entities.GradeType.CURRENT : com.kursova.dal.entities.GradeType.valueOf(gradeTypeStr);

        try {
            // Find student by user ID
            var studentOpt = unitOfWork.getStudentRepository().findByUserId(userId);
            if (studentOpt.isEmpty()) {
                java.util.Map<String, Object> err = java.util.Map.of(
                    "error", "StudentNotFound",
                    "message", "Student not found for user with id: " + userId
                );
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
            }

            Long studentId = studentOpt.get().getId();
            GradeDto result = gradeService.createGradeWithValidation(studentId, teacherId, subjectId, gradeValue, gradeType, comments);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception ex) {
            // log internal error and return a minimal, non-sensitive response
            logger.error("Error creating grade by user ids", ex);
            java.util.Map<String, Object> err = java.util.Map.of(
                "error", "InternalServerError",
                "message", "Failed to create grade: " + ex.getMessage()
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
        }
    }

    @GetMapping("/{id:\\d+}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get grade by ID", description = "Retrieves grade information by ID")
    public ResponseEntity<GradeDto> getGradeById(
            @PathVariable @Parameter(description = "Grade ID") Long id) {
        GradeDto grade = gradeService.findById(id);
        return ResponseEntity.ok(grade);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get all grades", description = "Retrieves all grades (admin/manager/teacher)")
    public ResponseEntity<List<GradeDto>> getAllGrades() {
        List<GradeDto> grades = gradeService.findAll();
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER')")
    @Operation(summary = "Get grades by student", description = "Retrieves all grades for a specific student (Admin/Manager/Teacher)")
    public ResponseEntity<List<GradeDto>> getGradesByStudent(
            @PathVariable @Parameter(description = "Student ID") Long studentId) {
        List<GradeDto> grades = gradeService.findByStudentId(studentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/my-grades")
    @Operation(summary = "Get my grades", description = "Retrieves grades for the current student")
    public ResponseEntity<List<GradeDto>> getMyGrades(Authentication authentication) {
        
        logger.debug("getMyGrades called");
        logger.debug("Authentication: {}", authentication);
        
        // Check if authentication is null first
        if (authentication == null) {
            logger.debug("Authentication is null");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        logger.debug("Authentication name: {}", authentication.getName());
        logger.debug("Authentication authorities: {}", authentication.getAuthorities());
        
        // Get current user's student ID
        var currentUser = unitOfWork.getUserRepository().findByUsername(authentication.getName());
        if (currentUser.isEmpty()) {
            logger.debug("Current user not found");
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        var studentOpt = unitOfWork.getStudentRepository().findByUserId(currentUser.get().getId());
        if (studentOpt.isEmpty()) {
            logger.debug("Student record not found for user ID: {}", currentUser.get().getId());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Long studentId = studentOpt.get().getId();
        logger.debug("Found student ID: {}", studentId);
        
        List<GradeDto> grades = gradeService.findByStudentId(studentId);
        logger.debug("Found {} grades for student", grades.size());
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
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get grades by student and subject", description = "Retrieves grades for a specific student in a specific subject")
    public ResponseEntity<List<GradeDto>> getGradesByStudentAndSubject(
            @PathVariable @Parameter(description = "Student ID") Long studentId,
            @PathVariable @Parameter(description = "Subject ID") Long subjectId,
            Authentication authentication) {
        
        if (!canStudentAccessStudentId(studentId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        List<GradeDto> grades = gradeService.findByStudentAndSubject(studentId, subjectId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/student/{studentId}/final")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get final grades by student", description = "Retrieves final grades for a specific student")
    public ResponseEntity<List<GradeDto>> getFinalGradesByStudent(
            @PathVariable @Parameter(description = "Student ID") Long studentId,
            Authentication authentication) {
        
        if (!canStudentAccessStudentId(studentId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
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
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get student average grade", description = "Calculates overall average grade for a student")
    public ResponseEntity<Double> getStudentAverageGrade(
            @PathVariable @Parameter(description = "Student ID") Long studentId,
            Authentication authentication) {
        
        if (!canStudentAccessStudentId(studentId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Double averageGrade = gradeService.getOverallAverageGradeForStudent(studentId);
        return ResponseEntity.ok(averageGrade);
    }

    @GetMapping("/student/{studentId}/subject/{subjectId}/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TEACHER', 'STUDENT')")
    @Operation(summary = "Get student subject average", description = "Calculates average grade for a student in a specific subject")
    public ResponseEntity<Double> getStudentSubjectAverage(
            @PathVariable @Parameter(description = "Student ID") Long studentId,
            @PathVariable @Parameter(description = "Subject ID") Long subjectId,
            Authentication authentication) {
        
        if (!canStudentAccessStudentId(studentId, authentication)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        Double averageGrade = gradeService.getAverageGradeForStudentInSubject(studentId, subjectId);
        return ResponseEntity.ok(averageGrade);
    }

    @GetMapping("/diagnostics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Debug grades raw", description = "Returns raw grade ids and values for diagnostics")
    public ResponseEntity<List<Map<String, Object>>> debugGrades() {
    List<Grade> grades = unitOfWork.getGradeRepository().findAll();
        List<Map<String, Object>> simple = grades.stream()
                .map(g -> {
                    java.util.Map<String, Object> m = new java.util.HashMap<>();
                    m.put("id", g.getId());
                    m.put("gradeValue", g.getGradeValue());
                    m.put("studentId", g.getStudent() == null ? null : g.getStudent().getId());
                    return m;
                })
                .collect(Collectors.toList());
    return ResponseEntity.ok(simple);
    }
}
