package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.infrastructure.web.dto.product.request.CreateProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.request.UpdateFieldsProductDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToOrderDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToPurchaseDto;
import com.store.arka.backend.infrastructure.web.dto.product.response.ProductResponseToSupplierDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductDtoMapper {
  private final CategoryDtoMapper categoryMapper;

  public Product toDomain(CreateProductDto dto) {
    if (dto == null) return null;
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
    if (dto == null) return null;
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
    if (domain == null) return null;
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

  public ProductResponseToOrderDto toOrderDto(Product domain) {
    if (domain == null) return null;
    return new ProductResponseToOrderDto(
        domain.getId(),
        domain.getSku(),
        domain.getName(),
        domain.getDescription(),
        domain.getPrice()
    );
  }

  public ProductResponseToPurchaseDto toPurchaseDto(Product domain) {
    if (domain == null) return null;
    return new ProductResponseToPurchaseDto(
        domain.getId(),
        domain.getSku(),
        domain.getName(),
        domain.getDescription(),
        domain.getPrice()
    );
  }

  public List<ProductResponseToSupplierDto> toSupplierDto(List<Product> listDomain) {
    if (listDomain == null) return Collections.emptyList();
    List<ProductResponseToSupplierDto> dtoList = new ArrayList<>();
    listDomain.forEach(domain -> {
      dtoList.add(new ProductResponseToSupplierDto(
          domain.getId(),
          domain.getSku(),
          domain.getName()
      ));
    });
    return dtoList;
  }
}
