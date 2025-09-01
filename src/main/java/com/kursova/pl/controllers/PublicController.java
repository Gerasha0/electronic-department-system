package com.kursova.pl.controllers;

import com.kursova.bll.dto.SubjectDto;
import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.services.SubjectService;
import com.kursova.bll.services.TeacherService;
import com.kursova.bll.services.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Public REST Controller for general information access
 * Available for all users including unregistered guests
 */
@RestController
@RequestMapping("/api/public")
@Tag(name = "Public Information", description = "Public access to general department information")
public class PublicController {

    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final StudentService studentService;

    public PublicController(TeacherService teacherService, SubjectService subjectService, StudentService studentService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
        this.studentService = studentService;
    }

    @GetMapping("/teachers")
    @Operation(summary = "Get all active teachers", description = "Retrieves information about all active teachers")
    public ResponseEntity<List<TeacherDto>> getAllTeachers() {
        List<TeacherDto> teachers = teacherService.findActiveTeachers();
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/teachers/{id}")
    @Operation(summary = "Get teacher by ID", description = "Retrieves teacher information by ID")
    public ResponseEntity<TeacherDto> getTeacherById(
            @PathVariable @Parameter(description = "Teacher ID") Long id) {
        TeacherDto teacher = teacherService.findById(id);
        return ResponseEntity.ok(teacher);
    }

    @GetMapping("/teachers/search")
    @Operation(summary = "Search teachers by name", description = "Searches teachers by name")
    public ResponseEntity<List<TeacherDto>> searchTeachers(
            @RequestParam @Parameter(description = "Search term") String q) {
        List<TeacherDto> teachers = teacherService.searchByName(q);
        return ResponseEntity.ok(teachers);
    }

    @GetMapping("/subjects")
    @Operation(summary = "Get all active subjects", description = "Retrieves information about all active subjects")
    public ResponseEntity<List<SubjectDto>> getAllSubjects() {
        List<SubjectDto> subjects = subjectService.findActiveSubjects();
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/subjects/{id}")
    @Operation(summary = "Get subject by ID", description = "Retrieves subject information by ID")
    public ResponseEntity<SubjectDto> getSubjectById(
            @PathVariable @Parameter(description = "Subject ID") Long id) {
        SubjectDto subject = subjectService.findById(id);
        return ResponseEntity.ok(subject);
    }

    @GetMapping("/subjects/search")
    @Operation(summary = "Search subjects by name", description = "Searches subjects by name")
    public ResponseEntity<List<SubjectDto>> searchSubjects(
            @RequestParam @Parameter(description = "Search term") String q) {
        List<SubjectDto> subjects = subjectService.searchByName(q);
        return ResponseEntity.ok(subjects);
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple health check endpoint")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK - Electronic Department System is running");
    }

    @GetMapping("/status")
    @Operation(summary = "System status", description = "Get system status information")
    public ResponseEntity<SystemStatusDto> getStatus() {
        SystemStatusDto status = new SystemStatusDto();
        status.setStatus("RUNNING");
        status.setTimestamp(java.time.LocalDateTime.now().toString());
        status.setVersion("0.3.0");
        return ResponseEntity.ok(status);
    }

    @GetMapping("/department-info")
    @Operation(summary = "Get department information", description = "Retrieves general department information")
    public ResponseEntity<DepartmentInfoDto> getDepartmentInfo() {
        DepartmentInfoDto info = new DepartmentInfoDto();
        info.setName("Кафедра комп'ютерних наук");
        info.setDescription("Кафедра комп'ютерних наук готує спеціалістів у галузі інформаційних технологій");
        info.setTotalTeachers(teacherService.findActiveTeachers().size());
        info.setTotalSubjects(subjectService.findActiveSubjects().size());

        return ResponseEntity.ok(info);
    }

    @GetMapping("/students")
    @Operation(summary = "Get all active students", description = "Retrieves information about all active students with calculated data")
    public ResponseEntity<List<StudentDto>> getAllStudents() {
        List<StudentDto> students = studentService.findActiveStudents();
        return ResponseEntity.ok(students);
    }

    /**
     * DTO for department information
     */
    public static class DepartmentInfoDto {
        private String name;
        private String description;
        private Integer totalTeachers;
        private Integer totalSubjects;

        // Getters and Setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Integer getTotalTeachers() {
            return totalTeachers;
        }

        public void setTotalTeachers(Integer totalTeachers) {
            this.totalTeachers = totalTeachers;
        }

        public Integer getTotalSubjects() {
            return totalSubjects;
        }

        public void setTotalSubjects(Integer totalSubjects) {
            this.totalSubjects = totalSubjects;
        }
    }

    /**
     * DTO for system status
     */
    public static class SystemStatusDto {
        private String status;
        private String timestamp;
        private String version;

        // Getters and Setters
        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
