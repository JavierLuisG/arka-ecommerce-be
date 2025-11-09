package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.*;
import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import com.store.arka.backend.shared.util.ValidateStatusUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
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
    ValidateAttributesUtils.throwIfModelNull(cart, "Cart");
    Customer customerFound = findCustomerOrThrow(customerId);
    List<CartItem> cartItems = new ArrayList<>();
    cart.getItems().forEach(item -> {
      Product productFound = findProductOrThrow(item.getProductId());
      productFound.validateAvailability(item.getQuantity());
      cartItems.add(CartItem.create(productFound, item.getQuantity()));
    });
    Cart created = Cart.create(customerFound, cartItems);
    Cart saved = cartAdapterPort.saveCreateCart(created);
    log.info("[CART_SERVICE][CREATED] Created new cart ID {}", saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Cart getCartById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Cart ID");
    return cartAdapterPort.findCartById(id)
        .orElseThrow(() -> {
          log.warn("[CART_SERVICE][GET_BY_ID] Cart ID {} not found", id);
          return new ModelNotFoundException("Cart ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCarts() {
    log.info("[CART_SERVICE][GET_ALL] Fetching all carts");
    return cartAdapterPort.findAllCarts();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByStatus(CartStatus status) {
    log.info("[CART_SERVICE][GET_ALL_BY_STATUS] Fetching all carts with status {}", status);
    return cartAdapterPort.findAllCartsByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByCustomerId(UUID customerId) {
    findCustomerOrThrow(customerId);
    log.info("[CART_SERVICE][GET_ALL_BY_CUSTOMER] Fetching all carts with customer ID {}", customerId);
    return cartAdapterPort.findAllCartsByCustomerId(customerId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Cart> getAllCartsByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    log.info("[CART_SERVICE][GET_ALL_BY_PRODUCT] Fetching all carts with product ID {}", productId);
    return cartAdapterPort.findAllCartsByItemsProductId(productId);
  }

  @Override
  @Transactional
  public Cart addCartItem(UUID id, UUID productId, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
    cartFound.ensureCartIsModifiable();
    if (cartFound.containsProduct(productFound.getId())) {
      CartItem cartItem = findCartItemInCartOrThrow(productId, cartFound);
      CartItem saved = cartItemUseCase.addQuantityById(cartItem.getId(), quantity);
      log.info("[CART_SERVICE][ADDED_ITEM] Add quantity {} in item ID {}", quantity, cartItem.getId());
    } else {
      CartItem saved = cartItemUseCase.addCartItem(cartFound.getId(), CartItem.create(productFound, quantity));
      log.info("[CART_SERVICE][ADDED_ITEM] Create item ID {} whit product ID {} in cart ID {}",
          saved.getId(), productId, id);
    }
    Cart saved = cartAdapterPort.saveUpdateCart(cartFound);
    log.info("[CART_SERVICE][ADDED_ITEM] Updated cart ID {} ", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Cart updateCartItemQuantity(UUID id, UUID productId, Integer quantity) {
    productUseCase.validateAvailabilityOrThrow(productId, quantity);
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
    cartFound.ensureCartIsModifiable();
    if (!cartFound.containsProduct(productFound.getId())) {
      log.warn("[CART_SERVICE][UPDATED_ITEM_QUANTITY] Product ID {} not found in cart ID {}", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Cart ID " + cartFound.getId());
    }
    CartItem cartItem = findCartItemInCartOrThrow(productId, cartFound);
    cartItemUseCase.updateQuantity(cartItem.getId(), quantity);
    Cart saved = cartAdapterPort.saveUpdateCart(cartFound);
    log.info("[CART_SERVICE][UPDATED_ITEM_QUANTITY] Updated quantity {} in item ID {} ", quantity, saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Cart removeCartItem(UUID id, UUID productId) {
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
    cartFound.ensureCartIsModifiable();
    if (!cartFound.containsProduct(productFound.getId())) {
      log.warn("[CART_SERVICE][REMOVED_ITEM] Product ID {} not found in cart ID {}", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Cart ID " + cartFound.getId());
    }
    cartFound.removeCartItem(productFound);
    Cart saved = cartAdapterPort.saveUpdateCart(cartFound);
    log.info("[CART_SERVICE][REMOVE_ITEM] Product ID {} has removed of cart ID {}", productId, saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Cart emptyCartItems(UUID id) {
    Cart cartFound = getCartById(id);
    cartFound.emptyCartItems();
    Cart saved = cartAdapterPort.saveUpdateCart(cartFound);
    log.info("[CART_SERVICE][EMPTIED_CART_ITEMS] The items in cart ID {} have been emptied", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public String checkoutCart(UUID id) {
    Cart cartFound = getCartById(id);
    cartFound.checkout();
    Cart saved = cartAdapterPort.saveUpdateCart(cartFound);
    log.info("[CART_SERVICE][CHECKED_OUT] Cart ID {} was {}", id, saved.getStatus());
    Order orderCreated = orderUseCase.createOrder(saved.getId());
    return orderCreated.getId().toString();
  }

  @Override
  @Transactional
  public void deleteCart(UUID id) {
    Cart cartFound = getCartById(id);
    ValidateStatusUtils.throwIfCheckout(cartFound.getStatus());
    cartAdapterPort.deleteCartById(cartFound.getId());
    log.info("[CART_SERVICE][DELETED] Cart ID {} was deleted", id);
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    ValidateAttributesUtils.throwIfIdNull(customerId, "Customer ID in Cart");
    Customer customer = customerUseCase.getCustomerById(customerId);
    customer.throwIfDeleted();
    return customer;
  }

  private Product findProductOrThrow(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Cart");
    Product product =  productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }

  private static CartItem findCartItemInCartOrThrow(UUID productId, Cart cartFound) {
    return cartFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> {
          log.warn("[CART_SERVICE][FIND_CART_ITEM] Product ID {} not found in cart", productId);
          return new ProductNotFoundInOperationException(
              "Product ID " + productId + " not found in Cart " + cartFound.getId());
        });
  }
}
