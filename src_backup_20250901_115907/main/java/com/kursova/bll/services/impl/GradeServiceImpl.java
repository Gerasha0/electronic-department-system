package com.kursova.bll.services.impl;

import com.kursova.bll.dto.GradeDto;
import com.kursova.bll.mappers.GradeMapper;
import com.kursova.bll.services.GradeService;
import com.kursova.dal.entities.Grade;
import com.kursova.dal.entities.GradeType;
import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.Subject;
import com.kursova.dal.entities.Teacher;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of GradeService for managing grades
 */
@Service
@Transactional
public class GradeServiceImpl implements GradeService {
    
    private final UnitOfWork unitOfWork;
    private final GradeMapper gradeMapper;
    private static final Logger log = LoggerFactory.getLogger(GradeServiceImpl.class);
    
    @Autowired
    public GradeServiceImpl(UnitOfWork unitOfWork, GradeMapper gradeMapper) {
        this.unitOfWork = unitOfWork;
        this.gradeMapper = gradeMapper;
    }
    
    // Implementation of BaseService methods
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findAll() {
        // Using full mapping to show student, teacher and subject names
        return unitOfWork.getGradeRepository().findAllWithRelations()
            .stream()
            .map(gradeMapper::toDto)
            .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<GradeDto> getAllGrades() {
        return findAll();
    }
    
    @Override
    @Transactional(readOnly = true)
    public GradeDto findById(Long id) {
    Grade grade = unitOfWork.getGradeRepository().findByIdWithRelations(id)
        .orElseGet(() -> unitOfWork.getGradeRepository().findById(id)
            .orElseThrow(() -> new RuntimeException("Grade not found with id: " + id)));
        return gradeMapper.toDto(grade);
    }
    
    @Override
    public GradeDto create(GradeDto gradeDto) {
        if (gradeDto == null) {
            throw new IllegalArgumentException("Grade DTO cannot be null");
        }
        
        Grade grade = gradeMapper.toEntity(gradeDto);
        grade.setCreatedAt(LocalDateTime.now());
        grade.setUpdatedAt(LocalDateTime.now());
        grade.setGradeDate(LocalDateTime.now());
        // Ensure related entities are set from DTO IDs to avoid null foreign keys
        if (gradeDto.getStudentId() == null) {
            throw new IllegalArgumentException("studentId is required");
        }
        if (gradeDto.getTeacherId() == null) {
            throw new IllegalArgumentException("teacherId is required");
        }
        if (gradeDto.getSubjectId() == null) {
            throw new IllegalArgumentException("subjectId is required");
        }

        Student student = unitOfWork.getStudentRepository().findById(gradeDto.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + gradeDto.getStudentId()));
        Teacher teacher = unitOfWork.getTeacherRepository().findById(gradeDto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + gradeDto.getTeacherId()));
        Subject subject = unitOfWork.getSubjectRepository().findById(gradeDto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + gradeDto.getSubjectId()));

        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setSubject(subject);

    Grade savedGrade = unitOfWork.getGradeRepository().save(grade);
    // Reload saved grade with relations to ensure mapper can access nested user/subject fields
    Grade savedWithRelations = unitOfWork.getGradeRepository().findByIdWithRelations(savedGrade.getId())
        .orElse(savedGrade);
    return gradeMapper.toDto(savedWithRelations);
    }
    
    public GradeDto save(GradeDto gradeDto) {
        return create(gradeDto);
    }
    
    @Override
    public GradeDto update(Long id, GradeDto gradeDto) {
        if (gradeDto == null) {
            throw new IllegalArgumentException("Grade DTO cannot be null");
        }
        
        Grade existingGrade = unitOfWork.getGradeRepository().findById(id)
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + id));
        
        gradeMapper.updateEntityFromDto(gradeDto, existingGrade);
        existingGrade.setUpdatedAt(LocalDateTime.now());
        
        Grade updatedGrade = unitOfWork.getGradeRepository().save(existingGrade);
        return gradeMapper.toDto(updatedGrade);
    }
    
    @Override
    public void delete(Long id) {
        if (!unitOfWork.getGradeRepository().existsById(id)) {
            throw new RuntimeException("Grade not found with id: " + id);
        }
        unitOfWork.getGradeRepository().deleteById(id);
    }
    
    public void deleteById(Long id) {
        delete(id);
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return unitOfWork.getGradeRepository().existsById(id);
    }
    
    public long count() {
        return unitOfWork.getGradeRepository().count();
    }
    
    // Implementation of GradeService methods
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findByStudentId(Long studentId) {
        return unitOfWork.getGradeRepository().findByStudentIdOrderByGradeDateDesc(studentId)
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findByTeacherId(Long teacherId) {
        return unitOfWork.getGradeRepository().findByTeacherIdOrderByGradeDateDesc(teacherId)
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findBySubjectId(Long subjectId) {
        return unitOfWork.getGradeRepository().findBySubjectIdOrderByGradeDateDesc(subjectId)
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findByStudentAndSubject(Long studentId, Long subjectId) {
        return unitOfWork.getGradeRepository().findByStudentIdAndSubjectIdOrderByGradeDateDesc(studentId, subjectId)
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public GradeDto findByStudentSubjectAndType(Long studentId, Long subjectId, GradeType gradeType) {
        return unitOfWork.getGradeRepository()
                .findByStudentIdAndSubjectIdAndGradeType(studentId, subjectId, gradeType)
                .map(gradeMapper::toDto)
                .orElse(null);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findFinalGradesByStudent(Long studentId) {
        return unitOfWork.getGradeRepository().findByStudentIdAndIsFinalTrueOrderBySubjectSubjectNameAsc(studentId)
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findByGradeType(GradeType gradeType) {
        return unitOfWork.getGradeRepository().findByGradeTypeOrderByGradeDateDesc(gradeType)
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<GradeDto> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return unitOfWork.getGradeRepository().findByGradeDateBetweenOrderByGradeDateDesc(startDate, endDate)
                .stream()
                .map(gradeMapper::toDto)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getAverageGradeForStudentInSubject(Long studentId, Long subjectId) {
        return unitOfWork.getGradeRepository().getAverageGradeForStudentInSubject(studentId, subjectId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public Double getOverallAverageGradeForStudent(Long studentId) {
        return unitOfWork.getGradeRepository().getOverallAverageGradeForStudent(studentId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getTopPerformingStudents() {
        return unitOfWork.getGradeRepository().findTopPerformingStudents();
    }
    
    @Override
    @Transactional(readOnly = true)
    public Long countGradesByTeacherAndSubject(Long teacherId, Long subjectId) {
        return unitOfWork.getGradeRepository().countGradesByTeacherAndSubject(teacherId, subjectId);
    }
    
    @Override
    public GradeDto createGradeWithValidation(Long studentId, Long teacherId, Long subjectId, 
                                             Integer gradeValue, GradeType gradeType, String comments) {
        // Validate grade value
        if (gradeValue == null || gradeValue < 0 || gradeValue > 100) {
            throw new IllegalArgumentException("Grade value must be between 0 and 100");
        }
        
        // Check if entities exist
        Student student = unitOfWork.getStudentRepository().findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));
        
        Teacher teacher = unitOfWork.getTeacherRepository().findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));
        
        Subject subject = unitOfWork.getSubjectRepository().findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        
        // Check for existing grade of same type
        if (unitOfWork.getGradeRepository()
                .findByStudentIdAndSubjectIdAndGradeType(studentId, subjectId, gradeType).isPresent()) {
            throw new IllegalStateException("Grade of this type already exists for this student and subject");
        }
        
        Grade grade = new Grade();
        grade.setStudent(student);
        grade.setTeacher(teacher);
        grade.setSubject(subject);
        grade.setGradeValue(gradeValue);
        grade.setGradeType(gradeType);
        grade.setComments(comments);
        grade.setIsFinal(false);
        grade.setGradeDate(LocalDateTime.now());
        grade.setCreatedAt(LocalDateTime.now());
        grade.setUpdatedAt(LocalDateTime.now());
        
    log.debug("Creating grade - studentId={}, teacherId={}, subjectId={}",
        studentId, teacherId, subjectId);
    log.debug("Before save - grade.getStudent()={}, grade.getTeacher()={}, grade.getSubject()={}",
        grade.getStudent(), grade.getTeacher(), grade.getSubject());

    Grade savedGrade = unitOfWork.getGradeRepository().save(grade);
    log.debug("Saved grade id={}", savedGrade.getId());
        return gradeMapper.toDto(savedGrade);
    }
    
    @Override
    public GradeDto updateGradeWithValidation(Long gradeId, Integer gradeValue, String comments) {
        // Validate grade value
        if (gradeValue == null || gradeValue < 0 || gradeValue > 100) {
            throw new IllegalArgumentException("Grade value must be between 0 and 100");
        }
        
        Grade grade = unitOfWork.getGradeRepository().findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + gradeId));
        
        grade.setGradeValue(gradeValue);
        grade.setComments(comments);
        grade.setUpdatedAt(LocalDateTime.now());
        
        Grade updatedGrade = unitOfWork.getGradeRepository().save(grade);
        return gradeMapper.toDto(updatedGrade);
    }
    
    @Override
    public GradeDto markAsFinal(Long gradeId) {
        Grade grade = unitOfWork.getGradeRepository().findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + gradeId));
        
        grade.setIsFinal(true);
        grade.setUpdatedAt(LocalDateTime.now());
        
        Grade updatedGrade = unitOfWork.getGradeRepository().save(grade);
        return gradeMapper.toDto(updatedGrade);
    }
}
