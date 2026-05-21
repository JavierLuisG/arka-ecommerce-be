package com.store.arka.backend.infrastructure.web.dto.supplier.request;

import jakarta.validation.constraints.*;

public record SupplierDto(
    @NotBlank(message = "commercial_name is required")
    @Size(min = 1, max = 100, message = "Commercial name must be between 1 and 100 characters")
    String commercialName,
    @NotBlank(message = "contact_name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÑñ0-9 _-]+$",
        message = "Contact name can only contain letters (A-Z,a-z) including Ñ/ñ, numbers, single spaces, underscores or hyphens;" +
            " multiple consecutive spaces and accented letters are NOT allowed."
    )
    String contactName,
    @NotBlank(message = "email is required")
    @Email(message = "Email should be valid")
    @Size(min = 10, max = 100, message = "Email must be between 10 and 100 characters")
    String email,
    @NotBlank(message = "phone is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone must contain exactly 10 characters and numbers only"
    )
    String phone,
    @NotBlank(message = "tax_id is required")
    String taxId,
    @NotBlank(message = "address is required")
    @Size(min = 10, max = 150, message = "Address must be between 10 and 150 characters")
    String address,
    @NotBlank(message = "city is required")
    @Size(min = 1, max = 100, message = "City must be between 5 and 100 characters")
    String city,
    @NotBlank(message = "country is required")
    String country
) {
}
