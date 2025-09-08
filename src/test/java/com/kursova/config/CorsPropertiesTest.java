package com.kursova.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CorsProperties configuration class
 * Tests all getters, setters, and default values
 */
class CorsPropertiesTest {

    private CorsProperties corsProperties;

    @BeforeEach
    void setUp() {
        corsProperties = new CorsProperties();
    }

    // ===============================
    // DEFAULT VALUES TESTS
    // ===============================

    @Test
    void testDefaultValues() {
        // Test default allowedOrigins (should be empty list)
        assertNotNull(corsProperties.getAllowedOrigins());
        assertTrue(corsProperties.getAllowedOrigins().isEmpty());

        // Test default allowedMethods (should be empty list)
        assertNotNull(corsProperties.getAllowedMethods());
        assertTrue(corsProperties.getAllowedMethods().isEmpty());

        // Test default allowedHeaders (should be empty list)
        assertNotNull(corsProperties.getAllowedHeaders());
        assertTrue(corsProperties.getAllowedHeaders().isEmpty());

        // Test default allowCredentials (should be true)
        assertTrue(corsProperties.isAllowCredentials());

        // Test default maxAge (should be 3600L)
        assertEquals(3600L, corsProperties.getMaxAge());
    }

    // ===============================
    // ALLOWED ORIGINS TESTS
    // ===============================

    @Test
    void testAllowedOrigins_GetterSetter() {
        List<String> origins = Arrays.asList("http://localhost:3000", "http://localhost:8080", "https://example.com");

        corsProperties.setAllowedOrigins(origins);
        assertEquals(origins, corsProperties.getAllowedOrigins());
        assertEquals(3, corsProperties.getAllowedOrigins().size());
    }

    @Test
    void testAllowedOrigins_EmptyList() {
        List<String> emptyList = new ArrayList<>();
        corsProperties.setAllowedOrigins(emptyList);
        assertEquals(emptyList, corsProperties.getAllowedOrigins());
        assertTrue(corsProperties.getAllowedOrigins().isEmpty());
    }

    @Test
    void testAllowedOrigins_Null() {
        corsProperties.setAllowedOrigins(null);
        assertNull(corsProperties.getAllowedOrigins());
    }

    // ===============================
    // ALLOWED METHODS TESTS
    // ===============================

    @Test
    void testAllowedMethods_GetterSetter() {
        List<String> methods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");

        corsProperties.setAllowedMethods(methods);
        assertEquals(methods, corsProperties.getAllowedMethods());
        assertEquals(5, corsProperties.getAllowedMethods().size());
    }

    @Test
    void testAllowedMethods_EmptyList() {
        List<String> emptyList = new ArrayList<>();
        corsProperties.setAllowedMethods(emptyList);
        assertEquals(emptyList, corsProperties.getAllowedMethods());
        assertTrue(corsProperties.getAllowedMethods().isEmpty());
    }

    @Test
    void testAllowedMethods_Null() {
        corsProperties.setAllowedMethods(null);
        assertNull(corsProperties.getAllowedMethods());
    }

    // ===============================
    // ALLOWED HEADERS TESTS
    // ===============================

    @Test
    void testAllowedHeaders_GetterSetter() {
        List<String> headers = Arrays.asList("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With");

        corsProperties.setAllowedHeaders(headers);
        assertEquals(headers, corsProperties.getAllowedHeaders());
        assertEquals(5, corsProperties.getAllowedHeaders().size());
    }

    @Test
    void testAllowedHeaders_EmptyList() {
        List<String> emptyList = new ArrayList<>();
        corsProperties.setAllowedHeaders(emptyList);
        assertEquals(emptyList, corsProperties.getAllowedHeaders());
        assertTrue(corsProperties.getAllowedHeaders().isEmpty());
    }

    @Test
    void testAllowedHeaders_Null() {
        corsProperties.setAllowedHeaders(null);
        assertNull(corsProperties.getAllowedHeaders());
    }

    // ===============================
    // ALLOW CREDENTIALS TESTS
    // ===============================

    @Test
    void testAllowCredentials_GetterSetter() {
        // Test setting to false
        corsProperties.setAllowCredentials(false);
        assertFalse(corsProperties.isAllowCredentials());

        // Test setting to true
        corsProperties.setAllowCredentials(true);
        assertTrue(corsProperties.isAllowCredentials());
    }

    // ===============================
    // MAX AGE TESTS
    // ===============================

    @Test
    void testMaxAge_GetterSetter() {
        // Test setting to different values
        corsProperties.setMaxAge(86400L);
        assertEquals(86400L, corsProperties.getMaxAge());

        corsProperties.setMaxAge(0L);
        assertEquals(0L, corsProperties.getMaxAge());

        corsProperties.setMaxAge(-1L);
        assertEquals(-1L, corsProperties.getMaxAge());
    }

    // ===============================
    // INTEGRATION TESTS
    // ===============================

    @Test
    void testCompleteConfiguration() {
        // Set up a complete CORS configuration
        List<String> origins = Arrays.asList("https://example.com", "https://www.example.com");
        List<String> methods = Arrays.asList("GET", "POST", "PUT", "DELETE");
        List<String> headers = Arrays.asList("Authorization", "Content-Type");

        corsProperties.setAllowedOrigins(origins);
        corsProperties.setAllowedMethods(methods);
        corsProperties.setAllowedHeaders(headers);
        corsProperties.setAllowCredentials(true);
        corsProperties.setMaxAge(86400L);

        // Verify all values
        assertEquals(origins, corsProperties.getAllowedOrigins());
        assertEquals(methods, corsProperties.getAllowedMethods());
        assertEquals(headers, corsProperties.getAllowedHeaders());
        assertTrue(corsProperties.isAllowCredentials());
        assertEquals(86400L, corsProperties.getMaxAge());
    }

    @Test
    void testDevelopmentConfiguration() {
        // Set up development-like configuration
        List<String> origins = Arrays.asList("http://localhost:3000", "http://127.0.0.1:3000");
        List<String> methods = Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS");
        List<String> headers = Arrays.asList("Authorization", "Content-Type", "Accept", "Origin");

        corsProperties.setAllowedOrigins(origins);
        corsProperties.setAllowedMethods(methods);
        corsProperties.setAllowedHeaders(headers);
        corsProperties.setAllowCredentials(true);
        corsProperties.setMaxAge(3600L);

        // Verify development configuration
        assertEquals(origins, corsProperties.getAllowedOrigins());
        assertEquals(methods, corsProperties.getAllowedMethods());
        assertEquals(headers, corsProperties.getAllowedHeaders());
        assertTrue(corsProperties.isAllowCredentials());
        assertEquals(3600L, corsProperties.getMaxAge());
    }
}
