package com.kursova.bll.services;

import com.kursova.bll.dto.UserDto;
import com.kursova.dal.entities.UserRole;

import java.util.List;

/**
 * Service interface for User operations
 */
public interface UserService extends BaseService<UserDto, Long> {
    
    /**
     * Find user by username
     */
    UserDto findByUsername(String username);
    
    /**
     * Find user by email
     */
    UserDto findByEmail(String email);
    
    /**
     * Find users by role
     */
    List<UserDto> findByRole(UserRole role);
    
    /**
     * Find active users by role
     */
    List<UserDto> findActiveByRole(UserRole role);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Search users by name
     */
    List<UserDto> searchByName(String name);
    
    /**
     * Find active users
     */
    List<UserDto> findActiveUsers();
    
    /**
     * Create user with password
     */
    UserDto createWithPassword(UserDto userDto, String password);
    
    /**
     * Update user password
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * Activate user
     */
    UserDto activateUser(Long userId);
    
    /**
     * Deactivate user
     */
    UserDto deactivateUser(Long userId);
}
