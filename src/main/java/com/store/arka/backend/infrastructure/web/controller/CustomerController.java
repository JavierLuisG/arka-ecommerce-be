package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.ICustomerUseCase;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.customer.request.CreateCustomerDto;
import com.store.arka.backend.infrastructure.web.dto.customer.request.UpdateFieldsCustomerDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.CustomerResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.CustomerDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
  private final ICustomerUseCase customerUseCase;
  private final CustomerDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PostMapping
  public ResponseEntity<CustomerResponseDto> postCustomer(@RequestBody @Valid CreateCustomerDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(customerUseCase.createCustomer(mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(customerUseCase.getCustomerById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @GetMapping("/number/{number}")
  public ResponseEntity<CustomerResponseDto> getCustomerByNumber(@PathVariable("number") String number) {
    return ResponseEntity.ok(mapper.toDto(customerUseCase.getCustomerByDocumentNumber(number)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<CustomerResponseDto>> getAllCustomers(@RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(customerUseCase.getAllCustomers().stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    CustomerStatus statusEnum = PathUtils.validateEnumOrThrow(CustomerStatus.class, status, "CustomerStatus");
    return ResponseEntity.ok(customerUseCase.getAllCustomersByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponseDto> updateCustomer(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateFieldsCustomerDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(customerUseCase.updateFieldsCustomer(uuid, mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteCustomer(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    customerUseCase.softDeleteCustomer(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Customer with id " + id + " eliminated successfully"));
  }

  @PreAuthorize("hasAnyRole('ADMIN')")
  @PutMapping("/{id}/restore")
  public ResponseEntity<CustomerResponseDto> restoreCustomer(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(customerUseCase.restoreCustomer(uuid)));
  }
}
