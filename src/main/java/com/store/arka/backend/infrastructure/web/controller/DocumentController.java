package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.IDocumentUseCase;
import com.store.arka.backend.domain.enums.DocumentStatus;
import com.store.arka.backend.infrastructure.web.dto.document.request.DocumentRequestDto;
import com.store.arka.backend.infrastructure.web.dto.document.response.DocumentResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.DocumentDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

  @GetMapping("/id/{id}")
  public ResponseEntity<DocumentResponseDto> getDocumentById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(documentUseCase.getDocumentById(uuid)));
  }

  @GetMapping("/id/{id}/status/{status}")
  public ResponseEntity<DocumentResponseDto> getDocumentByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    DocumentStatus statusEnum = PathUtils.validateEnumOrThrow(DocumentStatus.class, status, "DocumentStatus");
    return ResponseEntity.ok(mapper.toDto(documentUseCase.getDocumentByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/number/{number}")
  public ResponseEntity<DocumentResponseDto> getDocumentByNumber(@PathVariable("number") String number) {
    return ResponseEntity.ok(mapper.toDto(documentUseCase.getDocumentByNumber(number)));
  }

  @GetMapping("/number/{number}/status/{status}")
  public ResponseEntity<DocumentResponseDto> getDocumentByNumberAndStatus(
      @PathVariable("number") String number,
      @PathVariable("status") String status) {
    DocumentStatus statusEnum = PathUtils.validateEnumOrThrow(DocumentStatus.class, status, "DocumentStatus");
    return ResponseEntity.ok(mapper.toDto(documentUseCase.getDocumentByNumberAndStatus(number, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<DocumentResponseDto>> getAllDocuments() {
    return ResponseEntity.ok(
        documentUseCase.getAllDocuments().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<DocumentResponseDto>> getAllDocumentsByStatus(@PathVariable("status") String status) {
    DocumentStatus statusEnum = PathUtils.validateEnumOrThrow(DocumentStatus.class, status, "DocumentStatus");
    return ResponseEntity.ok(
        documentUseCase.getAllDocumentsByStatus(statusEnum).stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/id/{id}")
  public ResponseEntity<DocumentResponseDto> putDocumentById(
      @PathVariable("id") String id,
      @RequestBody @Valid DocumentRequestDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(documentUseCase.updateDocument(uuid, mapper.toDomain(dto))));
  }
}
