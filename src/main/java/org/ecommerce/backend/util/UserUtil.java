package org.ecommerce.backend.util;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.ProductRepository;
import org.ecommerce.backend.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void verifyUserNotExist(String email){
        userRepository.findByEmail(email).ifPresent(_ -> {
            throw new EntityExistsException(ErrorMessage.USER_ALREADY_EXISTS.getMessage());
        });
    }

    public void verifyUserNotExist(Long id){
        userRepository.findById(id).ifPresent(_ -> {
            throw new EntityExistsException(ErrorMessage.USER_ALREADY_EXISTS.getMessage());
        });
    }

    public User verifyUserExist(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new EntityExistsException(ErrorMessage.USER_NOT_FOUND.getMessage()));
    }

    public User verifyUserExist(Long id){
        return userRepository.findById(id).orElseThrow(() -> new EntityExistsException(ErrorMessage.USER_NOT_FOUND.getMessage()));
    }

    public void deactivateSellerProducts(List<Long> userIds) {
        productRepository.deactivateProducts(userIds);
    }
}
