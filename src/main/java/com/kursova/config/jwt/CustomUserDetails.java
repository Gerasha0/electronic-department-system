package com.kursova.config.jwt;

import com.kursova.dal.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom UserDetails implementation that includes teacher ID for method security expressions
 */
public class CustomUserDetails implements UserDetails {

    private final transient User user;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {
        this.user = user;
        this.authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword() != null ? user.getPassword() : "";
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getIsActive() != null && user.getIsActive();
    }

    // Custom methods for Spring Security expressions
    public Long getTeacherId() {
        return user.getTeacher() != null ? user.getTeacher().getId() : null;
    }

    public Long getStudentId() {
        return user.getStudent() != null ? user.getStudent().getId() : null;
    }

    public String getRole() {
        return user.getRole().name();
    }

    public User getUser() {
        return user;
    }
}
