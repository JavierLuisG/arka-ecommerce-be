package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.*;
import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import com.store.arka.backend.shared.util.ValidateStatusUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService implements ICartUseCase {
  private final ICartAdapterPort cartAdapterPort;
  private final IProductUseCase productUseCase;
  private final ICustomerUseCase customerUseCase;
  private final ICartItemUseCase cartItemUseCase;
  private final IOrderUseCase orderUseCase;

  @Override
  @Transactional
  public Cart createCart(Cart cart, UUID customerId) {
    if (cart == null) throw new ModelNullException("Cart cannot be null");
    Customer customerFound = findCustomerOrThrow(customerId);
    List<CartItem> cartItems = new ArrayList<>();
    cart.getItems().forEach(item -> {
      Product productFound = findProductOrThrow(item.getProductId());
      productFound.validateAvailability(item.getQuantity());
      cartItems.add(CartItem.create(productFound, item.getQuantity()));
    });
    Cart created = Cart.create(customerFound, cartItems);
    return cartAdapterPort.saveCreateCart(created);
  }

  @Override
  @Transactional(readOnly = true)
  public Cart getCartById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Cart ID");
    return cartAdapterPort.findCartById(id)
        .orElseThrow(() -> new ModelNotFoundException("Cart ID " + id + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Cart getCartByIdAndStatus(UUID id, CartStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id, "Cart ID");
    return cartAdapterPort.findCartByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Cart ID " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Cart getCartByIdAndCustomerId(UUID id, UUID customerId) {
    ValidateAttributesUtils.throwIfIdNull(id, "Cart ID");
    findCustomerOrThrow(customerId);
    return cartAdapterPort.findCartByIdAndCustomerId(id, customerId)
        .orElseThrow(() -> new ModelNotFoundException(
            "Cart ID " + id + " and customer ID " + customerId + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public Cart getCartByIdAndCustomerIdAndStatus(UUID id, UUID customerId, CartStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id, "Cart ID");
    findCustomerOrThrow(customerId);
    return cartAdapterPort.findCartByIdAndCustomerIdAndStatus(id, customerId, status)
        .orElseThrow(() -> new ModelNotFoundException(
            "Cart ID " + id + ", customer ID " + customerId + " and status " + status + " not found"));
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCarts() {
    return cartAdapterPort.findAllCarts();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByStatus(CartStatus status) {
    return cartAdapterPort.findAllCartsByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByCustomerId(UUID customerId) {
    findCustomerOrThrow(customerId);
    return cartAdapterPort.findAllCartsByCustomerId(customerId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByCustomerIdAndStatus(UUID customerId, CartStatus status) {
    findCustomerOrThrow(customerId);
    return cartAdapterPort.findAllCartsByCustomerIdAndStatus(customerId, status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    return cartAdapterPort.findAllCartsByItemsProductId(productId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByItemsProductIdAndStatus(UUID productId, CartStatus status) {
    findProductOrThrow(productId);
    return cartAdapterPort.findAllCartsByItemsProductIdAndStatus(productId, status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByCustomerIdAndItemsProductIdAndStatus(
      UUID customerId, UUID productId, CartStatus status) {
    findCustomerOrThrow(customerId);
    findProductOrThrow(productId);
    return cartAdapterPort.findAllCartsByCustomerIdAndItemsProductIdAndStatus(customerId, productId, status);
  }

  @Override
  @Transactional
  public Cart addCartItemById(UUID id, UUID productId, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
    cartFound.ensureCartIsModifiable();
    if (cartFound.containsProduct(productFound.getId())) {
      CartItem cartItem = findCartItemInCartOrThrow(productId, cartFound);
      cartItemUseCase.addQuantityById(cartItem.getId(), quantity);
    } else {
      cartItemUseCase.addCartItem(cartFound.getId(), CartItem.create(productFound, quantity));
    }
    return cartAdapterPort.saveUpdateCart(cartFound);
  }

  @Override
  @Transactional
  public Cart updateCartItemQuantityById(UUID id, UUID productId, Integer quantity) {
    productUseCase.validateAvailabilityOrThrow(productId, quantity);
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
    cartFound.ensureCartIsModifiable();
    if (!cartFound.containsProduct(productFound.getId())) {
      throw new ProductNotFoundInOperationException("Product not found in Cart ID " + cartFound.getId());
    }
    CartItem cartItem = findCartItemInCartOrThrow(productId, cartFound);
    cartItemUseCase.updateQuantity(cartItem.getId(), quantity);
    return cartAdapterPort.saveUpdateCart(cartFound);
  }

  @Override
  @Transactional
  public Cart removeCartItemById(UUID id, UUID productId) {
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
    cartFound.ensureCartIsModifiable();
    if (!cartFound.containsProduct(productFound.getId())) {
      throw new ProductNotFoundInOperationException("Product not found in Cart ID " + cartFound.getId());
    }
    cartFound.removeCartItem(productFound);
    return cartAdapterPort.saveUpdateCart(cartFound);
  }

  @Override
  @Transactional
  public Cart emptyCartItemsById(UUID id) {
    Cart cartFound = getCartById(id);
    cartFound.emptyCartItems();
    return cartAdapterPort.saveUpdateCart(cartFound);
  }

  @Override
  @Transactional
  public String checkedOutCartById(UUID id) {
    Cart cartFound = getCartById(id);
    cartFound.checkedOut();
    cartAdapterPort.saveUpdateCart(cartFound);
    Order orderCreated = orderUseCase.createOrder(cartFound.getId());
    return orderCreated.getId().toString();
  }

  @Override
  @Transactional
  public void deleteCartById(UUID id) {
    Cart cartFound = getCartById(id);
    ValidateStatusUtils.throwIfCheckedOut(cartFound.getStatus());
    cartAdapterPort.deleteCartById(cartFound.getId());
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    if (customerId == null) throw new InvalidArgumentException("Customer ID in Cart cannot be null");
    Customer customer = customerUseCase.getCustomerById(customerId);
    customer.throwIfDeleted();
    return customer;
  }

  private Product findProductOrThrow(UUID productId) {
    if (productId == null) throw new InvalidArgumentException("Product ID in Cart cannot be null");
    Product product =  productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }

  private static CartItem findCartItemInCartOrThrow(UUID productId, Cart cartFound) {
    return cartFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ProductNotFoundInOperationException(
            "Product ID " + productId + " not found in Order " + cartFound.getId()));
  }
}
