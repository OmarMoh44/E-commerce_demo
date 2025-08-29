package org.ecommerce.backend.service;

import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.model.Category;
import org.ecommerce.backend.repository.CategoryRepository;
import org.ecommerce.backend.util.CategoryUtil;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryUtil categoryUtil;
    private final CategoryRepository categoryRepository;

    public void addCategory(String name) {
        categoryUtil.verifyCategoryNotExist(name);
        categoryRepository.save(Category.builder().name(name).build());
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public Category findCategory(Long id) {
        return categoryUtil.verifyCategoryExist(id);
    }

    public Category updateCategory(Long id, String name) {
        Category category = categoryUtil.verifyCategoryExist(id);
        categoryUtil.verifyCategoryNotExist(name);
        category.setName(name);
        return categoryRepository.save(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryUtil.verifyCategoryExist(id);
        categoryRepository.delete(category);
    }
}
