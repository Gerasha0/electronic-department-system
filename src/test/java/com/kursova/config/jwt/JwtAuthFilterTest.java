package com.kursova.config.jwt;
import java.util.Arrays;
import java.util.Collection;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private DbUserDetailsService userDetailsService;

    private JwtAuthFilter jwtAuthFilter;

    @BeforeEach
    void setUp() {
        jwtAuthFilter = new JwtAuthFilter(jwtUtils, userDetailsService);
        SecurityContextHolder.setContext(securityContext);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should pass filter chain when Authorization header is null or empty")
    void shouldPassFilterChainWhenAuthorizationHeaderIsNullOrEmpty(String headerValue) throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn(headerValue);

        // When
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateToken(anyString());
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
    }

    @Test
    @DisplayName("Should pass filter chain when Authorization header doesn't start with Bearer")
    void shouldPassFilterChainWhenAuthorizationHeaderDoesNotStartWithBearer() throws Exception {
        // Given
        when(request.getHeader("Authorization")).thenReturn("Basic token123");

        // When
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils, never()).validateToken(anyString());
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
    }

    @Test
    @DisplayName("Should pass filter chain when token is invalid")
    void shouldPassFilterChainWhenTokenIsInvalid() throws Exception {
        // Given
        String invalidToken = "invalid.jwt.token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + invalidToken);
        when(jwtUtils.validateToken(invalidToken)).thenReturn(false);

        // When
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils).validateToken(invalidToken);
        verify(jwtUtils, never()).parseToken(anyString());
        verify(securityContext, never()).setAuthentication(any(Authentication.class));
    }

    @Test
    @DisplayName("Should set authentication when token is valid")
    void shouldSetAuthenticationWhenTokenIsValid() throws Exception {
        // Given
        String validToken = "valid.jwt.token";
        String username = "testuser";
        String role = "ADMIN";

        Claims claims = mock(Claims.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtils.validateToken(validToken)).thenReturn(true);
        when(jwtUtils.parseToken(validToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((Collection) Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role)));

        // When
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(jwtUtils).validateToken(validToken);
        verify(jwtUtils).parseToken(validToken);
        verify(claims).getSubject();
        verify(userDetailsService).loadUserByUsername(username);
        verify(securityContext).setAuthentication(any(Authentication.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"USER", "TEACHER", "STUDENT", "ADMIN"})
    @NullAndEmptySource
    @DisplayName("Should handle all roles including null and empty correctly")
    void shouldHandleAllRolesIncludingNullAndEmptyCorrectly(String role) throws Exception {
        // Given
        String validToken = "valid.jwt.token";
        String username = "testuser";

        Claims claims = mock(Claims.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer " + validToken);
        when(jwtUtils.validateToken(validToken)).thenReturn(true);
        when(jwtUtils.parseToken(validToken)).thenReturn(claims);
        when(claims.getSubject()).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((Collection) Arrays.asList(new SimpleGrantedAuthority("ROLE_" + role)));

        // When
        jwtAuthFilter.doFilter(request, response, filterChain);

        // Then
        verify(filterChain).doFilter(request, response);
        verify(securityContext).setAuthentication(any(Authentication.class));
    }
}
