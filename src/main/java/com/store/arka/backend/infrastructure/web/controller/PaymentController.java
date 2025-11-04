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

  @PostMapping
  public ResponseEntity<PaymentResponseDto> postPayment(@RequestBody @Valid CreatePaymentDto request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(paymentUseCase.createPayment(request.orderId(), mapper.toDomain(request))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PaymentResponseDto> getPaymentById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.getPaymentById(uuid)));
  }

  @GetMapping("/order/{orderId}")
  public ResponseEntity<PaymentResponseDto> getPaymentByOrderId(@PathVariable("orderId") String orderId) {
    UUID orderUuid = PathUtils.validateAndParseUUID(orderId);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.getPaymentByOrderId(orderUuid)));
  }

  @GetMapping
  public ResponseEntity<List<PaymentResponseDto>> getAllPayments() {
    return ResponseEntity.ok(paymentUseCase.getAllPayments().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/method/{method}")
  public ResponseEntity<List<PaymentResponseDto>> getAllPaymentsByMethod(@PathVariable("method") String method) {
    PaymentMethod methodEnum = PathUtils.validateEnumOrThrow(PaymentMethod.class, method, "PaymentMethod");
    return ResponseEntity.ok(paymentUseCase.getAllPaymentsByMethod(methodEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<PaymentResponseDto>> getAllPaymentsByStatus(@PathVariable("status") String status) {
    PaymentStatus statusEnum = PathUtils.validateEnumOrThrow(PaymentStatus.class, status, "PaymentStatus");
    return ResponseEntity.ok(paymentUseCase.getAllPaymentsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/method/{method}/status/{status}")
  public ResponseEntity<List<PaymentResponseDto>> getAllPaymentsByMethodAndStatus(
      @PathVariable("method") String method,
      @PathVariable("status") String status) {
    PaymentMethod methodEnum = PathUtils.validateEnumOrThrow(PaymentMethod.class, method, "PaymentMethod");
    PaymentStatus statusEnum = PathUtils.validateEnumOrThrow(PaymentStatus.class, status, "PaymentStatus");
    return ResponseEntity.ok(paymentUseCase.getAllPaymentsByMethodAndStatus(methodEnum, statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/{id}/confirm")
  public ResponseEntity<PaymentResponseDto> confirmPaymentById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.confirmPaymentById(uuid)));
  }

  @PutMapping("/{id}/change-method")
  public ResponseEntity<PaymentResponseDto> changePaymentMethodById(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdatePaymentMethodDto methodDto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.changePaymentMethodById(uuid, methodDto.method())));
  }

  @PutMapping("/{id}/pay-again")
  public ResponseEntity<PaymentResponseDto> payAgainById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(paymentUseCase.payAgainById(uuid)));
  }

  @GetMapping("/order/{orderId}/exists")
  public ResponseEntity<Boolean> existsPaymentByOrderId(@PathVariable("orderId") String orderId) {
    UUID orderUuid = PathUtils.validateAndParseUUID(orderId);
    return ResponseEntity.ok(paymentUseCase.existsPaymentByOrderId(orderUuid));
  }
}
