package org.ecommerce.backend.config;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;

import org.ecommerce.backend.model.Category;
import org.ecommerce.backend.model.Product;
import org.ecommerce.backend.model.Role;
import org.ecommerce.backend.model.User;
import org.ecommerce.backend.repository.CategoryRepository;
import org.ecommerce.backend.repository.ProductRepository;
import org.ecommerce.backend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.*;

@Configuration
@RequiredArgsConstructor
public class DataSeeder {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedDatabase() {
        return _ -> {
            if (userRepository.count() > 0) {
                System.out.println("Database already seeded, skipping...");
                return;
            }

            Faker faker = new Faker(new Locale("en"));

            // Create 5 Categories
            List<String> categoryNames = Arrays.asList("Electronics", "Books", "Clothing", "Home & Kitchen", "Sports");
            List<Long> categoryIds = new ArrayList<>();
            for (String categoryName : categoryNames) {
                var category = categoryRepository.save(
                        Category.builder()
                                .name(categoryName)
                                .build()
                );
                categoryIds.add(category.getId());
            }

            // Create 5 Sellers
            for (int i = 0; i < 5; i++) {
                var seller = userRepository.save(
                        User.builder()
                                .fullName(faker.name().fullName())
                                .email(String.format("seller%d@gmail.com", i))
                                .password(passwordEncoder.encode("password"))
                                .role(Role.SELLER)
                                .dateOfBirth(LocalDate.now().minusYears(20 + faker.number().numberBetween(0, 15)))
                                .build()
                );

                // Create 5 Products for each Seller
                for (int j = 0; j < 5; j++) {
                    productRepository.save(
                            Product.builder()
                                    .name(faker.commerce().productName())
                                    .description(faker.lorem().sentence(10))
                                    .price(Double.parseDouble(faker.commerce().price()))
                                    .active(true)
                                    .stockQuantity(faker.number().numberBetween(10, 100))
                                    .category(categoryRepository.findById(
                                            categoryIds.get(faker.number().numberBetween(0, categoryIds.size()))
                                    ).orElseThrow())
                                    .seller(seller)
                                    .build()
                    );
                }
            }

            // Create 5 Buyers
            for (int i = 0; i < 5; i++) {
                userRepository.save(
                        User.builder()
                                .fullName(faker.name().fullName())
                                .email(String.format("buyer%d@gmail.com", i))
                                .password(passwordEncoder.encode("password"))
                                .role(Role.BUYER)
                                .dateOfBirth(LocalDate.now().minusYears(20 + faker.number().numberBetween(0, 15)))
                                .build()
                );
            }

            //create 5 Admins
            for (int i = 0; i < 5; i++) {
                userRepository.save(
                        User.builder()
                                .fullName(faker.name().fullName())
                                .email(String.format("admin%d@gmail.com", i))
                                .password(passwordEncoder.encode("password"))
                                .role(Role.ADMIN)
                                .dateOfBirth(LocalDate.now().minusYears(20 + faker.number().numberBetween(0, 15)))
                                .build()
                );
            }

            System.out.println("Seeded 5 categories, 5 sellers with 5 products each, 5 buyers, and 5 admins.");
        };
    }
}