package com.kursova.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for JwtUtils
 * Tests JWT token generation, parsing, and validation
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JWT Utils Tests")
class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String testSecret = "test-secret-key-for-jwt-utils-testing-that-is-long-enough-for-hmac-sha-256-algorithm-123456789";
    private final long testExpirationMs = 3600000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils(testSecret, testExpirationMs);
    }

    @Test
    @DisplayName("Should create JwtUtils with custom secret and expiration")
    void shouldCreateJwtUtilsWithCustomValues() {
        // Given
        String customSecret = "custom-test-secret-key-for-jwt-utils-testing-that-is-long-enough-for-hmac-sha-256-algorithm-456789123";
        long customExpiration = 7200000L; // 2 hours

        // When
        JwtUtils customJwtUtils = new JwtUtils(customSecret, customExpiration);

        // Then
        assertThat(customJwtUtils).isNotNull();
        // We can't directly test private fields, but we can test behavior
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void shouldGenerateValidJwtToken() {
        // Given
        String username = "testuser";
        String role = "ADMIN";

        // When
        String token = jwtUtils.generateToken(username, role);

        // Then
        assertThat(token)
                .isNotNull()
                .isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts separated by dots
    }

    @Test
    @DisplayName("Should parse valid JWT token and extract claims")
    void shouldParseValidJwtToken() {
        // Given
        String username = "testuser";
        String role = "ADMIN";
        String token = jwtUtils.generateToken(username, role);

        // When
        Claims claims = jwtUtils.parseToken(token);

        // Then
        assertThat(claims).isNotNull();
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role", String.class)).isEqualTo(role);
        assertThat(claims.getIssuedAt()).isNotNull();
        assertThat(claims.getExpiration()).isNotNull();
    }

    @Test
    @DisplayName("Should validate valid JWT token")
    void shouldValidateValidJwtToken() {
        // Given
        String username = "testuser";
        String role = "ADMIN";
        String token = jwtUtils.generateToken(username, role);

        // When
        boolean isValid = jwtUtils.validateToken(token);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate malformed JWT token")
    void shouldInvalidateMalformedJwtToken() {
        // Given
        String malformedToken = "malformed.jwt.token";

        // When
        boolean isValid = jwtUtils.validateToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should invalidate JWT token with wrong signature")
    void shouldInvalidateJwtTokenWithWrongSignature() {
        // Given - create token with different secret
        JwtUtils differentJwtUtils = new JwtUtils("different-secret-key-for-jwt-utils-testing-that-is-long-enough-for-hmac-sha-256-algorithm-9876543210", testExpirationMs);
        String token = differentJwtUtils.generateToken("testuser", "ADMIN");

        // When - try to validate with different secret
        boolean isValid = jwtUtils.validateToken(token);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should invalidate null token")
    void shouldInvalidateNullToken() {
        // When
        boolean isValid = jwtUtils.validateToken(null);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should invalidate empty token")
    void shouldInvalidateEmptyToken() {
        // When
        boolean isValid = jwtUtils.validateToken("");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should invalidate blank token")
    void shouldInvalidateBlankToken() {
        // When
        boolean isValid = jwtUtils.validateToken("   ");

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should throw exception when parsing malformed token")
    void shouldThrowExceptionWhenParsingMalformedToken() {
        // Given
        String malformedToken = "malformed.jwt.token";

        // When & Then
        assertThatThrownBy(() -> jwtUtils.parseToken(malformedToken))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Should throw exception when parsing token with wrong signature")
    void shouldThrowExceptionWhenParsingTokenWithWrongSignature() {
        // Given - create token with different secret
        JwtUtils differentJwtUtils = new JwtUtils("different-secret-key-for-jwt-utils-testing-that-is-long-enough-for-hmac-sha-256-algorithm-9876543210", testExpirationMs);
        String token = differentJwtUtils.generateToken("testuser", "ADMIN");

        // When & Then
        assertThatThrownBy(() -> jwtUtils.parseToken(token))
                .isInstanceOf(JwtException.class);
    }

    @Test
    @DisplayName("Should generate token with correct expiration time")
    void shouldGenerateTokenWithCorrectExpirationTime() {
        // Given
        String username = "testuser";
        String role = "ADMIN";

        // When
        String token = jwtUtils.generateToken(username, role);
        Claims claims = jwtUtils.parseToken(token);

        // Then
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();

        assertThat(issuedAt).isNotNull();
        assertThat(expiration).isNotNull();
        assertThat(expiration.getTime() - issuedAt.getTime()).isEqualTo(testExpirationMs);
    }

    @Test
    @DisplayName("Should handle token with null username")
    void shouldHandleTokenWithNullUsername() {
        // Given
        String username = null;
        String role = "GUEST";

        // When
        String token = jwtUtils.generateToken(username, role);
        Claims claims = jwtUtils.parseToken(token);

        // Then
        assertThat(claims.getSubject()).isNull();
        assertThat(claims.get("role", String.class)).isEqualTo(role);
    }

    @Test
    @DisplayName("Should handle token with null role")
    void shouldHandleTokenWithNullRole() {
        // Given
        String username = "testuser";
        String role = null;

        // When
        String token = jwtUtils.generateToken(username, role);
        Claims claims = jwtUtils.parseToken(token);

        // Then
        assertThat(claims.getSubject()).isEqualTo(username);
        assertThat(claims.get("role", String.class)).isNull();
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should handle token with empty strings")
    void shouldHandleTokenWithEmptyStrings() {
        // Given
        String username = "";
        String role = "";

        // When
        String token = jwtUtils.generateToken(username, role);
        Claims claims = jwtUtils.parseToken(token);

        // Then
        // Empty subject might be null or empty
        String subject = claims.getSubject();
        assertThat(subject == null || subject.isEmpty()).isTrue();
        // Empty string claims might not be included or might be null
        String retrievedRole = claims.get("role", String.class);
        if (retrievedRole != null) {
            assertThat(retrievedRole).isEmpty();
        }
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should generate different tokens for different inputs")
    void shouldGenerateDifferentTokensForDifferentInputs() {
        // Given
        String username1 = "testuser1";
        String username2 = "testuser2";
        String role = "ADMIN";

        // When
        String token1 = jwtUtils.generateToken(username1, role);
        String token2 = jwtUtils.generateToken(username2, role);

        // Then
        assertThat(token1).isNotEqualTo(token2);
        // But both should be valid
        assertThat(jwtUtils.validateToken(token1)).isTrue();
        assertThat(jwtUtils.validateToken(token2)).isTrue();
    }

    @Test
    @DisplayName("Should handle very long username and role")
    void shouldHandleVeryLongUsernameAndRole() {
        // Given
        String longUsername = "a".repeat(1000);
        String longRole = "b".repeat(500);

        // When
        String token = jwtUtils.generateToken(longUsername, longRole);
        Claims claims = jwtUtils.parseToken(token);

        // Then
        assertThat(claims.getSubject()).isEqualTo(longUsername);
        assertThat(claims.get("role", String.class)).isEqualTo(longRole);
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should handle special characters in username and role")
    void shouldHandleSpecialCharactersInUsernameAndRole() {
        // Given
        String specialUsername = "test@user.domain";
        String specialRole = "ROLE_ADMIN@#$%^&*()";

        // When
        String token = jwtUtils.generateToken(specialUsername, specialRole);
        Claims claims = jwtUtils.parseToken(token);

        // Then
        assertThat(claims.getSubject()).isEqualTo(specialUsername);
        assertThat(claims.get("role", String.class)).isEqualTo(specialRole);
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @Test
    @DisplayName("Should handle unicode characters in username and role")
    void shouldHandleUnicodeCharactersInUsernameAndRole() {
        // Given
        String unicodeUsername = "тестовий_користувач";
        String unicodeRole = "роль_адміністратора";

        // When
        String token = jwtUtils.generateToken(unicodeUsername, unicodeRole);
        Claims claims = jwtUtils.parseToken(token);

        // Then
        assertThat(claims.getSubject()).isEqualTo(unicodeUsername);
        assertThat(claims.get("role", String.class)).isEqualTo(unicodeRole);
        assertThat(jwtUtils.validateToken(token)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"   "})
    @DisplayName("Should invalidate invalid tokens")
    void shouldInvalidateInvalidTokens(String invalidToken) {
        // When
        boolean isValid = jwtUtils.validateToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }
}
