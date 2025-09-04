package com.kursova.bll.services.impl;

import com.kursova.bll.dto.SubjectDto;
import com.kursova.bll.dto.TeacherDto;
import com.kursova.bll.mappers.SubjectMapper;
import com.kursova.bll.mappers.TeacherMapper;
import com.kursova.bll.mappers.StudentGroupMapper;
import com.kursova.bll.services.SubjectService;
import com.kursova.dal.entities.Subject;
import com.kursova.dal.entities.Teacher;
import com.kursova.dal.entities.StudentGroup;
import com.kursova.dal.entities.AssessmentType;
import com.kursova.dal.repositories.SubjectRepository;
import com.kursova.dal.repositories.TeacherRepository;
import com.kursova.dal.repositories.StudentGroupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of SubjectService
 */
@Service
@Transactional(readOnly = true)
public class SubjectServiceImpl implements SubjectService {

    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final StudentGroupRepository groupRepository;
    private final SubjectMapper subjectMapper;
    private final TeacherMapper teacherMapper;
    private final StudentGroupMapper groupMapper;

    public SubjectServiceImpl(SubjectRepository subjectRepository,
                             TeacherRepository teacherRepository,
                             StudentGroupRepository groupRepository,
                             SubjectMapper subjectMapper,
                             TeacherMapper teacherMapper,
                             StudentGroupMapper groupMapper) {
        this.subjectRepository = subjectRepository;
        this.teacherRepository = teacherRepository;
        this.groupRepository = groupRepository;
        this.subjectMapper = subjectMapper;
        this.teacherMapper = teacherMapper;
        this.groupMapper = groupMapper;
    }

    // Helper method to add teachers and group count to SubjectDto
    private SubjectDto enrichWithTeachers(Subject subject) {
        SubjectDto dto = subjectMapper.toDto(subject);
        
        // Add teachers
        if (subject.getTeachers() != null && !subject.getTeachers().isEmpty()) {
            List<TeacherDto> teacherDtos = subject.getTeachers().stream()
                .map(teacherMapper::toDto)
                .collect(Collectors.toList());
            dto.setTeachers(teacherDtos);
        }
        
        // Add REAL group count from database
        try {
            Long groupCount = subjectRepository.countGroupsBySubjectId(subject.getId());
            dto.setGroupCount(groupCount != null ? groupCount.intValue() : 0);
        } catch (Exception e) {
            // Fallback to loaded groups if query fails
            dto.setGroupCount(subject.getGroups() != null ? subject.getGroups().size() : 0);
        }
        
        return dto;
    }

    @Override
    @Transactional
    public SubjectDto create(SubjectDto dto) {
        if (dto.getSubjectCode() != null && subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new RuntimeException("Subject code already exists: " + dto.getSubjectCode());
        }

        Subject subject = subjectMapper.toEntity(dto);
        subject.setIsActive(true);
        Subject savedSubject = subjectRepository.save(subject);
        return subjectMapper.toDto(savedSubject);
    }

