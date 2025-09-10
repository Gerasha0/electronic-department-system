package com.kursova.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import java.util.Arrays;

/**
 * Security configuration for the Electronic Department System
 * 
 * CSRF Protection Analysis:
 * ========================
 * This application disables CSRF protection because:
 * 
 * 1. Stateless Architecture: Uses SessionCreationPolicy.STATELESS - no server-side sessions
 * 2. JWT Authentication: All authentication is done via JWT tokens in Authorization headers
 * 3. No Cookie-based Authentication: No session cookies that could be vulnerable to CSRF
 * 4. REST API Design: Primarily serves JSON APIs with @RestController annotations
 * 
 * When CSRF protection IS needed:
 * - Applications using session cookies for authentication
 * - Form-based authentication with server-side sessions
 * - Any stateful authentication mechanism
 * - Applications serving HTML forms that modify server state
 * 
 * When CSRF protection can be safely disabled:
 * - Stateless APIs using token-based authentication (like JWT)
 * - APIs that authenticate via Authorization headers
 * - Pure REST APIs without browser-based form submissions
 * - Applications where all clients are controlled (mobile apps, SPAs)
 * 
 * Additional Security Measures in place:
 * - CORS configuration with restricted origins (NOT using "*" wildcard)
 * - JWT token validation for all protected endpoints
 * - Role-based access control with @PreAuthorize annotations
 * - Security headers (HSTS, Content-Type Options)
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // Role constants to avoid duplication
    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_MANAGER = "MANAGER";
    private static final String ROLE_TEACHER = "TEACHER";
    private static final String ROLE_STUDENT = "STUDENT";
    private static final String ROLE_GUEST = "GUEST";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, com.kursova.config.jwt.JwtUtils jwtUtils, com.kursova.config.jwt.DbUserDetailsService userDetailsService) throws Exception {
        http
            // CSRF is disabled because:
            // 1. This is a stateless REST API using JWT tokens
            // 2. No session cookies are used (SessionCreationPolicy.STATELESS)
            // 3. Authentication is done via Authorization header, not cookies
            // 4. All state is maintained client-side in JWT tokens
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/api-docs/**").permitAll()
                .requestMatchers("/swagger-ui.html").permitAll()
                // Allow root, explicit HTML pages and static resources
                .requestMatchers("/", "/index.html", "/login.html", "/register.html", "/static/**", "/favicon.ico", "/*.css", "/*.js", "/*.html").permitAll()

                // Admin only endpoints
                .requestMatchers("/api/admin/**").hasRole(ROLE_ADMIN)

                // Manager endpoints
                .requestMatchers("/api/manager/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)

                // Teacher endpoints
                .requestMatchers("/api/teacher/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_TEACHER)

                // Grade endpoints - specific endpoints for different roles
                .requestMatchers("/api/grades/my-grades").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_TEACHER, ROLE_STUDENT)
                .requestMatchers("/api/grades/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_TEACHER)

                // User endpoints
                .requestMatchers("/api/users/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER)

                // Student endpoints
                .requestMatchers("/api/student/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_TEACHER, ROLE_STUDENT)

                // Guest endpoints - read-only access
                .requestMatchers("/api/departments/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_TEACHER, ROLE_STUDENT, ROLE_GUEST)
                .requestMatchers("/api/teachers/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_TEACHER, ROLE_STUDENT, ROLE_GUEST)
                .requestMatchers("/api/subjects/**").hasAnyRole(ROLE_ADMIN, ROLE_MANAGER, ROLE_TEACHER, ROLE_STUDENT, ROLE_GUEST)

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            // Configure security headers for additional protection
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable) // For H2 console
                .contentTypeOptions(Customizer.withDefaults())
                .httpStrictTransportSecurity(hsts -> hsts
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                ));

        // Add JWT filter before username/password filter
        http.addFilterBefore(new com.kursova.config.jwt.JwtAuthFilter(jwtUtils, userDetailsService), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // SECURITY: Restrict allowed origins - DO NOT use "*" with credentials
        // In production, replace with specific domain(s)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",  // React development server
            "http://localhost:8080",  // Spring Boot development server  
            "http://localhost:5173",  // Vite development server
            "http://127.0.0.1:3000",
            "http://127.0.0.1:8080",
            "http://127.0.0.1:5173"
            // Add your production domain here, e.g.:
            // "https://yourdomain.com"
        ));
        
        // Restrict HTTP methods to only what's needed
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // Be specific about allowed headers instead of using "*"
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept",
            "Origin",
            "X-Requested-With"
        ));
        
        // Only allow credentials for trusted origins
        configuration.setAllowCredentials(true);
        
        // Set max age for preflight requests (in seconds)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
