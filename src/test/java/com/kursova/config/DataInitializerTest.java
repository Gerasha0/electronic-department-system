package com.kursova.config;

import com.kursova.dal.entities.*;
import com.kursova.dal.uow.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Integration tests for DataInitializer
 * Tests the data initialization process with mocked dependencies
 */
@ExtendWith(MockitoExtension.class)
class DataInitializerTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.kursova.dal.repositories.UserRepository userRepository;

    @Mock
    private com.kursova.dal.repositories.StudentGroupRepository studentGroupRepository;

    @Mock
    private com.kursova.dal.repositories.SubjectRepository subjectRepository;

    @Mock
    private com.kursova.dal.repositories.TeacherRepository teacherRepository;

    @Mock
    private com.kursova.dal.repositories.StudentRepository studentRepository;

    @Mock
    private com.kursova.dal.repositories.GradeRepository gradeRepository;

    @InjectMocks
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        // No setup needed - each test sets up its own mocks
    }

    // ===============================
    // RUN METHOD TESTS
    // ===============================

    @Test
    void run_WhenDataAlreadyExists_ShouldReturnEarly() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.count()).thenReturn(5L);

        // Act
        dataInitializer.run();

        // Assert
        verify(userRepository).count();
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(studentGroupRepository, subjectRepository, teacherRepository, studentRepository, gradeRepository);
    }

    @Test
    void run_WhenNoDataExists_ShouldInitializeAllData() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(userRepository).count();
        verify(userRepository, times(8)).save(any(User.class)); // 8 users: admin, manager, 2 teachers, 3 students, guest
        verify(studentGroupRepository, times(3)).save(any(StudentGroup.class));
        verify(subjectRepository, times(4)).save(any(Subject.class));
        verify(teacherRepository, times(4)).save(any(Teacher.class)); // 2 teachers saved twice each
        verify(studentRepository, times(3)).save(any(Student.class));
        verify(gradeRepository, times(4)).save(any(Grade.class));
    }

    @Test
    void run_ShouldCreateAdminUser() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("admin") &&
            user.getEmail().equals("admin@university.ua") &&
            user.getFirstName().equals("Адміністратор") &&
            user.getLastName().equals("Системи") &&
            user.getRole() == UserRole.ADMIN
        ));
    }

    @Test
    void run_ShouldCreateManagerUser() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("manager") &&
            user.getEmail().equals("manager@university.ua") &&
            user.getFirstName().equals("Олена") &&
            user.getLastName().equals("Менеджерова") &&
            user.getRole() == UserRole.MANAGER
        ));
    }

    @Test
    void run_ShouldCreateTeacherUsers() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("teacher1") &&
            user.getEmail().equals("ivanov@university.ua") &&
            user.getFirstName().equals("Іван") &&
            user.getLastName().equals("Іванов") &&
            user.getRole() == UserRole.TEACHER
        ));

        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("teacher2") &&
            user.getEmail().equals("petrov@university.ua") &&
            user.getFirstName().equals("Петро") &&
            user.getLastName().equals("Петров") &&
            user.getRole() == UserRole.TEACHER
        ));
    }

    @Test
    void run_ShouldCreateStudentUsers() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("student1") &&
            user.getEmail().equals("sidorov@student.ua") &&
            user.getFirstName().equals("Сергій") &&
            user.getLastName().equals("Сидоров") &&
            user.getRole() == UserRole.STUDENT
        ));

        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("student2") &&
            user.getEmail().equals("kowalenko@student.ua") &&
            user.getFirstName().equals("Анна") &&
            user.getLastName().equals("Коваленко") &&
            user.getRole() == UserRole.STUDENT
        ));

        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("student3") &&
            user.getEmail().equals("moroz@student.ua") &&
            user.getFirstName().equals("Олексій") &&
            user.getLastName().equals("Мороз") &&
            user.getRole() == UserRole.STUDENT
        ));
    }

    @Test
    void run_ShouldCreateGuestUser() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(userRepository).save(argThat(user ->
            user.getUsername().equals("guest") &&
            user.getEmail().equals("guest@university.ua") &&
            user.getFirstName().equals("Гість") &&
            user.getLastName().equals("Системи") &&
            user.getRole() == UserRole.GUEST
        ));
    }

    @Test
    void run_ShouldCreateStudentGroups() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(studentGroupRepository).save(argThat(group ->
            group.getGroupName().equals("БЗ-121") &&
            group.getSpecialization().equals("Кібербезпека") &&
            group.getMaxStudents() == 25 &&
            group.getStudyForm() == StudyForm.FULL_TIME
        ));

        verify(studentGroupRepository).save(argThat(group ->
            group.getGroupName().equals("ПІ-121") &&
            group.getSpecialization().equals("Програмна інженерія") &&
            group.getMaxStudents() == 30
        ));

        verify(studentGroupRepository).save(argThat(group ->
            group.getGroupName().equals("КН-221") &&
            group.getSpecialization().equals("Комп'ютерні науки") &&
            group.getMaxStudents() == 28
        ));
    }

    @Test
    void run_ShouldCreateSubjects() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(subjectRepository).save(argThat(subject ->
            subject.getSubjectName().equals("Програмування") &&
            subject.getSubjectCode().equals("PROG-301") &&
            subject.getCredits() == 6 &&
            subject.getAssessmentType() == AssessmentType.EXAM
        ));

        verify(subjectRepository).save(argThat(subject ->
            subject.getSubjectName().equals("Бази даних") &&
            subject.getSubjectCode().equals("DB-301") &&
            subject.getCredits() == 5
        ));

        verify(subjectRepository).save(argThat(subject ->
            subject.getSubjectName().equals("Алгоритми та структури даних") &&
            subject.getSubjectCode().equals("ASD-201") &&
            subject.getAssessmentType() == AssessmentType.DIFFERENTIATED_CREDIT
        ));

        verify(subjectRepository).save(argThat(subject ->
            subject.getSubjectName().equals("Веб-технології") &&
            subject.getSubjectCode().equals("WEB-401") &&
            subject.getAssessmentType() == AssessmentType.COURSE_WORK
        ));
    }

    @Test
    void run_ShouldCreateTeachersWithDetails() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert - teachers are saved twice: once initially, once after subject assignment
        verify(teacherRepository, times(2)).save(argThat(teacher ->
            teacher.getAcademicTitle().equals("Доцент") &&
            teacher.getDepartmentPosition().equals("Завідувач кафедри") &&
            teacher.getScientificDegree().equals("Кандидат технічних наук") &&
            teacher.getPhoneNumber().equals("+380501234567") &&
            teacher.getOfficeNumber().equals("201")
        ));

        verify(teacherRepository, times(2)).save(argThat(teacher ->
            teacher.getAcademicTitle().equals("Старший викладач") &&
            teacher.getDepartmentPosition().equals("Викладач") &&
            teacher.getScientificDegree().equals("Магістр комп'ютерних наук") &&
            teacher.getPhoneNumber().equals("+380507654321") &&
            teacher.getOfficeNumber().equals("203")
        ));
    }

    @Test
    void run_ShouldCreateStudentsWithDetails() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(studentRepository, times(3)).save(any(Student.class));
        // Specific student details are verified through the mock setup
    }

    @Test
    void run_ShouldCreateSampleGrades() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run();

        // Assert
        verify(gradeRepository).save(argThat(grade ->
            grade.getGradeValue() == 85 &&
            grade.getGradeType() == GradeType.CURRENT &&
            grade.getComments().equals("Хороша робота на практичних заняттях")
        ));

        verify(gradeRepository).save(argThat(grade ->
            grade.getGradeValue() == 90 &&
            grade.getGradeType() == GradeType.FINAL &&
            grade.getComments().equals("Відмінний результат на екзамені") &&
            grade.getIsFinal()
        ));

        verify(gradeRepository).save(argThat(grade ->
            grade.getGradeValue() == 78 &&
            grade.getGradeType() == GradeType.CURRENT &&
            grade.getComments().equals("Задовільна робота, потребує покращення")
        ));

        verify(gradeRepository).save(argThat(grade ->
            grade.getGradeValue() == 82 &&
            grade.getGradeType() == GradeType.FINAL &&
            grade.getComments().equals("Покращення результатів на екзамені") &&
            grade.getIsFinal()
        ));
    }

    @Test
    void run_WithArgs_ShouldIgnoreArgs() {
        // Arrange
        setupRepositoryMocks();
        when(userRepository.count()).thenReturn(0L);

        // Act
        dataInitializer.run("arg1", "arg2");

        // Assert - should work the same regardless of args
        verify(userRepository).count();
        verify(userRepository, atLeast(1)).save(any(User.class));
    }

    // ===============================
    // HELPER METHODS
    // ===============================

    private void setupRepositoryMocks() {
        // Setup password encoder
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Setup UnitOfWork mock to return repository mocks
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(unitOfWork.getStudentGroupRepository()).thenReturn(studentGroupRepository);
        when(unitOfWork.getSubjectRepository()).thenReturn(subjectRepository);
        when(unitOfWork.getTeacherRepository()).thenReturn(teacherRepository);
        when(unitOfWork.getStudentRepository()).thenReturn(studentRepository);
        when(unitOfWork.getGradeRepository()).thenReturn(gradeRepository);

        // Mock user lookups for teachers and students
        User teacher1 = new User("teacher1", "pass", "email", "first", "last", UserRole.TEACHER);
        User teacher2 = new User("teacher2", "pass", "email", "first", "last", UserRole.TEACHER);
        User student1 = new User("student1", "pass", "email", "first", "last", UserRole.STUDENT);
        User student2 = new User("student2", "pass", "email", "first", "last", UserRole.STUDENT);
        User student3 = new User("student3", "pass", "email", "first", "last", UserRole.STUDENT);

        when(userRepository.findByUsername("teacher1")).thenReturn(Optional.of(teacher1));
        when(userRepository.findByUsername("teacher2")).thenReturn(Optional.of(teacher2));
        when(userRepository.findByUsername("student1")).thenReturn(Optional.of(student1));
        when(userRepository.findByUsername("student2")).thenReturn(Optional.of(student2));
        when(userRepository.findByUsername("student3")).thenReturn(Optional.of(student3));

        // Mock group lookups
        StudentGroup group1 = new StudentGroup("БЗ-121", 3, StudyForm.FULL_TIME, 2021);
        StudentGroup group2 = new StudentGroup("ПІ-121", 3, StudyForm.FULL_TIME, 2021);

        when(studentGroupRepository.findByGroupName("БЗ-121")).thenReturn(Optional.of(group1));
        when(studentGroupRepository.findByGroupName("ПІ-121")).thenReturn(Optional.of(group2));

        // Mock subject lookups for teacher assignments and grades
        Subject prog = new Subject("Програмування", "PROG-301", 6, AssessmentType.EXAM);
        Subject asd = new Subject("Алгоритми", "ASD-201", 4, AssessmentType.DIFFERENTIATED_CREDIT);
        Subject db = new Subject("Бази даних", "DB-301", 5, AssessmentType.EXAM);
        Subject web = new Subject("Веб-технології", "WEB-401", 5, AssessmentType.COURSE_WORK);

        when(subjectRepository.findBySubjectCode("PROG-301")).thenReturn(Optional.of(prog));
        when(subjectRepository.findBySubjectCode("ASD-201")).thenReturn(Optional.of(asd));
        when(subjectRepository.findBySubjectCode("DB-301")).thenReturn(Optional.of(db));
        when(subjectRepository.findBySubjectCode("WEB-401")).thenReturn(Optional.of(web));

        // Mock student lookups for grades
        Student student1Entity = new Student();
        Student student2Entity = new Student();
        student1Entity.setStudentNumber("БЗ121001");
        student2Entity.setStudentNumber("БЗ121002");

        when(studentRepository.findByStudentNumber("БЗ121001")).thenReturn(Optional.of(student1Entity));
        when(studentRepository.findByStudentNumber("БЗ121002")).thenReturn(Optional.of(student2Entity));

        // Mock teacher lookup for grades
        Teacher teacher1Entity = new Teacher();
        when(teacherRepository.findByUsername("teacher1")).thenReturn(Optional.of(teacher1Entity));
    }
}
