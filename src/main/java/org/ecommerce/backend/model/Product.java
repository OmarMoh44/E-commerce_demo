package org.ecommerce.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "products")
public class Product extends BaseAudit {
    @NotBlank(message = "Name must be not null")
    @Size(min = 5, message = "Name must be more than 4 character")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Description must be not null")
    @Size(min = 10, message = "Description must be more than 9 character")
    @Column(nullable = false, length = 1000)
    private String description;

    @NotNull(message = "Price must be not null")
    @Positive(message = "Price must be positive")
    @Column(nullable = false)
    private Double price;

    @NotNull(message = "Stock quantity must be not null")
    @Positive(message = "Stock quantity must be positive")
    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Builder.Default
    @Column(columnDefinition = "boolean default true")
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
}
