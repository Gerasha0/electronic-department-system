package com.kursova.bll.services.impl;

import com.kursova.bll.dto.UserDto;
import com.kursova.bll.mappers.UserMapper;
import com.kursova.bll.services.UserService;
import com.kursova.dal.entities.Student;
import com.kursova.dal.entities.StudyForm;
import com.kursova.dal.entities.Teacher;
import com.kursova.dal.entities.User;
import com.kursova.dal.entities.UserRole;
import com.kursova.dal.uow.UnitOfWork;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of UserService
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UnitOfWork unitOfWork;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UnitOfWork unitOfWork, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.unitOfWork = unitOfWork;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto create(UserDto dto) {
        if (existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }
        if (existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        User entity = userMapper.toEntity(dto);
        entity = unitOfWork.getUserRepository().save(entity);

        // If the user is a STUDENT, create a corresponding Student entity
        if (entity.getRole() == UserRole.STUDENT) {
            createStudentEntity(entity);
        }

        return userMapper.toDto(entity);
    }

    @Override
    public UserDto createWithPassword(UserDto userDto, String password) {
        if (existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + userDto.getUsername());
        }
        if (existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + userDto.getEmail());
        }

        User entity = userMapper.toEntity(userDto);
        entity.setPassword(passwordEncoder.encode(password));
        entity = unitOfWork.getUserRepository().save(entity);

        // If the user is a STUDENT, create a corresponding Student entity
        if (entity.getRole() == UserRole.STUDENT) {
            createStudentEntity(entity);
        }

        // If the user is a TEACHER, create a corresponding Teacher entity
        if (entity.getRole() == UserRole.TEACHER) {
            createTeacherEntity(entity);
        }

        return userMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User entity = unitOfWork.getUserRepository().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
        return userMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        User entity = unitOfWork.getUserRepository().findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found with username: " + username));
        
        UserDto dto = userMapper.toDto(entity);
        
        // Set role-specific IDs
        if (entity.getRole() == UserRole.TEACHER) {
            unitOfWork.getTeacherRepository().findByUserId(entity.getId())
                    .ifPresent(teacher -> dto.setTeacherId(teacher.getId()));
        } else if (entity.getRole() == UserRole.STUDENT) {
            unitOfWork.getStudentRepository().findByUserId(entity.getId())
                    .ifPresent(student -> dto.setStudentId(student.getId()));
        }
        
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        User entity = unitOfWork.getUserRepository().findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
        return userMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        List<User> entities = unitOfWork.getUserRepository().findAll();
        return userMapper.toDtoList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findByRole(UserRole role) {
        List<User> entities = unitOfWork.getUserRepository().findByRole(role);
        return userMapper.toDtoList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findActiveByRole(UserRole role) {
        List<User> entities = unitOfWork.getUserRepository().findByRoleAndIsActiveTrue(role);
        return userMapper.toDtoList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> searchByName(String name) {
        List<User> entities = unitOfWork.getUserRepository().findByFullNameContainingIgnoreCase(name);
        return userMapper.toDtoList(entities);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findActiveUsers() {
        List<User> entities = unitOfWork.getUserRepository().findByIsActiveTrue();
        return userMapper.toDtoList(entities);
    }

    @Override
    public UserDto update(Long id, UserDto dto) {
        User existingEntity = unitOfWork.getUserRepository().findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Check if username or email is being changed and doesn't conflict
        if (!existingEntity.getUsername().equals(dto.getUsername()) && existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Username already exists: " + dto.getUsername());
        }
        if (!existingEntity.getEmail().equals(dto.getEmail()) && existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        userMapper.updateEntityFromDto(dto, existingEntity);
        existingEntity = unitOfWork.getUserRepository().save(existingEntity);
        return userMapper.toDto(existingEntity);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        User user = unitOfWork.getUserRepository().findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        unitOfWork.getUserRepository().save(user);
    }

    @Override
    public UserDto activateUser(Long userId) {
        User user = unitOfWork.getUserRepository().findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setIsActive(true);
        user = unitOfWork.getUserRepository().save(user);
        return userMapper.toDto(user);
    }

    @Override
    public UserDto deactivateUser(Long userId) {
        User user = unitOfWork.getUserRepository().findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setIsActive(false);
        user = unitOfWork.getUserRepository().save(user);
        return userMapper.toDto(user);
    }

    @Override
    public void delete(Long id) {
        if (!existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        unitOfWork.getUserRepository().deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return unitOfWork.getUserRepository().existsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return unitOfWork.getUserRepository().existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return unitOfWork.getUserRepository().existsByEmail(email);
    }

    /**
     * Creates a Student entity for a User with STUDENT role
     */
    private void createStudentEntity(User user) {
        String studentNumber = generateStudentNumber();
        Integer enrollmentYear = LocalDateTime.now().getYear();

        Student student = new Student(user, studentNumber, enrollmentYear, StudyForm.FULL_TIME);
        student.setIsActive(true);

        unitOfWork.getStudentRepository().save(student);
    }

    /**
     * Generates a unique student number
     */
    private String generateStudentNumber() {
        // Find the highest existing student number to generate next one
        List<Student> students = unitOfWork.getStudentRepository().findAll();
        int maxNumber = 0;

        for (Student student : students) {
            String number = student.getStudentNumber();
            if (number != null && number.length() >= 3) {
                try {
                    // Extract number part (last 3 digits)
                    String numberPart = number.substring(number.length() - 3);
                    int currentNumber = Integer.parseInt(numberPart);
                    maxNumber = Math.max(maxNumber, currentNumber);
                } catch (NumberFormatException e) {
                    // Ignore invalid numbers
                }
            }
        }

        // Generate next number
        int nextNumber = maxNumber + 1;
        int currentYear = LocalDateTime.now().getYear();
        String yearSuffix = String.valueOf(currentYear).substring(2); // Last 2 digits of year

        return String.format("БЗ%s%03d", yearSuffix, nextNumber);
    }

    /**
     * Creates a Teacher entity when a user with TEACHER role is created
     */
    private void createTeacherEntity(User user) {
        Teacher teacher = new Teacher();
        teacher.setUser(user);
        teacher.setIsActive(true);
        teacher.setHireDate(LocalDateTime.now());
        teacher.setDepartmentPosition("Викладач"); // Default position

        unitOfWork.getTeacherRepository().save(teacher);
    }
}
