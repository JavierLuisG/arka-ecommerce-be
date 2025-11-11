package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.ICategoryAdapterPort;
import com.store.arka.backend.application.port.out.IProductAdapterPort;
import com.store.arka.backend.domain.enums.CategoryStatus;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.Category;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.shared.security.SecurityUtils;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import org.springframework.transaction.annotation.Transactional;
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
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public Product createProduct(Product product) {
    ValidateAttributesUtils.throwIfModelNull(product, "Product");
    validateSkuExistence(product.getSku());
    Product created = Product.create(
        product.getSku(),
        product.getName(),
        product.getDescription(),
        product.getPrice(),
        product.getStock()
    );
    Product saved = productAdapterPort.saveCreateProduct(created);
    log.info("[PRODUCT_SERVICE][CREATED] User(id={}) has created new Product(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Product getProductById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Product ID");
    return productAdapterPort.findProductById(id)
        .orElseThrow(() -> {
          log.warn("[PRODUCT_SERVICE][GET_BY_ID] Product(id={}) not found", id);
          return new ModelNotFoundException("Product ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Product getProductBySku(String sku) {
    String normalizedSku = ValidateAttributesUtils.throwIfNullOrEmpty(sku, "SKU");
    return productAdapterPort.findProductBySku(sku)
        .orElseThrow(() -> {
          log.warn("[PRODUCT_SERVICE][GET_BY_SKU] Product with SKU=({}) not found", normalizedSku);
          return new ModelNotFoundException("Product with SKU " + normalizedSku + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Product> getAllProducts() {
    log.info("[PRODUCT_SERVICE][GET_ALL] Fetching all Products");
    return productAdapterPort.findAllProducts();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Product> getAllProductsByStatus(ProductStatus status) {
    log.info("[PRODUCT_SERVICE][GET_ALL_BY_STATUS] Fetching all Products with status=({})", status);
    return productAdapterPort.findAllProductsByStatus(status);
  }

  @Override
  @Transactional
  public Product updateFieldsProduct(UUID id, Product product) {
    ValidateAttributesUtils.throwIfModelNull(product, "Product");
    Product found = getProductById(id);
    found.updateFields(product.getName(), product.getDescription(), product.getPrice());
    Product saved = productAdapterPort.saveUpdateProduct(found);
    log.info("[PRODUCT_SERVICE][UPDATED] User(id={}) has updated Product(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Product updateCategories(UUID id, Set<UUID> categories) {
    Product found = getProductById(id);
    found.throwIfDeleted();
    Set<Category> newCategories = new HashSet<>();
    categories.forEach(uuid -> {
      newCategories.add(categoryAdapterPort.findCategoryById(uuid)
          .filter(category -> category.getStatus().equals(CategoryStatus.ACTIVE))
          .orElseThrow(() -> {
            log.info("[PRODUCT_SERVICE][UPDATED_CATEGORIES] Category(id={}) in Product(id={}) not found",
                uuid, found.getId());
            return new ModelNotFoundException("Category ID " + uuid + " not found");
          }));
    });
    found.updateCategories(newCategories);
    Product saved = productAdapterPort.saveUpdateProduct(found);
    log.info("[PRODUCT_SERVICE][UPDATED_CATEGORIES] User(id={}) has updated categories in Product(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void decreaseStock(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.decreaseStock(quantity);
    productAdapterPort.saveUpdateProduct(found);
    log.info("[PRODUCT_SERVICE][DECREASED_STOCK] User(id={}) has decreased {} its stock in Product(id={})",
        securityUtils.getCurrentUserId(), quantity, found.getId());
  }

  @Override
  @Transactional
  public void increaseStock(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.increaseStock(quantity);
    productAdapterPort.saveUpdateProduct(found);
    log.info("[PRODUCT_SERVICE][INCREASED_STOCK] User(id={}) has increased {} its stock in Product(id={})",
        securityUtils.getCurrentUserId(), quantity, found.getId());
  }

  @Override
  @Transactional
  public void softDeleteProduct(UUID id) {
    Product found = getProductById(id);
    found.delete();
    productAdapterPort.saveUpdateProduct(found);
    log.info("[PRODUCT_SERVICE][DELETED] User(id={}) has marked the Product(id={}) whit status={}",
        securityUtils.getCurrentUserId(), found.getId(), found.getStatus());
  }

  @Override
  @Transactional
  public Product restoreProduct(UUID id) {
    Product found = getProductById(id);
    found.restore();
    Product restored = productAdapterPort.saveUpdateProduct(found);
    log.info("[PRODUCT_SERVICE][RESTORED] User(id={}) has restored Product(id={}) successfully",
        securityUtils.getCurrentUserId(), id);
    return restored;
  }

  @Override
  public void validateAvailability(UUID id, Integer quantity) {
    Product found = getProductById(id);
    found.validateAvailability(quantity);
  }

  private void validateSkuExistence(String sku) {
    String normalizedSku = ValidateAttributesUtils.throwIfNullOrEmpty(sku, "SKU");
    if (productAdapterPort.existsProductBySku(normalizedSku)) {
      log.warn("[PRODUCT_SERVICE][CREATED] SKU={} already exists", normalizedSku);
      throw new FieldAlreadyExistsException("SKU " + normalizedSku + " already exists. Choose a different SKU");
    }
  }
}
