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

  @PostMapping
  public ResponseEntity<CustomerResponseDto> postCustomer(@RequestBody @Valid CreateCustomerDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(customerUseCase.createCustomer(mapper.toDomain(dto))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CustomerResponseDto> getCustomerById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(customerUseCase.getCustomerById(uuid)));
  }

  @GetMapping("/{id}/status/{status}")
  public ResponseEntity<CustomerResponseDto> getCustomerByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    CustomerStatus statusEnum = PathUtils.validateEnumOrThrow(CustomerStatus.class, status, "CustomerStatus");
    return ResponseEntity.ok(mapper.toDto(customerUseCase.getCustomerByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/number/{number}")
  public ResponseEntity<CustomerResponseDto> getCustomerByNumber(@PathVariable("number") String number) {
    return ResponseEntity.ok(mapper.toDto(customerUseCase.getCustomerByDocumentNumber(number)));
  }

  @GetMapping("/number/{number}/status/{status}")
  public ResponseEntity<CustomerResponseDto> getCustomerByNumberAndStatus(
      @PathVariable("number") String number,
      @PathVariable("status") String status) {
    CustomerStatus statusEnum = PathUtils.validateEnumOrThrow(CustomerStatus.class, status, "CustomerStatus");
    return ResponseEntity.ok(mapper.toDto(customerUseCase.getCustomerByDocumentNumberAndStatus(number, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<CustomerResponseDto>> getAllCustomers() {
    return ResponseEntity.ok(
        customerUseCase.getAllCustomers().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<CustomerResponseDto>> getAllCustomersByStatus(@PathVariable("status") String status) {
    CustomerStatus statusEnum = PathUtils.validateEnumOrThrow(CustomerStatus.class, status, "CustomerStatus");
    return ResponseEntity.ok(
        customerUseCase.getAllCustomersByStatus(statusEnum).stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{id}")
  public ResponseEntity<CustomerResponseDto> putCustomerById(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateFieldsCustomerDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(customerUseCase.updateFieldsCustomer(uuid, mapper.toDomain(dto))));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteCustomerById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    customerUseCase.deleteCustomerById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Customer with id " + id + " eliminated successfully"));
  }

  @PutMapping("/number/{number}/restore")
  public ResponseEntity<CustomerResponseDto> restoreCustomerByNumber(@PathVariable("number") String number) {
    return ResponseEntity.ok(mapper.toDto(customerUseCase.restoreCustomerByDocumentNumber(number)));
  }
}
