package org.ecommerce.backend.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProductMainView {
    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stockQuantity;
    private Boolean active;
    private String categoryName;
    private String sellerFullName;
    private String sellerEmail;
}
