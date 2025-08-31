package org.ecommerce.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// The used service in spring security
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository; // Your JPA/DB layer

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmailAndIsDeletedFalse(username)
                .orElseThrow(() -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getAuthorities() // Convert your DB roles to `GrantedAuthority`
        );
    }
}

