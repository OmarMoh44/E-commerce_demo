package org.ecommerce.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.ecommerce.backend.validator.FieldsMatching;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@FieldsMatching(firstField = "password", secondField = "confirmPassword", message = "Passwords do not match")
public class ResetPasswordRequest {
    @NotBlank(message = "Verification code must be not null")
    @Size(min = 10, max = 10, message = "Verification code must be exactly 10 characters")
    private String verificationCode;

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
}
