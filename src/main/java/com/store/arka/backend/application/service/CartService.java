package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICartItemUseCase;
import com.store.arka.backend.application.port.in.ICartUseCase;
import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.application.port.out.ICartItemAdapterPort;
import com.store.arka.backend.application.port.out.ICustomerAdapterPort;
import com.store.arka.backend.application.port.out.IProductAdapterPort;
import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.enums.CustomerStatus;
import com.store.arka.backend.domain.enums.ProductStatus;
import com.store.arka.backend.domain.exception.ModelNotAvailableException;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.exception.ProductNotFoundInCartException;
import com.store.arka.backend.domain.model.Cart;
import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.domain.model.Customer;
import com.store.arka.backend.domain.model.Product;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import com.store.arka.backend.shared.util.ValidateStatusUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CartService implements ICartUseCase {
  private final ICartAdapterPort cartAdapterPort;
  private final IProductAdapterPort productAdapterPort;
  private final ICustomerAdapterPort customerAdapterPort;
  private final ICartItemUseCase cartItemUseCase;

  @Override
  @Transactional
  public Cart createCart(Cart cart, UUID customerId) {
    if (cart == null) throw new ModelNullException("Cart cannot be null");
    Customer customerFound = findCustomerOrThrow(customerId);
    List<CartItem> cartItems = new ArrayList<>();
    cart.getItems().forEach(item -> {
      Product productFound = findProductOrThrow(item.getProductId());
      if (!productFound.isAvailable(item.getQuantity())) {
        throw new ModelNotAvailableException("Product with id " + item.getProductId() + " does not have sufficient stock");
      }
      cartItems.add(CartItem.create(productFound, item.getQuantity()));
    });
    Cart created = Cart.create(customerFound, cartItems);
    return cartAdapterPort.saveCreateCart(created);
  }

  @Override
  @Transactional
  public Cart getCartById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return cartAdapterPort.findCartById(id)
        .orElseThrow(() -> new ModelNotFoundException("Cart with id " + id + " not found"));
  }

  @Override
  @Transactional
  public Cart getCartByIdAndStatus(UUID id, CartStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return cartAdapterPort.findCartByIdAndStatus(id, status)
        .orElseThrow(() -> new ModelNotFoundException("Cart with id " + id + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public Cart getCartByIdAndCustomerId(UUID id, UUID customerId) {
    ValidateAttributesUtils.throwIfIdNull(id);
    Customer customerFound = findCustomerOrThrow(customerId);
    return cartAdapterPort.findCartByIdAndCustomerId(id, customerFound.getId())
        .orElseThrow(() -> new ModelNotFoundException(
            "Cart with id " + id + " and customerId " + customerId + " not found"));
  }

  @Override
  @Transactional
  public Cart getCartByIdAndCustomerIdAndStatus(UUID id, UUID customerId, CartStatus status) {
    ValidateAttributesUtils.throwIfIdNull(id);
    Customer customerFound = findCustomerOrThrow(customerId);
    return cartAdapterPort.findCartByIdAndCustomerIdAndStatus(id, customerFound.getId(), status)
        .orElseThrow(() -> new ModelNotFoundException(
            "Cart with id " + id + ", customerId " + customerId + " and status " + status + " not found"));
  }

  @Override
  @Transactional
  public List<Cart> getAllCarts() {
    return cartAdapterPort.findAllCarts();
  }

  @Override
  @Transactional
  public List<Cart> getAllCartsByStatus(CartStatus status) {
    return cartAdapterPort.findAllCartsByStatus(status);
  }

  @Override
  @Transactional
  public List<Cart> getAllCartsByCustomerId(UUID customerId) {
    return cartAdapterPort.findAllCartsByCustomerId(customerId);
  }

  @Override
  @Transactional
  public List<Cart> getAllCartsByCustomerIdAndStatus(UUID customerId, CartStatus status) {
    return cartAdapterPort.findAllCartsByCustomerIdAndStatus(customerId, status);
  }

  @Override
  @Transactional
  public List<Cart> getAllCartsByItemsProductId(UUID productId) {
    return cartAdapterPort.findAllCartsByItemsProductId(productId);
  }

  @Override
  @Transactional
  public List<Cart> getAllCartsByItemsProductIdAndStatus(UUID productId, CartStatus status) {
    return cartAdapterPort.findAllCartsByItemsProductIdAndStatus(productId, status);
  }

  @Override
  @Transactional
  public List<Cart> getAllCartsByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, CartStatus status) {
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
    ValidateAttributesUtils.validateQuantity(quantity);
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
    cartFound.ensureCartIsModifiable();
    if (cartFound.containsProduct(productFound.getId())) {
      CartItem cartItem = findCartItemInCartOrThrow(productId, cartFound);
      cartItemUseCase.updateQuantity(cartItem.getId(), quantity);
    }
    return cartAdapterPort.saveUpdateCart(cartFound);
  }

  @Override
  @Transactional
  public Cart removeCartItemById(UUID id, UUID productId) {
    Cart cartFound = getCartById(id);
    Product productFound = findProductOrThrow(productId);
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
  public void confirmedCartById(UUID id) {
    Cart cartFound = getCartById(id);
    cartFound.confirmed();
    cartAdapterPort.saveUpdateCart(cartFound);
  }

  @Override
  @Transactional
  public void deleteCartById(UUID id) {
    Cart cartFound = getCartById(id);
    ValidateStatusUtils.throwIfConfirmed(cartFound.getStatus());
    cartAdapterPort.deleteById(cartFound.getId());
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    return customerAdapterPort.findCustomerByIdAndStatus(customerId, CustomerStatus.ACTIVE)
        .orElseThrow(() -> new ModelNotFoundException(
            "Customer with id " + customerId + " and status " + CustomerStatus.ACTIVE + " not found"));
  }

  private Product findProductOrThrow(UUID productId) {
    return productAdapterPort.findProductByIdAndStatus(productId, ProductStatus.ACTIVE)
        .orElseThrow(() -> new ModelNotFoundException(
            "Product with id " + productId + " and status " + ProductStatus.ACTIVE + " not found"));
  }

  private static CartItem findCartItemInCartOrThrow(UUID productId, Cart cartFound) {
    return cartFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> new ProductNotFoundInCartException(
            "Product " + productId + " not found in cart " + cartFound.getId()));
  }
}
