package com.kursova.config.jwt;

import com.kursova.dal.entities.User;
import com.kursova.dal.entities.UserRole;
import com.kursova.dal.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for DbUserDetailsService
 * Tests user authentication and authorization functionality
 */
@DisplayName("Database User Details Service Tests")
class DbUserDetailsServiceTest {

    private DbUserDetailsService userDetailsService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        userDetailsService = new DbUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("Should load user by username successfully")
    void shouldLoadUserByUsernameSuccessfully() {
        // Given
        String username = "testuser";
        String password = "password123";
        User user = createTestUser(username, password, UserRole.STUDENT, true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(password);
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();

        assertThat(userDetails.getAuthorities()).hasSize(1);
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertThat(authority.getAuthority()).isEqualTo("ROLE_STUDENT");
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void shouldThrowExceptionWhenUserNotFound() {
        // Given
        String username = "nonexistent";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessage("User not found: " + username);
    }

    @Test
    @DisplayName("Should handle inactive user")
    void shouldHandleInactiveUser() {
        // Given
        String username = "inactiveuser";
        User user = createTestUser(username, "password", UserRole.TEACHER, false);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.isEnabled()).isFalse();
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    @ParameterizedTest
    @EnumSource(UserRole.class)
    @DisplayName("Should handle all user roles correctly")
    void shouldHandleAllUserRolesCorrectly(UserRole role) {
        // Given
        String username = "testuser";
        User user = createTestUser(username, "password", role, true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getAuthorities()).hasSize(1);
        GrantedAuthority authority = userDetails.getAuthorities().iterator().next();
        assertThat(authority.getAuthority()).isEqualTo("ROLE_" + role.name());
    }

    @Test
    @DisplayName("Should handle null isActive field as inactive")
    void shouldHandleNullIsActiveFieldAsInactive() {
        // Given
        String username = "nullactiveuser";
        User user = createTestUser(username, "password", UserRole.ADMIN, null);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.isEnabled()).isFalse();
    }

    @Test
    @DisplayName("Should handle user with empty password")
    void shouldHandleUserWithEmptyPassword() {
        // Given
        String username = "emptypassworduser";
        User user = createTestUser(username, "", UserRole.MANAGER, true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // Then
        assertThat(userDetails.getPassword()).isEmpty();
        assertThat(userDetails.getUsername()).isEqualTo(username);
    }

    private User createTestUser(String username, String password, UserRole role, Boolean isActive) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(username + "@test.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(role);
        user.setIsActive(isActive);
        return user;
    }
}
