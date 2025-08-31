package org.ecommerce.backend.service;

import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.model.Category;
import org.ecommerce.backend.repository.CategoryRepository;
import org.ecommerce.backend.util.CategoryUtil;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryUtil categoryUtil;
    private final CategoryRepository categoryRepository;

    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void addCategory(String name) {
        categoryUtil.verifyCategoryNotExist(name);
        categoryRepository.save(Category.builder().name(name).build());
    }

    @Cacheable(value = "categories")
    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    @Cacheable(value = "category", key = "#id")
    public Category findCategory(Long id) {
        return categoryUtil.verifyCategoryExist(id);
    }

    @Transactional
    @Caching(
            evict = @CacheEvict(value = "categories", allEntries = true),
            put = @CachePut(value = "category", key = "#id")
    )
    public Category updateCategory(Long id, String name) {
        Category category = categoryUtil.verifyCategoryExist(id);
        categoryUtil.verifyCategoryNotExist(name);
        category.setName(name);
        return categoryRepository.save(category);
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "categories", allEntries = true),
                    @CacheEvict(value = "category", key = "#id"),
                    @CacheEvict(value = "products", allEntries = true),
                    @CacheEvict(value = "product", allEntries = true),
                    @CacheEvict(value = "searched-products", allEntries = true)
            }
    )
    public void deleteCategory(Long id) {
        Category category = categoryUtil.verifyCategoryExist(id);
        categoryRepository.delete(category);
    }
}
