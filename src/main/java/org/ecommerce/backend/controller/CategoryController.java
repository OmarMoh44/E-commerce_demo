package org.ecommerce.backend.controller;

import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.model.Category;
import org.ecommerce.backend.service.CategoryService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public void addCategory(@RequestParam String name) {
        categoryService.addCategory(name);
    }

    @GetMapping
    public List<Category> findAllCategories(){
        return categoryService.findAllCategories();
    }

    @GetMapping("/{id}")
    public Category findCategory(@PathVariable Long id){
        return categoryService.findCategory(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public Category updateCategory(@PathVariable Long id, @RequestParam String name){
        return categoryService.updateCategory(id, name);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id){
        categoryService.deleteCategory(id);
    }
}
