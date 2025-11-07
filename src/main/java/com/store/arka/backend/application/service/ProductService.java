package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.application.port.out.IProductAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService implements IProductUseCase {
  private final IProductAdapterPort productAdapterPort;
  private final ICategoryAdapterPort categoryAdapterPort;

  @Override
  @Transactional
  public Product createProduct(Product product) {
    ValidateAttributesUtils.throwIfModelNull(product, "Product");
    String normalizedSku = ValidateAttributesUtils.throwIfNullOrEmpty(product.getSku(), "SKU");
    if (productAdapterPort.existsProductBySku(normalizedSku)) {
      log.warn("SKU '{}' already exists", normalizedSku);
      throw new FieldAlreadyExistsException("SKU " + normalizedSku + " already exists. Choose a different SKU");
    }
    Product created = Product.create(
        normalizedSku,
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getStock()
    );
    Product saved = productAdapterPort.saveCreateProduct(created);
    log.info("Created new product {}, ID: {})", saved.getName(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Product getProductById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return productAdapterPort.findProductById(id)
        .orElseThrow(() -> {
          log.warn("Product with ID {} not found", id);
          return new ModelNotFoundException("Product with ID " + id + " not found");
        });
  }

  @Override
  @Transactional
  public Product getProductBySku(String sku) {
    String normalizedSku = ValidateAttributesUtils.throwIfNullOrEmpty(sku, "SKU");
    return productAdapterPort.findProductBySku(sku)
        .orElseThrow(() -> {
          log.warn("Product with SKU {} not found", normalizedSku);
          return new ModelNotFoundException("Product with SKU " + normalizedSku + " not found");
        });
  }

  @Override
  @Transactional
  public List<Product> getAllProducts() {
    log.info("Fetching all products");
    return productAdapterPort.findAllProducts();
  }

  @Override
  @Transactional
  public List<Product> getAllProductsByStatus(ProductStatus status) {
    log.info("Fetching all products with status {}", status);
    return productAdapterPort.findAllProductsByStatus(status);
  }

  @Override
  @Transactional
  public Product updateFieldsProduct(UUID id, Product product) {
    ValidateAttributesUtils.throwIfModelNull(product, "Product");
    Product found = getProductById(id);
    found.updateFields(product.getName(), product.getDescription(), product.getPrice());
    Product saved = productAdapterPort.saveUpdateProduct(found);
    log.info("Updated product ID {} ", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Product updateCategories(UUID id, Set<UUID> categories) {
    Product found = getProductById(id);
    if (found.isDeleted()) throw new ModelDeletionException("Product deleted previously");
    Set<Category> newCategories = new HashSet<>();
    categories.forEach(uuid -> {
      newCategories.add(categoryAdapterPort.findCategoryById(uuid)
          .filter(category -> category.getStatus().equals(CategoryStatus.ACTIVE))
          .orElseThrow(() -> {
            log.info("Category ID {} in product not found", uuid);
            return new ModelNotFoundException("Category with ID " + uuid + " not found");
          }));
    });
    found.updateCategories(newCategories);
    Product saved = productAdapterPort.saveUpdateProduct(found);
    log.info("Updated categories product ID {} ", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void decreaseStock(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.decreaseStock(quantity);
    productAdapterPort.saveUpdateProduct(found);
    log.info("Product ID {} has decreased its stock by {}", id, quantity);
  }

  @Override
  @Transactional
  public void increaseStock(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.increaseStock(quantity);
    productAdapterPort.saveUpdateProduct(found);
    log.info("Product ID {} has increased its stock by {}", id, quantity);
  }

  @Override
  @Transactional
  public void softDeleteProduct(UUID id) {
    Product found = getProductById(id);
    found.delete();
    productAdapterPort.saveUpdateProduct(found);
    log.info("Product ID {} marked as deleted", id);
  }

  @Override
  @Transactional
  public Product restoreProduct(UUID id) {
    Product found = getProductById(id);
    found.restore();
    Product restored = productAdapterPort.saveUpdateProduct(found);
    log.info("Product ID {} restored successfully", id);
    return restored;
  }

  @Override
  public void validateAvailabilityOrThrow(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.validateAvailability(quantity);
  }
}
