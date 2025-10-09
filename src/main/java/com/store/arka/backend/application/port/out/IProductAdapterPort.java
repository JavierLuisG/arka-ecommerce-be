package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.model.Product;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductAdapterPort {
  Product saveCreateProduct(Product product);

  Product saveUpdateProduct(Product product);

  Optional<Product> findProductById(UUID id);

  Optional<Product> findProductByIdAndStatus(UUID id, ProductStatus status);

  Optional<Product> findProductBySku(String sku);

  Optional<Product> findProductBySkuAndStatus(String sku, ProductStatus status);

  List<Product> findAllProducts();

  List<Product> findAllProductsByStatus(ProductStatus status);

  boolean existsProductBySku(String sku);
}
