package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IPaymentUseCase;
import com.store.arka.backend.domain.enums.PaymentMethod;
import com.store.arka.backend.domain.enums.PaymentStatus;
import com.store.arka.backend.infrastructure.web.dto.payment.request.CreatePaymentDto;
import com.store.arka.backend.infrastructure.web.dto.payment.request.UpdatePaymentMethodDto;
import com.store.arka.backend.infrastructure.web.dto.payment.response.PaymentResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.PaymentDtoMapper;
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
@RequestMapping("/api/payments")
public class PaymentController {
  private final IPaymentUseCase paymentUseCase;
  private final PaymentDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PostMapping
  public ResponseEntity<PaymentResponseDto> postPayment(@RequestBody @Valid CreatePaymentDto request) {
    UUID orderUuid = PathUtils.validateAndParseUUID(request.orderId());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(paymentUseCase.createPayment(orderUuid, mapper.toDomain(request))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.getPaymentByIdSecure(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'CUSTOMER')")
  @GetMapping("/order/{orderId}")
  public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(@PathVariable("orderId") String orderId) {
    UUID orderUuid = PathUtils.validateAndParseUUID(orderId);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.getPaymentByOrderId(orderUuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<PaymentResponseDto>> getAllPaymentsByFilters(
      @RequestParam(required = false) String method,
      @RequestParam(required = false) String status) {
    PaymentMethod methodEnum = null;
    PaymentStatus statusEnum = null;
    if (method != null) methodEnum = PathUtils.validateEnumOrThrow(PaymentMethod.class, method, "PaymentMethod");
    if (status != null) statusEnum = PathUtils.validateEnumOrThrow(PaymentStatus.class, status, "PaymentStatus");
    return ResponseEntity.ok(paymentUseCase.getAllPaymentsByFilters(methodEnum, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/confirm")
  public ResponseEntity<PaymentResponseDto> confirmPayment(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.confirmPayment(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/method")
  public ResponseEntity<PaymentResponseDto> updateMethod(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdatePaymentMethodDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.updateMethod(uuid, mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}/pay-again")
  public ResponseEntity<PaymentResponseDto> payAgain(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.payAgain(uuid)));
  }
}
