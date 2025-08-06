package com.kursova.pl.controllers;

import com.kursova.bll.dto.SubjectDto;
import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.services.SubjectService;
import com.kursova.bll.services.TeacherService;
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
    
    public PublicController(TeacherService teacherService, SubjectService subjectService) {
        this.teacherService = teacherService;
        this.subjectService = subjectService;
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
            @RequestParam @Parameter(description = "Search term") String name) {
        List<TeacherDto> teachers = teacherService.searchByName(name);
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
            @RequestParam @Parameter(description = "Search term") String name) {
        List<SubjectDto> subjects = subjectService.searchByName(name);
        return ResponseEntity.ok(subjects);
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
}
