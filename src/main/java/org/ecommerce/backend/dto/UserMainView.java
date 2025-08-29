package org.ecommerce.backend.dto;

import lombok.*;
import org.ecommerce.backend.model.Role;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class UserMainView {
    private Long id;
    private String fullName;
    private String email;
    private Integer age;
    private Role role;
}
