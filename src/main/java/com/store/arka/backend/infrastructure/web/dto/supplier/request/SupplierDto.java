package com.store.arka.backend.infrastructure.web.dto.supplier.request;

import com.store.arka.backend.domain.enums.Country;
import jakarta.validation.constraints.*;

public record SupplierDto(
    @NotBlank(message = "Commercial name is required")
    @Size(min = 1, max = 100, message = "Commercial name must be between 1 and 100 characters")
    String commercialName,
    @NotBlank(message = "Contact name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÑñ0-9 _-]+$",
        message = "Contact name can only contain letters (A-Z,a-z) including Ñ/ñ, numbers, single spaces, underscores or hyphens;" +
            " multiple consecutive spaces and accented letters are NOT allowed."
    )
    String contactName,
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Size(min = 10, max = 100, message = "Email must be between 10 and 100 characters")
    String email,
    @NotBlank(message = "Phone is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone must contain exactly 10 characters and numbers only"
    )
    String phone,
    @NotBlank(message = "Tax id is required")
    String taxId,
    @NotBlank(message = "Address is required")
    @Size(min = 10, max = 150, message = "Address must be between 10 and 150 characters")
    String address,
    @NotBlank(message = "City is required")
    @Size(min = 5, max = 100, message = "City must be between 5 and 100 characters")
    String city,
    @NotNull(message = "Country is required")
    Country country
) {
}
