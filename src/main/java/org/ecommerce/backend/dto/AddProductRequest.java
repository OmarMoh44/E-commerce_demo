package org.ecommerce.backend.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class AddProductRequest {
    @NotBlank(message = "Name must be not null")
    @Size(min = 5, message = "Name must be more than 4 character")
    private String name;

    @NotBlank(message = "Description must be not null")
    @Size(min = 10, message = "Description must be more than 9 character")
    private String description;

    @NotNull(message = "Price must be not null")
    @Positive(message = "Price must be positive")
    private Double price;

    @NotNull(message = "Stock quantity must be not null")
    @Positive(message = "Stock quantity must be positive")
    private Integer stockQuantity;

    @NotNull(message = "Category ID must be not null")
    @PositiveOrZero(message = "Category ID must be positive or zero")
    private Long categoryId;
}
