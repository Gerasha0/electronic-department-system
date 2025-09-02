package com.kursova.pl.controllers;

import com.kursova.bll.services.ArchiveService;
import com.kursova.dal.entities.ArchivedGrade;
import com.kursova.dal.entities.ArchivedStudent;
import com.kursova.dal.entities.ArchivedStudentGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Archive management (Admin only)
 */
@RestController
@RequestMapping("/api/archive")
@Tag(name = "Archive Management", description = "Operations for managing archived data (Admin only)")
@PreAuthorize("hasRole('ADMIN')")
public class ArchiveController {

    private final ArchiveService archiveService;

    @Autowired
    public ArchiveController(ArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @GetMapping("/groups")
    @Operation(summary = "Get all archived groups", description = "Retrieves all archived student groups")
    public ResponseEntity<List<ArchivedStudentGroup>> getAllArchivedGroups() {
        List<ArchivedStudentGroup> archivedGroups = archiveService.getAllArchivedGroups();
        return ResponseEntity.ok(archivedGroups);
    }

    @GetMapping("/students")
    @Operation(summary = "Get all archived students", description = "Retrieves all archived students")
    public ResponseEntity<List<ArchivedStudent>> getAllArchivedStudents() {
        List<ArchivedStudent> archivedStudents = archiveService.getAllArchivedStudents();
        return ResponseEntity.ok(archivedStudents);
    }

    @GetMapping("/grades")
    @Operation(summary = "Get all archived grades", description = "Retrieves all archived grades")
    public ResponseEntity<List<ArchivedGrade>> getAllArchivedGrades() {
        List<ArchivedGrade> archivedGrades = archiveService.getAllArchivedGrades();
        return ResponseEntity.ok(archivedGrades);
    }

    @GetMapping("/groups/search")
    @Operation(summary = "Search archived groups", description = "Searches archived groups by name or code")
    public ResponseEntity<List<ArchivedStudentGroup>> searchArchivedGroups(
            @RequestParam @Parameter(description = "Search term") String searchTerm) {
        List<ArchivedStudentGroup> groups = archiveService.searchArchivedGroups(searchTerm);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/students/search")
    @Operation(summary = "Search archived students", description = "Searches archived students by student number")
    public ResponseEntity<List<ArchivedStudent>> searchArchivedStudents(
            @RequestParam @Parameter(description = "Search term") String searchTerm) {
        List<ArchivedStudent> students = archiveService.searchArchivedStudents(searchTerm);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/students/by-group/{originalGroupId}")
    @Operation(summary = "Get archived students by group", description = "Retrieves archived students from a specific group")
    public ResponseEntity<List<ArchivedStudent>> getArchivedStudentsByGroup(
            @PathVariable @Parameter(description = "Original Group ID") Long originalGroupId) {
        List<ArchivedStudent> students = archiveService.getArchivedStudentsByGroupId(originalGroupId);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/grades/by-student/{originalStudentId}")
    @Operation(summary = "Get archived grades by student", description = "Retrieves archived grades for a specific student")
    public ResponseEntity<List<ArchivedGrade>> getArchivedGradesByStudent(
            @PathVariable @Parameter(description = "Original Student ID") Long originalStudentId) {
        List<ArchivedGrade> grades = archiveService.getArchivedGradesByStudentId(originalStudentId);
        return ResponseEntity.ok(grades);
    }

    @GetMapping("/groups/by-date-range")
    @Operation(summary = "Get archived groups by date range", description = "Retrieves archived groups within a date range")
    public ResponseEntity<List<ArchivedStudentGroup>> getArchivedGroupsByDateRange(
            @RequestParam @Parameter(description = "Start date") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @Parameter(description = "End date") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<ArchivedStudentGroup> groups = archiveService.getArchivedGroupsByDateRange(startDate, endDate);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get archive statistics", description = "Retrieves statistics about archived data")
    public ResponseEntity<ArchiveService.ArchiveStatistics> getArchiveStatistics() {
        ArchiveService.ArchiveStatistics stats = archiveService.getArchiveStatistics();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/students/{studentId}")
    @Operation(summary = "Archive student manually", description = "Manually archives a student and their data")
    public ResponseEntity<String> archiveStudent(
            @PathVariable @Parameter(description = "Student ID") Long studentId,
            @RequestParam(defaultValue = "Manual archiving") @Parameter(description = "Archive reason") String reason) {
        try {
            archiveService.archiveStudent(studentId, "ADMIN", reason);
            return ResponseEntity.ok("Student archived successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error archiving student: " + e.getMessage());
        }
    }

    @PostMapping("/groups/{groupId}")
    @Operation(summary = "Archive group manually", description = "Manually archives a group and all related data")
    public ResponseEntity<String> archiveGroup(
            @PathVariable @Parameter(description = "Group ID") Long groupId,
            @RequestParam(defaultValue = "Manual archiving") @Parameter(description = "Archive reason") String reason) {
        try {
            archiveService.archiveStudentGroup(groupId, "ADMIN", reason);
            return ResponseEntity.ok("Group archived successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error archiving group: " + e.getMessage());
        }
    }
}
