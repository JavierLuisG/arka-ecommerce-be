package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.model.CartItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICartItemAdapterPort {
  CartItem saveAddCartItem(UUID cartId, CartItem cartItem);

  CartItem saveUpdateCartItem(CartItem cartItem);

  Optional<CartItem> findCartItemById(UUID id);

  List<CartItem> findAllCartItems();

  List<CartItem> findAllCartItemsByProductId(UUID productId);

  void deleteCartItemById(UUID id);
}
