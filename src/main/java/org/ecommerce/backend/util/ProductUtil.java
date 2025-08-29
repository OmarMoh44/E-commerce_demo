package org.ecommerce.backend.util;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.Product;
import org.ecommerce.backend.repository.ProductRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductUtil {
    private final ProductRepository productRepository;

    public Product verifyProductExist(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessage.PRODUCT_NOT_FOUND.getMessage())
        );
    }

    public void verifyProductNotExist(Long productId){
        productRepository.findById(productId).ifPresent(_ -> {
            throw new EntityExistsException(ErrorMessage.PRODUCT_ALREADY_EXISTS.getMessage());
        });
    }
}
