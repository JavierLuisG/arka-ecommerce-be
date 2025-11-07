package com.store.arka.backend.infrastructure.web.controller;

import com.store.arka.backend.application.port.in.ICategoryUseCase;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.infrastructure.web.dto.MessageResponseDto;
import com.store.arka.backend.infrastructure.web.dto.category.request.CreateCategoryDto;
import com.store.arka.backend.infrastructure.web.dto.category.request.UpdateCategoryDto;
import com.store.arka.backend.infrastructure.web.dto.category.response.CategoryResponseDto;
import com.store.arka.backend.infrastructure.web.mapper.CategoryDtoMapper;
import com.store.arka.backend.shared.util.PathUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
  public final ICategoryUseCase categoryUseCase;
  public final CategoryDtoMapper mapper;

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PostMapping
  public ResponseEntity<CategoryResponseDto> postCategory(@RequestBody @Valid CreateCategoryDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(categoryUseCase.createCategory(mapper.toDomain(dto))));
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.getCategoryById(uuid)));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<CategoryResponseDto> getCategoryByName(@PathVariable("name") String name) {
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.getCategoryByName(name)));
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponseDto>> getAllCategories(
      @RequestParam(required = false) String status) {
    if (status == null) {
      return ResponseEntity.ok(categoryUseCase.getAllCategories().stream().map(mapper::toDto).toList());
    }
    CategoryStatus statusEnum = PathUtils.validateEnumOrThrow(CategoryStatus.class, status, "CategoryStatus");
    return ResponseEntity.ok(categoryUseCase.getAllCategoriesByStatus(statusEnum).stream().map(mapper::toDto).toList());
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponseDto> updateFieldsCategory(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateCategoryDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.updateFieldsCategory(uuid, mapper.toDomain(dto))));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @DeleteMapping("/{id}")
  public ResponseEntity<MessageResponseDto> softDeleteCategory(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    categoryUseCase.softDeleteCategory(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Category deleted successfully"));
  }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PutMapping("/{id}/restore")
  public ResponseEntity<CategoryResponseDto> restoreCategory(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.restoreCategory(uuid)));
  }
}
