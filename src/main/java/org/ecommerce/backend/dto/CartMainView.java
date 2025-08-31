package org.ecommerce.backend.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class CartMainView {
    private Long id;
    private UserMainView userMainView;
    private List<CartItemMainView> items;
}
