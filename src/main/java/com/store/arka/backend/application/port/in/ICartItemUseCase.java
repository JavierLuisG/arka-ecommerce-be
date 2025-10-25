package com.store.arka.backend.application.port.in;

import com.store.arka.backend.domain.model.CartItem;

import java.util.List;
import java.util.UUID;

public interface ICartItemUseCase {
  CartItem addCartItem(UUID cartId, CartItem cartItem);

  CartItem getCartItemById(UUID id);

  List<CartItem> getAllCartItems();

  List<CartItem> getAllCartItemsByProductId(UUID productId);

  CartItem addQuantityById(UUID id, Integer quantity);

  CartItem updateQuantity(UUID id, Integer quantity);
}
