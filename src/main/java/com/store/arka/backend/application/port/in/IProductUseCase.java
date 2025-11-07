package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.model.Product;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IProductUseCase {
  Product createProduct(Product product);

  Product getProductById(UUID id);

  Product getProductBySku(String sku);

  List<Product> getAllProducts();

  List<Product> getAllProductsByStatus(ProductStatus status);

  Product updateFieldsProduct(UUID id, Product product);

  Product updateCategories(UUID id, Set<UUID> categories);

  void decreaseStock(UUID id, Integer quantity);

  void increaseStock(UUID id, Integer quantity);

  void softDeleteProduct(UUID id);

  Product restoreProduct(UUID id);

  void validateAvailabilityOrThrow(UUID id, Integer quantity);
}
