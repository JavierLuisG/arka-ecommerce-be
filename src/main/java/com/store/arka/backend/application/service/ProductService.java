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
    if (product == null) throw new ModelNullException("Product cannot be null");
    String normalizedSku = product.getSku().trim();
    String normalizedName = product.getName().trim().toLowerCase();
    String normalizedDescription = product.getDescription().trim();
    List<String> forbiddenNames = List.of("null", "default", "admin");
    if (forbiddenNames.contains(normalizedName)) {
      throw new InvalidArgumentException("This product name is not allowed");
    }
    if (productAdapterPort.existsProductBySku(product.getSku())) {
      throw new FieldAlreadyExistsException("SKU " + product.getSku() + " already exists");
    }
    Product created = Product.create(
        normalizedSku,
        normalizedName,
        normalizedDescription,
        product.getPrice(),
        product.getStock()
    );
    return productAdapterPort.saveCreateProduct(created);
  }

  @Override
  @Transactional
  public Product getProductById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return productAdapterPort.findProductById(id)
        .orElseThrow(() -> new ModelNotFoundException("Product with id " + id + " not found"));
  }

  @Override
  @Transactional
  public Product getProductByIdAndStatus(UUID id, ProductStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return productAdapterPort.findProductByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Product with id " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public Product getProductBySku(String sku) {
    if (sku == null || sku.isBlank()) throw new InvalidArgumentException("SKU is required");
    return productAdapterPort.findProductBySku(sku)
        .orElseThrow(() -> new ModelNotFoundException("Product with SKU " + sku + " not found"));
  }

  @Override
  @Transactional
  public Product getProductBySkuAndStatus(String sku, ProductStatus status) {
    if (sku == null || sku.isBlank()) throw new InvalidArgumentException("SKU is required");
    return productAdapterPort.findProductBySkuAndStatus(sku, status)
        .orElseThrow(() -> new ModelNotFoundException("Product with SKU " + sku + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public List<Product> getAllProducts() {
    return productAdapterPort.findAllProducts();
  }

  @Override
  @Transactional
  public List<Product> getAllProductsByStatus(ProductStatus status) {
    return productAdapterPort.findAllProductsByStatus(status);
  }

  @Override
  @Transactional
  public Product updateFieldsProduct(UUID id, Product product) {
    if (product == null) throw new ModelNullException("Product cannot be null");
    String normalizedName = product.getName().trim().toLowerCase();
    String normalizedDescription = product.getDescription().trim();
    List<String> forbiddenNames = List.of("null", "default", "admin");
    if (forbiddenNames.contains(normalizedName)) {
      throw new InvalidArgumentException("This product name is not allowed");
    }
    Product found = getProductByIdAndStatus(id, ProductStatus.ACTIVE);
    found.updateFields(normalizedName, normalizedDescription, product.getPrice());
    return productAdapterPort.saveUpdateProduct(found);
  }

  @Override
  @Transactional
  public Product updateCategories(UUID id, Set<UUID> categories) {
    Product found = getProductByIdAndStatus(id, ProductStatus.ACTIVE);
    Set<Category> newCategories = new HashSet<>();
    categories.forEach(uuid -> {
      newCategories.add(categoryAdapterPort.findCategoryById(uuid)
          .filter(category -> category.getStatus().equals(CategoryStatus.ACTIVE))
          .orElseThrow(() -> new ModelNotFoundException("Category with id " + uuid + " not found")));
    });
    found.updateCategories(newCategories);
    return productAdapterPort.saveUpdateProduct(found);
  }

  @Override
  @Transactional
  public void decreaseStock(UUID id, Integer quantity) {
    Product found = getProductByIdAndStatus(id, ProductStatus.ACTIVE);
    found.decreaseStock(quantity);
    productAdapterPort.saveUpdateProduct(found);
  }

  @Override
  @Transactional
  public void increaseStock(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.increaseStock(quantity);
    productAdapterPort.saveUpdateProduct(found);
  }

  @Override
  @Transactional
  public void deleteProductById(UUID id) {
    Product found = getProductByIdAndStatus(id, ProductStatus.ACTIVE);
    found.delete();
    productAdapterPort.saveUpdateProduct(found);
  }

  @Override
  @Transactional
  public Product restoreProductBySku(String sku) {
    Product found = getProductBySku(sku);
    found.restore();
    return productAdapterPort.saveUpdateProduct(found);
  }

  @Override
  public void validateAvailabilityOrThrow(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.validateAvailability(quantity);
  }
}
