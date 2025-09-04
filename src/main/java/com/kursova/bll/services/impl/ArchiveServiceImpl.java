package com.kursova.bll.services.impl;

import com.kursova.bll.services.ArchiveService;
import com.kursova.dal.entities.*;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of ArchiveService
 */
@Service
@Transactional
public class ArchiveServiceImpl implements ArchiveService {

    private final UnitOfWork unitOfWork;

    @Autowired
    public ArchiveServiceImpl(UnitOfWork unitOfWork) {
        this.unitOfWork = unitOfWork;
    }

    @Override
    @Transactional
    public void archiveGrade(Long gradeId, String archivedBy, String reason) {
        Grade grade = unitOfWork.getGradeRepository().findById(gradeId)
                .orElseThrow(() -> new RuntimeException("Grade not found with id: " + gradeId));
        
        archiveSpecificGrade(grade, archivedBy, reason);
    }

    @Override
    @Transactional
    public ArchivedGrade archiveSpecificGrade(Grade grade, String archivedBy, String reason) {
        ArchivedGrade archivedGrade = new ArchivedGrade(grade, archivedBy, reason);
        return unitOfWork.getArchivedGradeRepository().save(archivedGrade);
    }

    @Override
    @Transactional
    public void archiveStudentGroup(Long groupId, String archivedBy, String reason) {
        // Get the group to archive
        StudentGroup group = unitOfWork.getStudentGroupRepository().findById(groupId)
                .orElseThrow(() -> new RuntimeException("StudentGroup not found with id: " + groupId));

        // Get all students in this group
        List<Student> studentsInGroup = unitOfWork.getStudentRepository().findByGroupId(groupId);

        // Archive all students and their grades
        for (Student student : studentsInGroup) {
            archiveStudentInternal(student, archivedBy, reason + " (group deletion)");
        }

        // Archive the group itself
        ArchivedStudentGroup archivedGroup = new ArchivedStudentGroup(group, archivedBy, reason);
        unitOfWork.getArchivedStudentGroupRepository().save(archivedGroup);

        // Remove the original group
        unitOfWork.getStudentGroupRepository().delete(group);
    }

    @Override
    @Transactional
    public void archiveStudent(Long studentId, String archivedBy, String reason) {
        Student student = unitOfWork.getStudentRepository().findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found with id: " + studentId));

        archiveStudentInternal(student, archivedBy, reason);

        // Remove the original student
        unitOfWork.getStudentRepository().delete(student);
    }

    private void archiveStudentInternal(Student student, String archivedBy, String reason) {
        // Archive all grades for this student
        List<Grade> grades = unitOfWork.getGradeRepository().findByStudentIdOrderByGradeDateDesc(student.getId());
        for (Grade grade : grades) {
            ArchivedGrade archivedGrade = new ArchivedGrade(grade, archivedBy, reason);
            unitOfWork.getArchivedGradeRepository().save(archivedGrade);
            unitOfWork.getGradeRepository().delete(grade);
        }

        // Archive the student
        ArchivedStudent archivedStudent = new ArchivedStudent(student, archivedBy, reason);
        unitOfWork.getArchivedStudentRepository().save(archivedStudent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedStudentGroup> getAllArchivedGroups() {
        return unitOfWork.getArchivedStudentGroupRepository().findAllByOrderByArchivedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedStudent> getAllArchivedStudents() {
        return unitOfWork.getArchivedStudentRepository().findAllByOrderByArchivedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedGrade> getAllArchivedGrades() {
        return unitOfWork.getArchivedGradeRepository().findAllByOrderByArchivedAtDesc();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedStudentGroup> searchArchivedGroups(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllArchivedGroups();
        }

        List<ArchivedStudentGroup> groupsByCode = 
            unitOfWork.getArchivedStudentGroupRepository().findByGroupCodeContainingIgnoreCase(searchTerm);
        List<ArchivedStudentGroup> groupsByName = 
            unitOfWork.getArchivedStudentGroupRepository().findByGroupNameContainingIgnoreCase(searchTerm);

        // Merge and deduplicate results
        groupsByCode.addAll(groupsByName);
        return groupsByCode.stream().distinct().toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedStudent> searchArchivedStudents(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllArchivedStudents();
        }

        return unitOfWork.getArchivedStudentRepository().findByStudentNumberContainingIgnoreCase(searchTerm);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedStudent> getArchivedStudentsByGroupId(Long originalGroupId) {
        return unitOfWork.getArchivedStudentRepository().findByOriginalGroupId(originalGroupId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedGrade> getArchivedGradesByStudentId(Long originalStudentId) {
        return unitOfWork.getArchivedGradeRepository().findByOriginalStudentId(originalStudentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ArchivedStudentGroup> getArchivedGroupsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return unitOfWork.getArchivedStudentGroupRepository().findByArchivedAtBetween(startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public ArchiveStatistics getArchiveStatistics() {
        long totalGroups = unitOfWork.getArchivedStudentGroupRepository().count();
        long totalStudents = unitOfWork.getArchivedStudentRepository().count();
        long totalGrades = unitOfWork.getArchivedGradeRepository().count();

        // Get the most recent archive date
        LocalDateTime lastArchiveDate = null;
        List<ArchivedStudentGroup> recentGroups = unitOfWork.getArchivedStudentGroupRepository()
                .findAllByOrderByArchivedAtDesc();
        if (!recentGroups.isEmpty()) {
            lastArchiveDate = recentGroups.get(0).getArchivedAt();
        }

        return new ArchiveStatistics(totalGroups, totalStudents, totalGrades, lastArchiveDate);
    }

    @Override
    @Transactional
    public void deleteArchivedGroup(Long archivedGroupId) {
        if (!unitOfWork.getArchivedStudentGroupRepository().existsById(archivedGroupId)) {
            throw new RuntimeException("Archived group not found with id: " + archivedGroupId);
        }
        unitOfWork.getArchivedStudentGroupRepository().deleteById(archivedGroupId);
    }

    @Override
    @Transactional
    public void deleteArchivedStudent(Long archivedStudentId) {
        if (!unitOfWork.getArchivedStudentRepository().existsById(archivedStudentId)) {
            throw new RuntimeException("Archived student not found with id: " + archivedStudentId);
        }
        unitOfWork.getArchivedStudentRepository().deleteById(archivedStudentId);
    }

    @Override
    @Transactional
    public void deleteArchivedGrade(Long archivedGradeId) {
        if (!unitOfWork.getArchivedGradeRepository().existsById(archivedGradeId)) {
            throw new RuntimeException("Archived grade not found with id: " + archivedGradeId);
        }
        unitOfWork.getArchivedGradeRepository().deleteById(archivedGradeId);
    }
}
