package org.ecommerce.backend.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.UserMainView;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.Role;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.ProductRepository;
import org.ecommerce.backend.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Cacheable(value = "users", key = "#page + '-' + #size")
    public List<UserMainView> findAllUsers(int page, int size) {
        // Validate pagination parameters
        if (page < 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PAGE_PARAMETERS.getMessage() + ": Page number cannot be negative");
        }
        if (size <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PAGE_PARAMETERS.getMessage() + ": Page size must be greater than 0");
        }
        if (size > 100) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_PAGE_PARAMETERS.getMessage() + ": Page size cannot exceed 100");
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("fullName"));
        return userRepository.findAllByRoleNot(Role.ADMIN, pageable).getContent().stream().map(
                this::mapToUserMainView
        ).toList();
    }

    @Cacheable(value = "user", key = "#id")
    public UserMainView getUserDetailsById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_REQUEST.getMessage() + ": User ID must be a positive number");
        }
        
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage())
        );
        
        if (Role.ADMIN.equals(user.getRole())) {
            throw new IllegalArgumentException(ErrorMessage.ADMIN_ACCESS_DENIED.getMessage());
        }
        
        if (user.isDeleted()) {
            throw new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage());
        }
        
        return mapToUserMainView(user);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "user", key = "#id"),
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = "product", allEntries = true),
                    @CacheEvict(value = "searched-products", allEntries = true),
                    @CacheEvict(value = "cart-items", allEntries = true),
                    @CacheEvict(value = "cart-item", allEntries = true)
            }
    )
    public void deleteUserById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ErrorMessage.INVALID_REQUEST.getMessage() + ": User ID must be a positive number");
        }
        
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.USER_NOT_FOUND.getMessage())
        );
        
        if (Role.ADMIN.equals(user.getRole())) {
            throw new IllegalArgumentException(ErrorMessage.ADMIN_DELETE_DENIED.getMessage());
        }
        
        if (user.isDeleted()) {
            throw new IllegalArgumentException(ErrorMessage.USER_ALREADY_DELETED.getMessage());
        }
        
        user.setDeleted(true);
        user.setDeletedDate(LocalDate.now());
        userRepository.save(user);
        if(user.getRole() == Role.SELLER) {
            productRepository.deactivateProducts(List.of(user.getId()));
        }
    }

    private UserMainView mapToUserMainView(User user) {
        if (user == null) {
            return null;
        }
        
        UserMainView userView = new UserMainView();
        userView.setId(user.getId());
        userView.setFullName(user.getFullName());
        userView.setEmail(user.getEmail());
        userView.setAge(user.getAge());
        userView.setRole(user.getRole());
        return userView;
    }
}
