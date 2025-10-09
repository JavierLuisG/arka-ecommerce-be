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
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryController {
  public final ICategoryUseCase categoryUseCase;
  public final CategoryDtoMapper mapper;

  @PostMapping
  public ResponseEntity<CategoryResponseDto> postCategory(@RequestBody @Valid CreateCategoryDto dto) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(mapper.toDto(categoryUseCase.createCategory(mapper.toDomain(dto))));
  }

  @GetMapping("/id/{id}")
  public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.getCategoryById(uuid)));
  }

  @GetMapping("/id/{id}/status/{status}")
  public ResponseEntity<CategoryResponseDto> getCategoryByIdAndStatus(
      @PathVariable("id") String id,
      @PathVariable("status") String status) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    CategoryStatus statusEnum = PathUtils.validateEnumOrThrow(CategoryStatus.class, status, "CategoryStatus");
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.getCategoryByIdAndStatus(uuid, statusEnum)));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<CategoryResponseDto> getCategoryByName(@PathVariable("name") String name) {
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.getCategoryByName(name)));
  }

  @GetMapping("/name/{name}/status/{status}")
  public ResponseEntity<CategoryResponseDto> getCategoryByNameAndStatus(
      @PathVariable("name") String name,
      @PathVariable("status") String status) {
    CategoryStatus statusEnum = PathUtils.validateEnumOrThrow(CategoryStatus.class, status, "CategoryStatus");
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.getCategoryByNameAndStatus(name, statusEnum)));
  }

  @GetMapping
  public ResponseEntity<List<CategoryResponseDto>> getAllCategories() {
    return ResponseEntity.ok(
        categoryUseCase.getAllCategories().stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @GetMapping("/status/{status}")
  public ResponseEntity<List<CategoryResponseDto>> getAllCategoriesByStatus(@PathVariable("status") String status) {
    CategoryStatus statusEnum = PathUtils.validateEnumOrThrow(CategoryStatus.class, status, "CategoryStatus");
    return ResponseEntity.ok(
        categoryUseCase.getAllCategoriesByStatus(statusEnum).stream().map(mapper::toDto).collect(Collectors.toList()));
  }

  @PutMapping("/id/{id}")
  public ResponseEntity<CategoryResponseDto> putCategoryById(
      @PathVariable("id") String id,
      @RequestBody @Valid UpdateCategoryDto dto) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.updateFieldsCategory(uuid, mapper.toDomain(dto))));
  }

  @DeleteMapping("/id/{id}")
  public ResponseEntity<MessageResponseDto> deleteCategoryById(@PathVariable("id") String id) {
    UUID uuid = PathUtils.validateAndParseUUID(id);
    categoryUseCase.deleteCategoryById(uuid);
    return ResponseEntity.ok(new MessageResponseDto("Category with id " + id + " eliminated successfully"));
  }

  @PutMapping("/name/{name}/restore")
  public ResponseEntity<CategoryResponseDto> restoreCategoryByName(@PathVariable("name") String name) {
    return ResponseEntity.ok(mapper.toDto(categoryUseCase.restoreCategoryByName(name)));
  }
}
