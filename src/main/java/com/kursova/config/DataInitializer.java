package com.kursova.config;

import com.kursova.dal.entities.*;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Data initializer to populate the database with sample data
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private final UnitOfWork unitOfWork;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public DataInitializer(UnitOfWork unitOfWork, PasswordEncoder passwordEncoder) {
        this.unitOfWork = unitOfWork;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (unitOfWork.getUserRepository().count() > 0) {
            return; // Data already initialized
        }
        
        initializeUsers();
        initializeStudentGroups();
        initializeSubjects();
        initializeTeachersAndStudents();
        initializeSampleGrades();
    }
    
    private void initializeUsers() {
        // Create admin user
        User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@university.ua", 
                             "Адміністратор", "Системи", UserRole.ADMIN);
        unitOfWork.getUserRepository().save(admin);
        
        // Create manager user
        User manager = new User("manager", passwordEncoder.encode("manager123"), "manager@university.ua", 
                               "Олена", "Менеджерова", UserRole.MANAGER);
        unitOfWork.getUserRepository().save(manager);
        
        // Create teacher users
        User teacher1 = new User("teacher1", passwordEncoder.encode("teacher123"), "ivanov@university.ua", 
                                "Іван", "Іванов", UserRole.TEACHER);
        unitOfWork.getUserRepository().save(teacher1);
        
        User teacher2 = new User("teacher2", passwordEncoder.encode("teacher123"), "petrov@university.ua", 
                                "Петро", "Петров", UserRole.TEACHER);
        unitOfWork.getUserRepository().save(teacher2);
        
        // Create student users
        User student1 = new User("student1", passwordEncoder.encode("student123"), "sidorov@student.ua", 
                                "Сергій", "Сидоров", UserRole.STUDENT);
        unitOfWork.getUserRepository().save(student1);
        
        User student2 = new User("student2", passwordEncoder.encode("student123"), "kowalenko@student.ua", 
                                "Анна", "Коваленко", UserRole.STUDENT);
        unitOfWork.getUserRepository().save(student2);
        
        User student3 = new User("student3", passwordEncoder.encode("student123"), "moroz@student.ua", 
                                "Олексій", "Мороз", UserRole.STUDENT);
        unitOfWork.getUserRepository().save(student3);
        
        // Create guest user
        User guest = new User("guest", passwordEncoder.encode("guest123"), "guest@university.ua", 
                             "Гість", "Системи", UserRole.GUEST);
        unitOfWork.getUserRepository().save(guest);
    }
    
    private void initializeStudentGroups() {
        StudentGroup group1 = new StudentGroup("БЗ-121", 3, StudyForm.FULL_TIME, 2021);
        group1.setSpecialization("Кібербезпека");
        group1.setMaxStudents(25);
        unitOfWork.getStudentGroupRepository().save(group1);
        
        StudentGroup group2 = new StudentGroup("ПІ-121", 3, StudyForm.FULL_TIME, 2021);
        group2.setSpecialization("Програмна інженерія");
        group2.setMaxStudents(30);
        unitOfWork.getStudentGroupRepository().save(group2);
        
        StudentGroup group3 = new StudentGroup("КН-221", 2, StudyForm.FULL_TIME, 2022);
        group3.setSpecialization("Комп'ютерні науки");
        group3.setMaxStudents(28);
        unitOfWork.getStudentGroupRepository().save(group3);
    }
    
    private void initializeSubjects() {
        Subject subject1 = new Subject("Програмування", "PROG-301", 6, AssessmentType.EXAM);
        subject1.setDescription("Основи програмування на мові Java");
        subject1.setHoursTotal(180);
        subject1.setHoursLectures(60);
        subject1.setHoursPractical(90);
        subject1.setHoursLaboratory(30);
        subject1.setSemester(5);
        unitOfWork.getSubjectRepository().save(subject1);
        
        Subject subject2 = new Subject("Бази даних", "DB-301", 5, AssessmentType.EXAM);
        subject2.setDescription("Проектування та розробка баз даних");
        subject2.setHoursTotal(150);
        subject2.setHoursLectures(45);
        subject2.setHoursPractical(75);
        subject2.setHoursLaboratory(30);
        subject2.setSemester(5);
        unitOfWork.getSubjectRepository().save(subject2);
        
        Subject subject3 = new Subject("Алгоритми та структури даних", "ASD-201", 4, AssessmentType.DIFFERENTIATED_CREDIT);
        subject3.setDescription("Вивчення основних алгоритмів та структур даних");
        subject3.setHoursTotal(120);
        subject3.setHoursLectures(40);
        subject3.setHoursPractical(50);
        subject3.setHoursLaboratory(30);
        subject3.setSemester(3);
        unitOfWork.getSubjectRepository().save(subject3);
        
        Subject subject4 = new Subject("Веб-технології", "WEB-401", 5, AssessmentType.COURSE_WORK);
        subject4.setDescription("Розробка веб-додатків з використанням сучасних технологій");
        subject4.setHoursTotal(150);
        subject4.setHoursLectures(30);
        subject4.setHoursPractical(90);
        subject4.setHoursLaboratory(30);
        subject4.setSemester(7);
        unitOfWork.getSubjectRepository().save(subject4);
    }
    
    private void initializeTeachersAndStudents() {
        // Create teachers
        User teacherUser1 = unitOfWork.getUserRepository().findByUsername("teacher1").orElse(null);
        if (teacherUser1 != null) {
            Teacher teacher1 = new Teacher(teacherUser1, "Доцент", "Завідувач кафедри");
            teacher1.setScientificDegree("Кандидат технічних наук");
            teacher1.setPhoneNumber("+380501234567");
            teacher1.setOfficeNumber("201");
            teacher1.setBiography("Досвідчений викладач з 15-річним стажем роботи у сфері IT");
            unitOfWork.getTeacherRepository().save(teacher1);
            
            // Assign subjects to teacher1
            Subject prog = unitOfWork.getSubjectRepository().findBySubjectCode("PROG-301").orElse(null);
            Subject asd = unitOfWork.getSubjectRepository().findBySubjectCode("ASD-201").orElse(null);
            if (prog != null) {
                teacher1.getSubjects().add(prog);
                prog.getTeachers().add(teacher1);
            }
            if (asd != null) {
                teacher1.getSubjects().add(asd);
                asd.getTeachers().add(teacher1);
            }
            unitOfWork.getTeacherRepository().save(teacher1);
        }
        
        User teacherUser2 = unitOfWork.getUserRepository().findByUsername("teacher2").orElse(null);
        if (teacherUser2 != null) {
            Teacher teacher2 = new Teacher(teacherUser2, "Старший викладач", "Викладач");
            teacher2.setScientificDegree("Магістр комп'ютерних наук");
            teacher2.setPhoneNumber("+380507654321");
            teacher2.setOfficeNumber("203");
            teacher2.setBiography("Молодий перспективний викладач, спеціаліст з баз даних");
            unitOfWork.getTeacherRepository().save(teacher2);
            
            // Assign subjects to teacher2
            Subject db = unitOfWork.getSubjectRepository().findBySubjectCode("DB-301").orElse(null);
            Subject web = unitOfWork.getSubjectRepository().findBySubjectCode("WEB-401").orElse(null);
            if (db != null) {
                teacher2.getSubjects().add(db);
                db.getTeachers().add(teacher2);
            }
            if (web != null) {
                teacher2.getSubjects().add(web);
                web.getTeachers().add(teacher2);
            }
            unitOfWork.getTeacherRepository().save(teacher2);
        }
        
        // Create students
        StudentGroup group1 = unitOfWork.getStudentGroupRepository().findByGroupName("БЗ-121").orElse(null);
        StudentGroup group2 = unitOfWork.getStudentGroupRepository().findByGroupName("ПІ-121").orElse(null);
        
        User studentUser1 = unitOfWork.getUserRepository().findByUsername("student1").orElse(null);
        if (studentUser1 != null && group1 != null) {
            Student student1 = new Student(studentUser1, "БЗ121001", 2021, StudyForm.FULL_TIME);
            student1.setGroup(group1);
            student1.setPhoneNumber("+380631234567");
            student1.setAddress("м. Київ, вул. Студентська, 15");
            unitOfWork.getStudentRepository().save(student1);
        }
        
        User studentUser2 = unitOfWork.getUserRepository().findByUsername("student2").orElse(null);
        if (studentUser2 != null && group1 != null) {
            Student student2 = new Student(studentUser2, "БЗ121002", 2021, StudyForm.FULL_TIME);
            student2.setGroup(group1);
            student2.setPhoneNumber("+380637654321");
            student2.setAddress("м. Київ, вул. Університетська, 22");
            unitOfWork.getStudentRepository().save(student2);
        }
        
        User studentUser3 = unitOfWork.getUserRepository().findByUsername("student3").orElse(null);
        if (studentUser3 != null && group2 != null) {
            Student student3 = new Student(studentUser3, "ПІ121001", 2021, StudyForm.FULL_TIME);
            student3.setGroup(group2);
            student3.setPhoneNumber("+380639876543");
            student3.setAddress("м. Київ, вул. Молодіжна, 8");
            unitOfWork.getStudentRepository().save(student3);
        }
    }
    
    private void initializeSampleGrades() {
        // Add some sample grades
        Student student1 = unitOfWork.getStudentRepository().findByStudentNumber("БЗ121001").orElse(null);
        Student student2 = unitOfWork.getStudentRepository().findByStudentNumber("БЗ121002").orElse(null);
        Teacher teacher1 = unitOfWork.getTeacherRepository().findByUsername("teacher1").orElse(null);
        Subject progSubject = unitOfWork.getSubjectRepository().findBySubjectCode("PROG-301").orElse(null);
        
        if (student1 != null && teacher1 != null && progSubject != null) {
            Grade grade1 = new Grade(student1, teacher1, progSubject, 85, GradeType.CURRENT);
            grade1.setComments("Хороша робота на практичних заняттях");
            unitOfWork.getGradeRepository().save(grade1);
            
            Grade grade2 = new Grade(student1, teacher1, progSubject, 90, GradeType.FINAL);
            grade2.setComments("Відмінний результат на екзамені");
            grade2.setIsFinal(true);
            unitOfWork.getGradeRepository().save(grade2);
        }
        
        if (student2 != null && teacher1 != null && progSubject != null) {
            Grade grade3 = new Grade(student2, teacher1, progSubject, 78, GradeType.CURRENT);
            grade3.setComments("Задовільна робота, потребує покращення");
            unitOfWork.getGradeRepository().save(grade3);
            
            Grade grade4 = new Grade(student2, teacher1, progSubject, 82, GradeType.FINAL);
            grade4.setComments("Покращення результатів на екзамені");
            grade4.setIsFinal(true);
            unitOfWork.getGradeRepository().save(grade4);
        }
    }
}