    @Override
    public SubjectDto findById(Long id) {
        Subject subject = subjectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));
        return enrichWithTeachers(subject);
    }

    @Override
    public List<SubjectDto> findAll() {
        return subjectRepository.findAll()
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SubjectDto update(Long id, SubjectDto dto) {
        Subject existingSubject = subjectRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + id));

        // Check subject code uniqueness (if changed)
        if (dto.getSubjectCode() != null &&
            !dto.getSubjectCode().equals(existingSubject.getSubjectCode()) &&
            subjectRepository.existsBySubjectCode(dto.getSubjectCode())) {
            throw new RuntimeException("Subject code already exists: " + dto.getSubjectCode());
        }

        // Update fields
        if (dto.getSubjectName() != null) {
            existingSubject.setSubjectName(dto.getSubjectName());
        }
        if (dto.getSubjectCode() != null) {
            existingSubject.setSubjectCode(dto.getSubjectCode());
        }
        if (dto.getDescription() != null) {
            existingSubject.setDescription(dto.getDescription());
        }
        if (dto.getCredits() != null) {
            existingSubject.setCredits(dto.getCredits());
        }
        if (dto.getHoursTotal() != null) {
            existingSubject.setHoursTotal(dto.getHoursTotal());
        }
        if (dto.getHoursLectures() != null) {
            existingSubject.setHoursLectures(dto.getHoursLectures());
        }
        if (dto.getHoursPractical() != null) {
            existingSubject.setHoursPractical(dto.getHoursPractical());
        }
        if (dto.getHoursLaboratory() != null) {
            existingSubject.setHoursLaboratory(dto.getHoursLaboratory());
        }
        if (dto.getSemester() != null) {
            existingSubject.setSemester(dto.getSemester());
        }
        if (dto.getAssessmentType() != null) {
            existingSubject.setAssessmentType(dto.getAssessmentType());
        }

        Subject savedSubject = subjectRepository.save(existingSubject);
        return subjectMapper.toDto(savedSubject);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!subjectRepository.existsById(id)) {
            throw new RuntimeException("Subject not found with id: " + id);
        }
        subjectRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return subjectRepository.existsById(id);
    }

    @Override
    public SubjectDto findBySubjectCode(String subjectCode) {
        Subject subject = subjectRepository.findBySubjectCode(subjectCode)
            .orElseThrow(() -> new RuntimeException("Subject not found with code: " + subjectCode));
        return subjectMapper.toDto(subject);
    }

    @Override
    public List<SubjectDto> findActiveSubjects() {
        return subjectRepository.findActiveSubjectsWithGroups()
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> searchByName(String name) {
        return subjectRepository.searchByName(name)
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> findByAssessmentType(AssessmentType assessmentType) {
        return subjectRepository.findByAssessmentTypeAndIsActiveTrueOrderBySubjectNameAsc(assessmentType)
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> findBySemester(Integer semester) {
        return subjectRepository.findBySemesterAndIsActiveTrueOrderBySubjectNameAsc(semester)
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> findByCredits(Integer credits) {
        return subjectRepository.findByCreditsAndIsActiveTrueOrderBySubjectNameAsc(credits)
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> findByTeacherId(Long teacherId) {
        return subjectRepository.findByTeacherIdWithGroups(teacherId)
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    public List<SubjectDto> findByGroupId(Long groupId) {
        return subjectRepository.findByGroupId(groupId)
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    public boolean existsBySubjectCode(String subjectCode) {
        return subjectRepository.existsBySubjectCode(subjectCode);
    }

    @Override
    public List<SubjectDto> findSubjectsWithGradesForStudent(Long studentId) {
        return subjectRepository.findSubjectsWithGradesForStudent(studentId)
            .stream()
            .map(this::enrichWithTeachers)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void assignTeacher(Long subjectId, Long teacherId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        if (!teacher.getIsActive()) {
            throw new RuntimeException("Cannot assign inactive teacher to subject");
        }

        subject.getTeachers().add(teacher);
        subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public void removeTeacher(Long subjectId, Long teacherId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        Teacher teacher = teacherRepository.findById(teacherId)
            .orElseThrow(() -> new RuntimeException("Teacher not found with id: " + teacherId));

        subject.getTeachers().remove(teacher);
        subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public SubjectDto activateSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        subject.setIsActive(true);
        Subject savedSubject = subjectRepository.save(subject);
        return subjectMapper.toDto(savedSubject);
    }

    @Override
    @Transactional
    public SubjectDto deactivateSubject(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));

        subject.setIsActive(false);
        Subject savedSubject = subjectRepository.save(subject);
        return subjectMapper.toDto(savedSubject);
    }

    @Override
    public List<Object> getAssignedGroups(Long subjectId) {
        try {
            Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
            
            return subject.getGroups().stream()
                .map(group -> {
                    var dto = groupMapper.toDto(group);
                    return (Object) dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> getAvailableGroups(Long subjectId) {
        try {
            Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
            
            // Get all groups except those already assigned to this subject
            List<StudentGroup> allGroups = groupRepository.findAll();
            Set<StudentGroup> assignedGroups = subject.getGroups();
            
            return allGroups.stream()
                .filter(group -> !assignedGroups.contains(group))
                .map(group -> {
                    var dto = groupMapper.toDto(group);
                    return (Object) dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    @Transactional
    public void addGroupToSubject(Long subjectId, Long groupId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        
        StudentGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        
        subject.getGroups().add(group);
        subjectRepository.save(subject);
    }

    @Override
    @Transactional
    public void removeGroupFromSubject(Long subjectId, Long groupId) {
        Subject subject = subjectRepository.findById(subjectId)
            .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
        
        StudentGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new RuntimeException("Group not found with id: " + groupId));
        
        subject.getGroups().remove(group);
        subjectRepository.save(subject);
    }

    @Override
    public List<Object> getAssignedTeachers(Long subjectId) {
        try {
            Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
            
            return subject.getTeachers().stream()
                .map(teacher -> {
                    var dto = teacherMapper.toDto(teacher);
                    return (Object) dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public List<Object> getAvailableTeachers(Long subjectId) {
        try {
            Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with id: " + subjectId));
            
            // Get all teachers except those already assigned
            List<Teacher> allTeachers = teacherRepository.findAll();
            List<Teacher> assignedTeachers = new ArrayList<>(subject.getTeachers());
            
            return allTeachers.stream()
                .filter(teacher -> !assignedTeachers.contains(teacher))
                .map(teacher -> {
                    var dto = teacherMapper.toDto(teacher);
                    return (Object) dto;
                })
                .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}
