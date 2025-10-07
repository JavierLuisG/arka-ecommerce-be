package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.application.port.out.IProductAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.domain.model.Product;
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
  public Product createProduct(Product product) {
    if (product == null) {
      log.warn("ProductService, createProduct, cannot be null");
      throw new ModelNullException("Product cannot be null");
    }
    String normalizedSku = product.getSku().trim();
    String normalizedName = product.getName().trim().toLowerCase();
    String normalizedDescription = product.getDescription().trim();
    List<String> forbiddenNames = List.of("null", "default", "admin");

    if (forbiddenNames.contains(normalizedName)) {
      log.warn("ProductService, createProduct, product name is not allowed");
      throw new InvalidArgumentException("This product name is not allowed");
    }
    if (productAdapterPort.existsProductBySku(product.getSku())) {
      log.warn("ProductService, createProduct, product with SKU " + product.getSku() + " already exists");
      throw new FieldAlreadyExistsException("SKU " + product.getSku() + " already exists");
    }
    Product created = Product.create(
        normalizedSku,
        normalizedName,
        normalizedDescription,
        product.getPrice(),
        product.getStock()
    );
    return productAdapterPort.saveProduct(created);
  }

  @Override
  @Transactional
  public Product getProductById(UUID id) {
    return productAdapterPort.findProductById(id)
        .orElseThrow(() -> {
          log.warn("ProductService, getProductById, product with id " + id + " not found");
          throw new ModelNotFoundException("Product with id " + id + " not found");
        });
  }

  @Override
  @Transactional
  public Product getProductByIdAndStatus(UUID id, ProductStatus status) {
    Product found = getProductById(id);
    if (!found.getStatus().equals(status)) {
      log.warn("ProductService, getProductByIdAndStatus, product with id " + id + " not " + status.toString());
      throw new ModelNotFoundException("Product with id " + id + " not " + status.toString());
    }
    return found;
  }

  @Override
  @Transactional
  public Product getProductBySku(String sku) {
    return productAdapterPort.findProductBySku(sku)
        .filter(product -> !product.getStatus().equals(ProductStatus.ELIMINATED))
        .orElseThrow(() -> {
          log.warn("ProductService, getProductBySku, product with SKU " + sku + " not found");
          throw new ModelNotFoundException("Product with SKU " + sku + " not found");
        });
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
    String normalizedName = product.getName().trim().toLowerCase();
    String normalizedDescription = product.getDescription().trim();
    List<String> forbiddenNames = List.of("null", "default", "admin");

    if (forbiddenNames.contains(normalizedName)) {
      log.warn("ProductService, createProduct, product name is not allowed");
      throw new InvalidArgumentException("This product name is not allowed");
    }
    Product found = getProductByIdAndStatus(id, ProductStatus.ACTIVE);
    found.updateFields(normalizedName, normalizedDescription, product.getPrice());
    return productAdapterPort.saveProduct(found);
  }

  @Override
  @Transactional
  public Product updateCategories(UUID id, Set<UUID> categories) {
    Product found = getProductById(id);
    if (found.getStatus().equals(ProductStatus.ELIMINATED)) {
      log.warn("ProductService, updateCategories, product deleted previously");
      throw new ModelDeletionException("Product deleted previously");
    }
    Set<Category> newCategories = new HashSet<>();
    categories.forEach(uuid -> {
      newCategories.add(categoryAdapterPort.findCategoryById(uuid)
          .filter(category -> category.getStatus().equals(CategoryStatus.ACTIVE))
          .orElseThrow(() -> {
            log.warn("ProductService, updateCategories, category with id " + uuid + " not found");
            throw new ModelNotFoundException("Category with id " + uuid + " not found");
          }));
    });
    found.updateCategories(newCategories);
    return productAdapterPort.saveProduct(found);
  }

  @Override
  @Transactional
  public void decreaseStock(UUID id, Integer quantity) {
    Product found = getProductByIdAndStatus(id, ProductStatus.ACTIVE);
    found.decreaseStock(quantity);
    productAdapterPort.saveProduct(found);
  }

  @Override
  @Transactional
  public void increaseStock(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.increaseStock(quantity);
    productAdapterPort.saveProduct(found);
  }

  @Override
  @Transactional
  public void deleteProductById(UUID id) {
    Product found = getProductById(id);
    found.delete();
    productAdapterPort.saveProduct(found);
  }

  @Override
  @Transactional
  public Product restoreProductBySku(String sku) {
    Product found = productAdapterPort.findProductBySku(sku)
        .filter(product -> product.getStatus().equals(ProductStatus.ELIMINATED))
        .orElseThrow(() -> {
          log.warn("ProductService, restoreProductBySku, product with SKU " + sku + " not found");
          throw new ModelNotFoundException("Product with SKU " + sku + " not found");
        });
    found.restore();
    return productAdapterPort.saveProduct(found);
  }
}
