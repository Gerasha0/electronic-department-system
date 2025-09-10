package com.kursova.config.jwt;

import com.kursova.dal.entities.User;
import com.kursova.dal.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DbUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public DbUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Handle guest user specially - no database lookup needed
        if ("guest".equals(username)) {
            User guestUser = new User();
            guestUser.setUsername("guest");
            guestUser.setFirstName("Гість");
            guestUser.setLastName("Система");
            guestUser.setEmail("guest@example.com");
            guestUser.setRole(com.kursova.dal.entities.UserRole.GUEST);
            guestUser.setIsActive(true);
            return new CustomUserDetails(guestUser);
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new CustomUserDetails(user);
    }
}
