package org.ecommerce.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.UserMainView;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.ProductRepository;
import org.ecommerce.backend.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final Validator validator;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;


    @Caching(
            evict = @CacheEvict(value = "users", allEntries = true),
            put = @CachePut(value = "user", key = "#user.id")
    )
    public UserMainView updateUser(User user, Map<String, Object> updates) {
        boolean passwordUpdate = updates.containsKey("password");
        updates.forEach((key, value) -> {
            switch (key) { // update only updated fields
                case "fullName" -> user.setFullName((String) value);
                case "age" -> user.setAge((Integer) value);
                case "password" -> user.setPassword((String) value);
                case "dateOfBirth" -> user.setDateOfBirth(objectMapper.convertValue(value, LocalDate.class));
            }
        });
        var violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        if(passwordUpdate) user.setPassword(passwordEncoder.encode(updates.get("password").toString()));
        return modelMapper.map(userRepository.save(user), UserMainView.class);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "user", key = "#user.id"),
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = "product", allEntries = true),
                    @CacheEvict(value = "searched-products", allEntries = true),
                    @CacheEvict(value = "cart-items", allEntries = true),
                    @CacheEvict(value = "cart-item", allEntries = true)
            }
    )
    public void deleteUser(User user) {
        user.setDeleted(true);
        user.setDeletedDate(LocalDate.now());
        userRepository.save(user);
        if(user.getRole().name().equals("SELLER")){
            productRepository.deactivateProducts(List.of(user.getId()));
        }
    }
}
