package org.ecommerce.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.ecommerce.backend.model.Role;
import org.ecommerce.backend.validator.FieldsMatching;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@FieldsMatching(firstField = "password", secondField = "confirmPassword", message = "Passwords do not match")
public class RegisterRequest {
    @NotBlank(message = "Full name must be not null")
    @Size(min = 3, message = "Full name is too short")
    private String fullName;

    @NotBlank(message = "Must be not null")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format"
    )
    private String email;

    @NotBlank(message = "Must be not null")
    @Size(min = 6, message = "Password must be more than 5 characters")
    private String password;

    @NotBlank(message = "Must be not null")
    @Size(min = 6, message = "Password must be more than 5 characters")
    private String confirmPassword;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth must be not null")
    private LocalDate dateOfBirth;

    @NotNull(message = "Role must be not null")
    private Role role;
}
