package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.model.Cart;

import java.util.List;
import java.util.UUID;

public interface ICartUseCase {
  Cart createCart(Cart cart, UUID customerId);

  Cart getCartById(UUID id);

  List<Cart> getAllCarts();

  List<Cart> getAllCartsByStatus(CartStatus status);

  List<Cart> getAllCartsByCustomerId(UUID customerId);

  List<Cart> getAllCartsByItemsProductId(UUID productId);

  Cart addCartItem(UUID id, UUID productId, Integer quantity);

  Cart updateCartItemQuantity(UUID id, UUID productId, Integer quantity);

  Cart removeCartItem(UUID id, UUID productId);

  Cart emptyCartItems(UUID id);

  String checkoutCart(UUID id);

  void deleteCart(UUID id);
}
