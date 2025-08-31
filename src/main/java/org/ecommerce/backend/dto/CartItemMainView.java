package org.ecommerce.backend.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class CartItemMainView {
    private Long id;
    private Integer quantity;
    private ProductMainView productMainView;
}
