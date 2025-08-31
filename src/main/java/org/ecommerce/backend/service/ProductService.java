package org.ecommerce.backend.service;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.AddProductRequest;
import org.ecommerce.backend.dto.ProductMainView;
import org.ecommerce.backend.exception.ErrorMessage;
import org.ecommerce.backend.model.Category;
import org.ecommerce.backend.model.Product;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.CategoryRepository;
import org.ecommerce.backend.repository.ProductRepository;
import org.ecommerce.backend.util.CategoryUtil;
import org.ecommerce.backend.util.ProductUtil;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryUtil categoryUtil;
    private final ProductUtil productUtil;
    private final ModelMapper modelMapper;
    private final Validator validator;

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = "searched-products", allEntries = true)
            }
    )
    public ProductMainView addProduct(AddProductRequest addProductRequest, User user) {
        Product product = modelMapper.map(addProductRequest, Product.class);
        product.setId(null);
        Category category = categoryUtil.verifyCategoryExist(addProductRequest.getCategoryId());
        product.setCategory(category);
        product.setSeller(user);
        return modelMapper.map(productRepository.save(product), ProductMainView.class);
    }

    @Cacheable(value = "products", key = "#page + '-' + #size")
    public List<ProductMainView> findAllProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("category.name", "name").ascending());
        return productRepository.findAllBy(pageable).getContent();
    }

    @Cacheable(value = "product", key = "#id")
    public ProductMainView findProduct(Long id) {
        Product product = productUtil.verifyProductExist(id);
        return modelMapper.map(product, ProductMainView.class);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = "searched-products", allEntries = true)
            },
            put = @CachePut(value = "product", key = "#productId")
    )
    public ProductMainView updateProduct(User user, Long productId, Map<String, Object> productUpdates) {
        Product product = productUtil.verifyProductExist(productId);
        if(!product.getSeller().getId().equals(user.getId())){
            throw new SecurityException(ErrorMessage.UNAUTHORIZED_ACCESS.getMessage());
        }
        productUpdates.forEach((key, value) -> {
            if (value == null) {
                return; // Skip null values
            }
            switch (key) {
                case "name" -> product.setName((String) value);
                case "description" -> product.setDescription((String) value);
                case "price" -> product.setPrice(Double.valueOf(value.toString()));
                case "stockQuantity" -> product.setStockQuantity(Integer.valueOf(value.toString()));
                case "active" -> product.setActive((Boolean) value);
                case "categoryId" -> {
                    Long categoryId = Long.valueOf(value.toString());
                    Category category = categoryUtil.verifyCategoryExist(categoryId);
                    product.setCategory(category);
                }
                default -> throw new IllegalArgumentException("Unknown field: " + key);
            }
        });
        var violations = validator.validate(product);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        return modelMapper.map(productRepository.save(product), ProductMainView.class);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = "searched-products", allEntries = true),
                    @CacheEvict(value = "product", key = "#productId"),
                    @CacheEvict(value = "cart-items", allEntries = true),
                    @CacheEvict(value = "cart-item", allEntries = true)
            }
    )
    public void deleteProduct(User user, Long productId){
        Product product = productUtil.verifyProductExist(productId);
        if(!product.getSeller().getId().equals(user.getId())){
            throw new SecurityException(ErrorMessage.UNAUTHORIZED_ACCESS.getMessage());
        }
        productRepository.deleteById(productId);
    }

    // Search products by product name, category id, price range.
    @Cacheable(value = "searched-products", key = "#page + '-' + #size + '-' + #searchParams")
    public List<ProductMainView> searchProducts(Map<String, Object> searchParams, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("category.name", "name").ascending());

        Double min = 0.0;
        if (searchParams.containsKey("minPrice") && searchParams.get("minPrice") != null 
                && !searchParams.get("minPrice").toString().trim().isEmpty()) {
            try {
                min = Double.parseDouble(searchParams.get("minPrice").toString());
                if (min < 0) {
                    throw new IllegalArgumentException("Minimum price cannot be negative");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid minimum price format");
            }
        }
        
        Double maxPrice = productRepository.maxPrice();
        Double max = (maxPrice != null ? maxPrice : Double.MAX_VALUE);
        if (searchParams.containsKey("maxPrice") && searchParams.get("maxPrice") != null 
                && !searchParams.get("maxPrice").toString().trim().isEmpty()) {
            try {
                max = Double.parseDouble(searchParams.get("maxPrice").toString());
                if (max < 0) {
                    throw new IllegalArgumentException("Maximum price cannot be negative");
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid maximum price format");
            }
        }
        
        if (min > max) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        
        List<Long> categoryIds = categoryRepository.findAllIds();
        if (searchParams.containsKey("categoryId") && searchParams.get("categoryId") != null 
                && !searchParams.get("categoryId").toString().trim().isEmpty()) {
            try {
                categoryIds = List.of(Long.parseLong(searchParams.get("categoryId").toString()));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid category ID format");
            }
        }
        
        String name = searchParams.containsKey("name") && searchParams.get("name") != null 
                && !searchParams.get("name").toString().trim().isEmpty() ?
                searchParams.get("name").toString() : "";
        
        return productRepository.searchProducts(name, categoryIds, min, max, pageable).getContent();
    }
}
