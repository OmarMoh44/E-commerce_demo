package org.ecommerce.backend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "users")
public class User extends BaseAudit implements UserDetails {
    @NotBlank(message = "Full name must be not null")
    @Size(min = 3, message = "Full name is too short")
    @Column(nullable = false)
    private String fullName;

    @NotBlank(message = "Must be not null")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
            message = "Invalid email format"
    )
    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @NotBlank(message = "Must be not null")
    @Size(min = 6, message = "Password must be more than 5 characters")
    @Column(nullable = false)
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Date of birth must be not null")
    @Column(nullable = false)
    private LocalDate dateOfBirth;

    @Transient
    private Integer age;

    @NotNull(message = "Role must be not null")
    @Enumerated(EnumType.STRING)
    @Column(updatable = false, nullable = false)
    private Role role;

    @Builder.Default
    @Column(columnDefinition = "boolean default false")
    private boolean isDeleted = false;

    @Builder.Default
    @Column(name = "delete_date", columnDefinition = "DATE default null")
    private LocalDate deletedDate = null;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Cart cart;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Product> sellProducts;

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return Collections.singleton(() -> String.format("ROLE_%s", getRole().name()));
    }

    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        LocalDate now = LocalDate.now();
        int age = now.getYear() - dateOfBirth.getYear();
        if (now.getDayOfYear() < dateOfBirth.getDayOfYear()) {
            age--;
        }
        return age;
    }
}
