package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.web.dto.product.request.CreateProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.UpdateFieldsProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToCartDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
@RequiredArgsConstructor
public class ProductDtoMapper {
  private final CategoryDtoMapper categoryMapper;

  public Product toDomain(CreateProductDto dto) {
    return new Product(
        null,
        dto.sku(),
        dto.name(),
        dto.description(),
        dto.price(),
        Collections.emptySet(),
        dto.stock(),
        null,
        null,
        null
    );
  }

  public Product toDomain(UpdateFieldsProductDto dto) {
    return new Product(
      null,
        null,
        dto.name(),
        dto.description(),
        dto.price(),
        null,
        null,
        null,
        null,
        null
    );
  }

  public ProductResponseDto toDto(Product domain) {
    return new ProductResponseDto(
        domain.getId(),
        domain.getSku(),
        domain.getName(),
        domain.getDescription(),
        domain.getPrice(),
        categoryMapper.toProductDto(domain.getCategories()),
        domain.getStock(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }

  public ProductResponseToCartDto toCartDto(Product domain) {
    return new ProductResponseToCartDto(
        domain.getId(),
        domain.getSku(),
        domain.getName(),
        domain.getDescription(),
        domain.getPrice()
    );
  }
}
