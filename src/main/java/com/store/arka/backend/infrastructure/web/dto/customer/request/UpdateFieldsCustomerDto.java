package com.store.arka.backend.infrastructure.web.dto.customer.request;

import jakarta.validation.constraints.*;

public record UpdateFieldsCustomerDto(
    @NotBlank(message = "first_name is required")
    @Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    @Pattern(
        regexp = "^(?!.* {2,})[A-Za-zÑñ0-9 _-]+$",
        message = "Name can only contain letters (A-Z,a-z) including Ñ/ñ, numbers, single spaces, underscores or hyphens;" +
            " multiple consecutive spaces and accented letters are NOT allowed."
    )
    String firstName,
    @NotBlank(message = "last_name is required")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    String lastName,
    @NotBlank(message = "email is required")
    @Email(message = "Email format is invalid")
    @Size(min = 10, max = 100, message = "Email must be between 10 and 100 characters")
    String email,
    @NotBlank(message = "phone is required")
    @Pattern(
        regexp = "^[0-9]{10}$",
        message = "Phone must contain exactly 10 characters and numbers only"
    )
    String phone,
    @NotBlank(message = "address is required")
    @Size(min = 10, max = 100, message = "Address must be between 10 and 100 characters")
    String address,
    @NotBlank(message = "city is required")
    @Size(min = 1, max = 50, message = "City must be between 5 and 50 characters")
    String city,
    @NotBlank(message = "country is required")
    String country
) {
}
