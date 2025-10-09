package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.model.Product;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface IProductUseCase {
  Product createProduct(Product product);

  Product getProductById(UUID id);

  Product getProductByIdAndStatus(UUID id, ProductStatus status);

  Product getProductBySku(String sku);

  Product getProductBySkuAndStatus(String sku, ProductStatus status);

  List<Product> getAllProducts();

  List<Product> getAllProductsByStatus(ProductStatus status);

  Product updateFieldsProduct(UUID id, Product product);

  Product updateCategories(UUID id, Set<UUID> categories);

  void decreaseStock(UUID id, Integer quantity);

  void increaseStock(UUID id, Integer quantity);

  void deleteProductById(UUID id);

  Product restoreProductBySku(String sku);
}
