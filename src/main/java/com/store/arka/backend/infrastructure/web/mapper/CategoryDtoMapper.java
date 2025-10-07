package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.infrastructure.web.dto.category.request.CreateCategoryDto;
import com.store.arka.backend.infrastructure.web.dto.category.request.UpdateCategoryDto;
import com.store.arka.backend.infrastructure.web.dto.category.response.CategoryResponseDto;
import com.store.arka.backend.infrastructure.web.dto.category.response.CategoryResponseToProductDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class CategoryDtoMapper {
  public Category toDomain(CreateCategoryDto dto) {
    return new Category(
        null,
        dto.name(),
        dto.description(),
        null,
        null,
        null
    );
  }

  public Category toDomain(UpdateCategoryDto dto) {
    return new Category(
        null,
        null,
        dto.description(),
        null,
        null,
        null
    );
  }

  public CategoryResponseDto toDto(Category domain) {
    return new CategoryResponseDto(
        domain.getId(),
        domain.getName(),
        domain.getDescription(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public List<CategoryResponseToProductDto> toProductDto(Set<Category> listDomain) {
    List<CategoryResponseToProductDto> listResponse = new ArrayList<>();
    listDomain.stream()
        .filter(domain -> domain.getStatus().equals(CategoryStatus.ACTIVE))
        .forEach(domain -> {
          listResponse.add(new CategoryResponseToProductDto(
              domain.getId(),
              domain.getName()
          ));
        });
    return listResponse;
  }
}
