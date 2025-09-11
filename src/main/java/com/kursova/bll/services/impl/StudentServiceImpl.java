package com.kursova.bll.services.impl;

import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.mappers.StudentMapper;
import com.kursova.bll.services.ArchiveService;
import com.kursova.bll.services.StudentService;
import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.StudentGroup;
import com.kursova.dal.entities.EducationLevel;
import com.kursova.dal.entities.StudyForm;
import com.kursova.dal.entities.Grade;
import com.kursova.dal.entities.User;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Implementation of StudentService
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final UnitOfWork unitOfWork;
    private final StudentMapper studentMapper;
    private final ArchiveService archiveService;

    @Autowired
    public StudentServiceImpl(UnitOfWork unitOfWork, StudentMapper studentMapper, ArchiveService archiveService) {
        this.unitOfWork = unitOfWork;
        this.studentMapper = studentMapper;
        this.archiveService = archiveService;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findAll() {
        return unitOfWork.getStudentRepository().findAll()
                .stream()
                .map(this::mapStudentWithCalculatedData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto findById(Long id) {
        Student student = unitOfWork.getStudentRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));
        return mapStudentWithCalculatedData(student);
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto findByIdWithCalculatedData(Long id) {
        return findById(id); // Already includes calculated data
    }

    @Override
    @Transactional
    public StudentDto create(StudentDto studentDto) {
        Student student = studentMapper.toEntity(studentDto);
        student.setIsActive(true);
        Student savedStudent = unitOfWork.getStudentRepository().save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    @Transactional
    public StudentDto update(Long id, StudentDto studentDto) {
        Student existingStudent = unitOfWork.getStudentRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + id));

        // Check for conflicts if student is in a group
        if (existingStudent.getGroup() != null) {
            StudentGroup group = existingStudent.getGroup();
            
            // Check if trying to change education level, study form, or course year
            if (studentDto.getEducationLevel() != null && 
                !studentDto.getEducationLevel().equals(group.getEducationLevel())) {
                throw new RuntimeException("Неможливо змінити рівень освіти студента. " +
                    "Студент знаходиться у групі з рівнем освіти: " + 
                    translateEducationLevel(group.getEducationLevel()));
            }
            
            if (studentDto.getStudyForm() != null && 
                !studentDto.getStudyForm().equals(group.getStudyForm())) {
                throw new RuntimeException("Неможливо змінити форму навчання студента. " +
                    "Студент знаходиться у групі з формою навчання: " + 
                    translateStudyForm(group.getStudyForm()));
            }
            
            if (studentDto.getCourseYear() != null && 
                !studentDto.getCourseYear().equals(group.getCourseYear())) {
                throw new RuntimeException("Неможливо змінити курс студента. " +
                    "Студент знаходиться у групі " + group.getCourseYear() + " курсу.");
            }
        }

        studentMapper.updateEntityFromDto(studentDto, existingStudent);
        Student updatedStudent = unitOfWork.getStudentRepository().save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }
    
    private String translateEducationLevel(EducationLevel level) {
        return switch (level) {
            case BACHELOR -> "Бакалавр";
            case SPECIALIST -> "Спеціаліст";
            case MASTER -> "Магістр";
            case PHD -> "Аспірант";
        };
    }
    
    private String translateStudyForm(StudyForm form) {
        return switch (form) {
            case FULL_TIME -> "Денна";
            case EVENING -> "Вечірня";
            case PART_TIME -> "Заочна";
            case DISTANCE -> "Дистанційна";
        };
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!unitOfWork.getStudentRepository().existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        
        // Use archive service instead of direct deletion to handle foreign key constraints
        archiveService.archiveStudent(id, "ADMIN", "Student deleted via admin interface");
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return unitOfWork.getStudentRepository().existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findActiveStudents() {
        return unitOfWork.getStudentRepository().findAll()
                .stream()
                .filter(Student::getIsActive)
                .map(this::mapStudentWithCalculatedData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public StudentDto findByUserId(Long userId) {
        Student student = unitOfWork.getStudentRepository().findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Student not found for user id: " + userId));
        return mapStudentWithCalculatedData(student);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findActiveStudents();
        }
        List<Student> students = unitOfWork.getStudentRepository().searchByNameOrEmail(name.trim());
        return students.stream()
                .map(this::mapStudentWithCalculatedData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findByGroup(Long groupId) {
        List<Student> students = unitOfWork.getStudentRepository().findByGroupIdAndIsActiveTrueOrderByUserLastNameAsc(groupId);
        return students.stream()
                .map(this::mapStudentWithCalculatedData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findStudentsWithoutGroup() {
        List<Student> students = unitOfWork.getStudentRepository().findByGroupIsNullAndIsActiveTrueOrderByUserLastNameAsc();
        return students.stream()
                .map(this::mapStudentWithCalculatedData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findByEnrollmentYear(Integer year) {
        List<Student> students = unitOfWork.getStudentRepository().findByEnrollmentYearAndIsActiveTrueOrderByUserLastNameAsc(year);
        return students.stream()
                .map(this::mapStudentWithCalculatedData)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateAverageGrade(Long studentId) {
        try {
            List<Grade> grades = unitOfWork.getGradeRepository()
                    .findByStudentIdOrderByGradeDateDesc(studentId);

            if (grades.isEmpty()) {
                return 0.0;
            }

            // Calculate average of all grades
            double sum = grades.stream()
                    .mapToInt(Grade::getGradeValue)
                    .average()
                    .orElse(0.0);

            return Math.round(sum * 100.0) / 100.0; // Round to 2 decimal places
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Maps Student entity to DTO with calculated data (course, average grade)
     */
    private StudentDto mapStudentWithCalculatedData(Student student) {
        StudentDto dto = studentMapper.toDto(student);

        // Use actual courseYear from database instead of calculating from enrollment year
        // This allows manual adjustment of course year by administrators
        if (student.getCourseYear() != null) {
            dto.setCourse(student.getCourseYear());
        } else {
            // Fallback: Calculate course based on enrollment year only if courseYear is null
            if (student.getEnrollmentYear() != null) {
                int currentYear = LocalDateTime.now().getYear();
                int course = currentYear - student.getEnrollmentYear() + 1;
                // Course should be between 1 and 6 years
                course = Math.max(1, Math.min(course, 6));
                dto.setCourse(course);
            } else {
                dto.setCourse(1); // Default to 1st course
            }
        }

        // Calculate average grade
        Double averageGrade = calculateAverageGrade(student.getId());
        dto.setAverageGrade(averageGrade);

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findByGroupId(Long groupId) {
        return unitOfWork.getStudentRepository().findByGroupId(groupId)
                .stream()
                .map(this::mapStudentWithCalculatedData)
                .collect(Collectors.toList());
    }

    @Override
    public StudentDto activateStudent(Long studentId) {
        Student student = unitOfWork.getStudentRepository().findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setIsActive(true);
        student.setUpdatedAt(LocalDateTime.now());
        
        Student updatedStudent = unitOfWork.getStudentRepository().save(student);
        return mapStudentWithCalculatedData(updatedStudent);
    }

    @Override
    public StudentDto deactivateStudent(Long studentId) {
        Student student = unitOfWork.getStudentRepository().findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setIsActive(false);
        student.setUpdatedAt(LocalDateTime.now());
        
        Student updatedStudent = unitOfWork.getStudentRepository().save(student);
        return mapStudentWithCalculatedData(updatedStudent);
    }

    @Override
    @Transactional
    public StudentDto assignToGroup(Long studentId, Long groupId) {
        Student student = unitOfWork.getStudentRepository().findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        var group = unitOfWork.getStudentGroupRepository().findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        
        // Update student properties to match the group
        student.setGroup(group);
        student.setEducationLevel(group.getEducationLevel());
        student.setStudyForm(group.getStudyForm());
        student.setCourseYear(group.getCourseYear());
        student.setUpdatedAt(LocalDateTime.now());
        
        Student updatedStudent = unitOfWork.getStudentRepository().save(student);
        return mapStudentWithCalculatedData(updatedStudent);
    }

    @Override
    @Transactional
    public StudentDto removeFromGroup(Long studentId) {
        Student student = unitOfWork.getStudentRepository().findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        student.setGroup(null);
        student.setUpdatedAt(LocalDateTime.now());
        
        Student updatedStudent = unitOfWork.getStudentRepository().save(student);
        return mapStudentWithCalculatedData(updatedStudent);
    }

    @Override
    public List<Object> searchStudentsForGroup(String query, Long groupId) {
        try {
            List<Student> allStudents = unitOfWork.getStudentRepository().findAll();
            
            return allStudents.stream()
                .filter(student -> {
                    if (student.getUser() == null) return false;
                    
                    String firstName = student.getUser().getFirstName() != null ? 
                        student.getUser().getFirstName().toLowerCase() : "";
                    String lastName = student.getUser().getLastName() != null ? 
                        student.getUser().getLastName().toLowerCase() : "";
                    String email = student.getUser().getEmail() != null ? 
                        student.getUser().getEmail().toLowerCase() : "";
                    String queryLower = query.toLowerCase();
                    
                    return firstName.contains(queryLower) || 
                           lastName.contains(queryLower) || 
                           email.contains(queryLower);
                })
                .map(student -> {
                    StudentDto dto = mapStudentWithCalculatedData(student);
                    
                    // Add group information
                    List<String> groupNames = new ArrayList<>();
                    if (student.getGroup() != null) {
                        groupNames.add(student.getGroup().getGroupName());
                    }
                    
                    // Create a map with additional group info
                    Map<String, Object> studentWithGroups = new HashMap<>();
                    studentWithGroups.put("id", dto.getId());
                    studentWithGroups.put("firstName", dto.getUser() != null ? dto.getUser().getFirstName() : "");
                    studentWithGroups.put("lastName", dto.getUser() != null ? dto.getUser().getLastName() : "");
                    studentWithGroups.put("email", dto.getUser() != null ? dto.getUser().getEmail() : "");
                    studentWithGroups.put("user", dto.getUser());
                    
                    List<Map<String, Object>> groups = new ArrayList<>();
                    if (student.getGroup() != null) {
                        Map<String, Object> groupInfo = new HashMap<>();
                        groupInfo.put("id", student.getGroup().getId());
                        groupInfo.put("groupName", student.getGroup().getGroupName());
                        groupInfo.put("name", student.getGroup().getGroupName());
                        groups.add(groupInfo);
                    }
                    studentWithGroups.put("groups", groups);
                    
                    return (Object) studentWithGroups;
                })
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public StudentDto findByEmail(String email) {
        // Note: This method name is misleading - it's actually called with username from JWT authentication
        // The 'email' parameter contains username from Authentication.getName()
        String username = email; // Authentication.getName() returns username, not actual email

        User user = unitOfWork.getUserRepository().findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        
        Student student = unitOfWork.getStudentRepository().findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Student not found for user with username: " + username));
        
        return mapStudentWithCalculatedData(student);
    }
}
