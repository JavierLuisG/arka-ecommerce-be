package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.infrastructure.web.dto.customer.request.DocumentRequestDto;
import com.store.arka.backend.infrastructure.web.dto.customer.response.DocumentResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.DocumentDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/documents")
public class DocumentController {
  private final IDocumentUseCase documentUseCase;
  private final DocumentDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @GetMapping("/{id}")
  public ResponseEntity<DocumentResponseDto> getDocumentById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(documentUseCase.getDocumentById(uuid)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @GetMapping("/number/{number}")
  public ResponseEntity<DocumentResponseDto> getDocumentByNumber(@PathVariable("number") String number) {
    return ResponseEntity.ok(mapper.toDto(documentUseCase.getDocumentByNumber(number)));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @GetMapping
  public ResponseEntity<List<DocumentResponseDto>> getAllDocuments(@RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(documentUseCase.getAllDocuments().stream().map(mapper::toDto).collect(Collectors.toList()));
    }
    DocumentStatus statusEnum = PathUtils.validateEnumOrThrow(DocumentStatus.class, status, "DocumentStatus");
    return ResponseEntity.ok(documentUseCase.getAllDocumentsByStatus(statusEnum)
        .stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
  @PutMapping("/{id}")
  public ResponseEntity<DocumentResponseDto> updateDocument(
      @PathVariable("id") String id,
      @RequestBody @Valid DocumentRequestDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(documentUseCase.updateDocument(uuid, mapper.toDomain(dto))));
  }
}
