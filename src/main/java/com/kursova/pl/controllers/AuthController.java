package com.kursova.pl.controllers;

import com.kursova.bll.dto.UserDto;
import com.kursova.bll.services.UserService;
import com.kursova.config.jwt.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Authentication
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Operations for user authentication")
public class AuthController {
    
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    
    public AuthController(UserService userService, JwtUtils jwtUtils, AuthenticationManager authenticationManager) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
    }
    
    /**
     * Login request DTO
     */
    public static class LoginRequest {
        private String username;
        private String password;
        
        // Getters and Setters
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }
    
    /**
     * Login response DTO
     */
    public static class LoginResponse {
        private boolean success;
        private String message;
        private UserDto user;
        
        public LoginResponse(boolean success, String message, UserDto user) {
            this.success = success;
            this.message = message;
            this.user = user;
        }
        
        // Getters and Setters
        public boolean isSuccess() {
            return success;
        }
        
        public void setSuccess(boolean success) {
            this.success = success;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
        
        public UserDto getUser() {
            return user;
        }
        
        public void setUser(UserDto user) {
            this.user = user;
        }
    }
    
    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user with username and password")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate using AuthenticationManager (will check password)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            UserDto user = userService.findByUsername(loginRequest.getUsername());
            if (user == null || !user.getIsActive()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new LoginResponse(false, "Invalid credentials or user is not active", null));
            }

            String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());

            // Return token in message field and user info
            LoginResponse resp = new LoginResponse(true, token, user);
            return ResponseEntity.ok(resp);

        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, "Invalid credentials", null));
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register user", description = "Registers a new user with password")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto, @RequestParam String password) {
        if (userService.existsByUsername(userDto.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username exists");
        }

        UserDto created = userService.createWithPassword(userDto, password);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logs out the current user")
    public ResponseEntity<Map<String, String>> logout() {
        SecurityContextHolder.clearContext();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/current-user")
    @Operation(summary = "Get current user", description = "Gets information about the currently authenticated user")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getName())) {
            
            try {
                UserDto user = userService.findByUsername(authentication.getName());
                return ResponseEntity.ok(user);
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }
        
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
