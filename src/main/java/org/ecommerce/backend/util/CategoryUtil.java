package org.ecommerce.backend.util;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.Category;
import org.ecommerce.backend.repository.CategoryRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryUtil {
    private final CategoryRepository categoryRepository;

    public Category verifyCategoryExist(Long id){
        return categoryRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND.getMessage())
        );
    }

    public Category verifyCategoryExist(String name){
        return categoryRepository.findByNameIgnoreCase(name).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.CATEGORY_NOT_FOUND.getMessage())
        );
    }

    public void verifyCategoryNotExist(Long id){
        categoryRepository.findById(id).ifPresent(_ -> {
            throw new EntityNotFoundException(ErrorMessage.CATEGORY_ALREADY_EXISTS.getMessage());
        });
    }

    public void verifyCategoryNotExist(String name){
        categoryRepository.findByNameIgnoreCase(name).ifPresent(_ -> {
            throw new EntityNotFoundException(ErrorMessage.CATEGORY_ALREADY_EXISTS.getMessage());
        });
    }
}
