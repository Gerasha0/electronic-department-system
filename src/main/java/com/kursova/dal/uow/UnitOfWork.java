package com.kursova.dal.uow;

import com.kursova.dal.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Unit of Work pattern implementation
 * Provides centralized access to all repositories and transaction management
 */
@Component
@Transactional
public class UnitOfWork {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final StudentGroupRepository studentGroupRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final ArchivedStudentGroupRepository archivedStudentGroupRepository;
    private final ArchivedStudentRepository archivedStudentRepository;
    private final ArchivedGradeRepository archivedGradeRepository;

    @Autowired
    public UnitOfWork(
            UserRepository userRepository,
            TeacherRepository teacherRepository,
            StudentRepository studentRepository,
            StudentGroupRepository studentGroupRepository,
            SubjectRepository subjectRepository,
            GradeRepository gradeRepository,
            ArchivedStudentGroupRepository archivedStudentGroupRepository,
            ArchivedStudentRepository archivedStudentRepository,
            ArchivedGradeRepository archivedGradeRepository) {
        this.userRepository = userRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.studentGroupRepository = studentGroupRepository;
        this.subjectRepository = subjectRepository;
        this.gradeRepository = gradeRepository;
        this.archivedStudentGroupRepository = archivedStudentGroupRepository;
        this.archivedStudentRepository = archivedStudentRepository;
        this.archivedGradeRepository = archivedGradeRepository;
    }

    // Repository getters
    public UserRepository getUserRepository() {
        return userRepository;
    }

    public TeacherRepository getTeacherRepository() {
        return teacherRepository;
    }

    public StudentRepository getStudentRepository() {
        return studentRepository;
    }

    public StudentGroupRepository getStudentGroupRepository() {
        return studentGroupRepository;
    }

    public SubjectRepository getSubjectRepository() {
        return subjectRepository;
    }

    public GradeRepository getGradeRepository() {
        return gradeRepository;
    }

    public ArchivedStudentGroupRepository getArchivedStudentGroupRepository() {
        return archivedStudentGroupRepository;
    }

    public ArchivedStudentRepository getArchivedStudentRepository() {
        return archivedStudentRepository;
    }

    public ArchivedGradeRepository getArchivedGradeRepository() {
        return archivedGradeRepository;
    }

    /**
     * Commit all pending changes
     * Spring automatically handles transaction commit/rollback
     */
    @Transactional
    public void commit() {
        // Spring's transaction management handles the actual commit
        // This method is here for explicit commit calls if needed
    }

    /**
     * Rollback current transaction
     * Spring automatically handles rollback on exceptions
     */
    @Transactional
    public void rollback() {
        // Spring's transaction management handles the actual rollback
        // This method is here for explicit rollback calls if needed
        throw new RuntimeException("Transaction rollback requested");
    }
}
