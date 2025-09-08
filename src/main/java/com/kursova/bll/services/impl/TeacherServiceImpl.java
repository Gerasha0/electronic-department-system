package com.kursova.bll.services.impl;

import com.kursova.bll.dto.GradeDto;
import com.kursova.bll.dto.StudentDto;
import com.kursova.bll.dto.SubjectDto;
import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.dto.UserDto;
import com.kursova.bll.mappers.GradeMapper;
import com.kursova.bll.mappers.SubjectMapper;
import com.kursova.bll.mappers.TeacherMapper;
import com.kursova.bll.services.StudentService;
import com.kursova.bll.services.TeacherService;
import com.kursova.dal.entities.Grade;
import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.Teacher;
import com.kursova.dal.entities.Subject;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of TeacherService for managing teachers
 */
@Service
@Transactional
public class TeacherServiceImpl implements TeacherService {

    private final UnitOfWork unitOfWork;
    private final TeacherMapper teacherMapper;
    private final GradeMapper gradeMapper;
    private final SubjectMapper subjectMapper;
    private final StudentService studentService;

    @Autowired
    public TeacherServiceImpl(UnitOfWork unitOfWork, TeacherMapper teacherMapper, 
                             GradeMapper gradeMapper, SubjectMapper subjectMapper, 
                             StudentService studentService) {
        this.unitOfWork = unitOfWork;
        this.teacherMapper = teacherMapper;
        this.gradeMapper = gradeMapper;
        this.subjectMapper = subjectMapper;
        this.studentService = studentService;
    }

    // Implementation of BaseService methods

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> findAll() {
        return unitOfWork.getTeacherRepository().findAll()
                .stream()
                .map(teacherMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherDto findById(Long id) {
        Teacher teacher = unitOfWork.getTeacherRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));
        return teacherMapper.toDto(teacher);
    }

    @Override
    public TeacherDto create(TeacherDto teacherDto) {
        if (teacherDto == null) {
            throw new IllegalArgumentException("Teacher DTO cannot be null");
        }

        Teacher teacher = teacherMapper.toEntity(teacherDto);
        teacher.setCreatedAt(LocalDateTime.now());
        teacher.setUpdatedAt(LocalDateTime.now());

        Teacher savedTeacher = unitOfWork.getTeacherRepository().save(teacher);
        return teacherMapper.toDto(savedTeacher);
    }

