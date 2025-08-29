package org.ecommerce.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.ecommerce.backend.dto.AddProductRequest;
import org.ecommerce.backend.dto.ProductMainView;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.service.ProductService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PreAuthorize("hasRole('SELLER')")
    @PostMapping
    public ProductMainView addProduct(@RequestBody @Valid AddProductRequest addProductRequest,
                                      @AuthenticationPrincipal User user) {
        return productService.addProduct(addProductRequest, user);
    }

    @GetMapping
    public List<ProductMainView> findAllProducts(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        return productService.findAllProducts(page, size);
    }

    @GetMapping("/{id}")
    public ProductMainView findProduct(@PathVariable Long id) {
        return productService.findProduct(id);
    }

    @PreAuthorize("hasRole('SELLER')")
    @PatchMapping("/{id}")
    public ProductMainView updateProduct(@AuthenticationPrincipal User user,
                                         @PathVariable Long id,
                                         @RequestBody Map<String, Object> updates) {
        return productService.updateProduct(user, id, updates);
    }

    @PreAuthorize("hasRole('SELLER')")
    @DeleteMapping("/{id}")
    public void deleteProduct(@AuthenticationPrincipal User user, @PathVariable Long id) {
        productService.deleteProduct(user, id);
    }

    @PostMapping("/search")
    public List<ProductMainView> searchProducts(@RequestBody Map<String, Object> searchParams,
                                                @RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "10") int size) {
        return productService.searchProducts(searchParams, page, size);
    }
}
