package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.model.Cart;

import java.util.List;
import java.util.UUID;

public interface ICartUseCase {
  Cart createCart(Cart cart, UUID customerId);

  Cart getCartById(UUID id);

  Cart getCartByIdAndStatus(UUID id, CartStatus status);

  Cart getCartByIdAndCustomerId(UUID id, UUID customerId);

  Cart getCartByIdAndCustomerIdAndStatus(UUID id, UUID customerId, CartStatus status);

  List<Cart> getAllCarts();

  List<Cart> getAllCartsByStatus(CartStatus status);

  List<Cart> getAllCartsByCustomerId(UUID customerId);

  List<Cart> getAllCartsByCustomerIdAndStatus(UUID customerId, CartStatus status);

  List<Cart> getAllCartsByItemsProductId(UUID productId);

  List<Cart> getAllCartsByItemsProductIdAndStatus(UUID productId, CartStatus status);

  List<Cart> getAllCartsByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, CartStatus status);

  Cart addCartItemById(UUID id, UUID productId, Integer quantity);

  Cart updateCartItemQuantityById(UUID id, UUID productId, Integer quantity);

  Cart removeCartItemById(UUID id, UUID productId);

  Cart emptyCartItemsById(UUID id);

  void confirmedCartById(UUID id);

  void deleteCartById(UUID id);
}