    @Override
    public TeacherDto update(Long id, TeacherDto teacherDto) {
        if (teacherDto == null) {
            throw new IllegalArgumentException("Teacher DTO cannot be null");
        }

        Teacher existingTeacher = unitOfWork.getTeacherRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + id));

        teacherMapper.updateEntityFromDto(teacherDto, existingTeacher);
        existingTeacher.setUpdatedAt(LocalDateTime.now());

        Teacher updatedTeacher = unitOfWork.getTeacherRepository().save(existingTeacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Override
    public void delete(Long id) {
        if (!unitOfWork.getTeacherRepository().existsById(id)) {
            throw new RuntimeException("Teacher not found with id: " + id);
        }
        unitOfWork.getTeacherRepository().deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return unitOfWork.getTeacherRepository().existsById(id);
    }

    // Implementation of TeacherService specific methods

    @Override
    @Transactional(readOnly = true)
    public TeacherDto findByUserId(Long userId) {
        return unitOfWork.getTeacherRepository().findByUserId(userId)
                .map(teacherMapper::toDto)
                .orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherDto findByUsername(String username) {
        // Simplified implementation - would need proper query
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> findActiveTeachers() {
        List<Teacher> teachers = unitOfWork.getTeacherRepository().findAll()
                .stream()
                .filter(Teacher::getIsActive)
                .toList();

        return teachers.stream()
                .map(this::mapTeacherWithSubjects)
                .collect(Collectors.toList());
    }

    private TeacherDto mapTeacherWithSubjects(Teacher teacher) {
        TeacherDto dto = teacherMapper.toDto(teacher);

        // Manually add subjects to avoid circular dependency
        if (teacher.getSubjects() != null && !teacher.getSubjects().isEmpty()) {
            List<com.kursova.bll.dto.SubjectDto> subjectDtos = teacher.getSubjects().stream()
                    .map(subject -> {
                        com.kursova.bll.dto.SubjectDto subjectDto = new com.kursova.bll.dto.SubjectDto();
                        subjectDto.setId(subject.getId());
                        subjectDto.setSubjectName(subject.getSubjectName());
                        subjectDto.setSubjectCode(subject.getSubjectCode());
                        subjectDto.setCredits(subject.getCredits());
                        subjectDto.setSemester(subject.getSemester());
                        return subjectDto;
                    })
                    .collect(Collectors.toList());
            dto.setSubjects(subjectDtos);
        }

        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> searchByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return findActiveTeachers();
        }
        List<Teacher> teachers = unitOfWork.getTeacherRepository().searchByNameOrEmail(name.trim());
        return teachers.stream()
                .map(this::mapTeacherWithSubjects)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> findByAcademicTitle(String academicTitle) {
        // Simplified - return all teachers for now
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> findByDepartmentPosition(String position) {
        // Simplified - return all teachers for now
        return findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherDto> findBySubjectId(Long subjectId) {
        // Simplified - return all teachers for now
        return findAll();
    }

    @Override
    public TeacherDto createWithUser(UserDto userDto, String password, TeacherDto teacherDto) {
        // This would typically create both User and Teacher
        // For now, simplified implementation
        return create(teacherDto);
    }

    @Override
    public TeacherDto assignSubject(Long teacherId, Long subjectId) {
        Teacher teacher = unitOfWork.getTeacherRepository().findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        Subject subject = unitOfWork.getSubjectRepository().findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        teacher.getSubjects().add(subject);
        teacher.setUpdatedAt(LocalDateTime.now());

        Teacher updatedTeacher = unitOfWork.getTeacherRepository().save(teacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Override
    public TeacherDto removeSubject(Long teacherId, Long subjectId) {
        Teacher teacher = unitOfWork.getTeacherRepository().findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        Subject subject = unitOfWork.getSubjectRepository().findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        teacher.getSubjects().remove(subject);
        teacher.setUpdatedAt(LocalDateTime.now());

        Teacher updatedTeacher = unitOfWork.getTeacherRepository().save(teacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Override
    public TeacherDto activateTeacher(Long teacherId) {
        Teacher teacher = unitOfWork.getTeacherRepository().findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        teacher.setIsActive(true);
        teacher.setUpdatedAt(LocalDateTime.now());

        Teacher updatedTeacher = unitOfWork.getTeacherRepository().save(teacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Override
    public TeacherDto deactivateTeacher(Long teacherId) {
        Teacher teacher = unitOfWork.getTeacherRepository().findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        teacher.setIsActive(false);
        teacher.setUpdatedAt(LocalDateTime.now());

        Teacher updatedTeacher = unitOfWork.getTeacherRepository().save(teacher);
        return teacherMapper.toDto(updatedTeacher);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SubjectDto> findSubjectsByTeacherId(Long teacherId) {
        Teacher teacher = unitOfWork.getTeacherRepository().findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));
        
        List<Subject> subjects = new ArrayList<>(teacher.getSubjects());
        return subjectMapper.toDtoList(subjects);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findGradesByTeacherId(Long teacherId) {
        // Find all grades for subjects taught by this teacher
        Teacher teacher = unitOfWork.getTeacherRepository().findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));
        
        List<Grade> grades = unitOfWork.getGradeRepository().findByTeacherIdOrderByGradeDateDesc(teacherId);
        return gradeMapper.toDtoList(grades);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentDto> findStudentsByTeacherId(Long teacherId) {
        // Find students who have grades from this teacher or study subjects taught by this teacher
        List<Student> students = unitOfWork.getStudentRepository().findStudentsByTeacherId(teacherId);
        return students.stream()
                .map(student -> studentService.findById(student.getId()))
                .collect(Collectors.toList());
    }
}
