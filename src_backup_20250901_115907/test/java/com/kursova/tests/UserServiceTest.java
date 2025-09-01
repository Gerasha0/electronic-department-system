package com.kursova.tests;

import com.kursova.bll.dto.UserDto;
import com.kursova.bll.mappers.UserMapper;
import com.kursova.bll.services.impl.UserServiceImpl;
import com.kursova.dal.entities.User;
import com.kursova.dal.entities.UserRole;
import com.kursova.dal.repositories.UserRepository;
import com.kursova.dal.uow.UnitOfWork;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("User Service Tests")
class UserServiceTest {

    @Mock
    private UnitOfWork unitOfWork;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        // Arrange - Setup test data
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashedpassword");
        testUser.setRole(UserRole.STUDENT);
        testUser.setIsActive(true);

        testUserDto = new UserDto();
        testUserDto.setId(1L);
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setRole(UserRole.STUDENT);
        testUserDto.setIsActive(true);
    }

    @Test
    @DisplayName("Should find user by ID successfully")
    void findById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.findById(1L);

        // Assert
        assertThat(result)
            .isNotNull()
            .extracting(UserDto::getId, UserDto::getUsername)
            .containsExactly(1L, "testuser");
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw exception when user not found by ID")
    void findById_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> userService.findById(999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found with id: 999");
    }

    @Test
    @DisplayName("Should find user by username successfully")
    void findByUsername_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.findByUsername("testuser");

        // Assert
        assertThat(result)
            .isNotNull()
            .extracting(UserDto::getUsername, UserDto::getEmail)
            .containsExactly("testuser", "test@example.com");
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    @DisplayName("Should find user by email successfully")
    void findByEmail_ShouldReturnUser_WhenUserExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.findByEmail("test@example.com");

        // Assert
        assertThat(result)
            .isNotNull()
            .extracting(UserDto::getEmail, UserDto::getUsername)
            .containsExactly("test@example.com", "testuser");
        verify(userRepository).findByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should find users by role")
    void findByRole_ShouldReturnUsers_WhenUsersExist() {
        // Arrange
        List<User> users = Arrays.asList(testUser);
        List<UserDto> userDtos = Arrays.asList(testUserDto);
        
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findByRole(UserRole.STUDENT)).thenReturn(users);
        when(userMapper.toDtoList(users)).thenReturn(userDtos);

        // Act
        List<UserDto> result = userService.findByRole(UserRole.STUDENT);

        // Assert
        assertThat(result)
            .isNotNull()
            .hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(UserRole.STUDENT);
        verify(userRepository).findByRole(UserRole.STUDENT);
    }

    @Test
    @DisplayName("Should create user successfully")
    void create_ShouldReturnCreatedUser_WhenValidData() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(userMapper.toEntity(testUserDto)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.create(testUserDto);

        // Assert
        assertThat(result)
            .isNotNull()
            .extracting(UserDto::getUsername, UserDto::getEmail)
            .containsExactly("testuser", "test@example.com");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing username")
    void create_ShouldThrowException_WhenUsernameExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.create(testUserDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username already exists: testuser");
    }

    @Test
    @DisplayName("Should throw exception when creating user with existing email")
    void create_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.create(testUserDto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email already exists: test@example.com");
    }

    @Test
    @DisplayName("Should create user with password successfully")
    void createWithPassword_ShouldReturnUser_WhenValidData() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.existsByUsername("testuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("hashedpassword");
        when(userMapper.toEntity(testUserDto)).thenReturn(testUser);
        when(userRepository.save(testUser)).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        // Act
        UserDto result = userService.createWithPassword(testUserDto, "password123");

        // Assert
        assertThat(result).isNotNull();
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should check if username exists")
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        // Act
        boolean result = userService.existsByUsername("testuser");

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByUsername("testuser");
    }

    @Test
    @DisplayName("Should check if username exists - not found")
    void existsByUsername_ShouldReturnFalse_WhenUsernameNotExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.existsByUsername("nonexistent")).thenReturn(false);

        // Act
        boolean result = userService.existsByUsername("nonexistent");

        // Assert
        assertThat(result).isFalse();
        verify(userRepository).existsByUsername("nonexistent");
    }

    @Test
    @DisplayName("Should check if email exists")
    void existsByEmail_ShouldReturnTrue_WhenEmailExists() {
        // Arrange
        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // Act
        boolean result = userService.existsByEmail("test@example.com");

        // Assert
        assertThat(result).isTrue();
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should activate user successfully")
    void activateUser_ShouldReturnActivatedUser_WhenUserExists() {
        // Arrange
        testUser.setIsActive(false);
        User activatedUser = new User();
        activatedUser.setId(1L);
        activatedUser.setUsername("testuser");
        activatedUser.setIsActive(true);
        
        UserDto activatedUserDto = new UserDto();
        activatedUserDto.setId(1L);
        activatedUserDto.setUsername("testuser");
        activatedUserDto.setIsActive(true);

        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(activatedUser);
        when(userMapper.toDto(activatedUser)).thenReturn(activatedUserDto);

        // Act
        UserDto result = userService.activateUser(1L);

        // Assert
        assertThat(result.getIsActive()).isTrue();
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void deactivateUser_ShouldReturnDeactivatedUser_WhenUserExists() {
        // Arrange
        User deactivatedUser = new User();
        deactivatedUser.setId(1L);
        deactivatedUser.setUsername("testuser");
        deactivatedUser.setIsActive(false);
        
        UserDto deactivatedUserDto = new UserDto();
        deactivatedUserDto.setId(1L);
        deactivatedUserDto.setUsername("testuser");
        deactivatedUserDto.setIsActive(false);

        when(unitOfWork.getUserRepository()).thenReturn(userRepository);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(deactivatedUser);
        when(userMapper.toDto(deactivatedUser)).thenReturn(deactivatedUserDto);

        // Act
        UserDto result = userService.deactivateUser(1L);

        // Assert
        assertThat(result.getIsActive()).isFalse();
        verify(userRepository).save(any(User.class));
    }
}