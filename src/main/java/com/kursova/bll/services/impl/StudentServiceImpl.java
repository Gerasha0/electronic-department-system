package com.kursova.bll.services.impl;

import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.dto.StudentGroupDto;
import com.kursova.bll.mappers.StudentMapper;
import com.kursova.bll.services.StudentService;
import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.Grade;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of StudentService
 */
@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final UnitOfWork unitOfWork;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentServiceImpl(UnitOfWork unitOfWork, StudentMapper studentMapper) {
        this.unitOfWork = unitOfWork;
        this.studentMapper = studentMapper;
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

        studentMapper.updateEntityFromDto(studentDto, existingStudent);
        Student updatedStudent = unitOfWork.getStudentRepository().save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!unitOfWork.getStudentRepository().existsById(id)) {
            throw new RuntimeException("Student not found with id: " + id);
        }
        unitOfWork.getStudentRepository().deleteById(id);
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
                .filter(student -> student.getIsActive())
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
        List<Student> students = unitOfWork.getStudentRepository().searchByName(name.trim());
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

        // Calculate course based on enrollment year
        if (student.getEnrollmentYear() != null) {
            int currentYear = LocalDateTime.now().getYear();
            int course = currentYear - student.getEnrollmentYear() + 1;
            // Course should be between 1 and 6 years
            course = Math.max(1, Math.min(course, 6));
            dto.setCourse(course);
        } else {
            dto.setCourse(1); // Default to 1st course
        }

        // Calculate average grade
        Double averageGrade = calculateAverageGrade(student.getId());
        dto.setAverageGrade(averageGrade);

        return dto;
    }
}
