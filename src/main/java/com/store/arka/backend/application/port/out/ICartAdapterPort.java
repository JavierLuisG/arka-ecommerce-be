package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.model.Cart;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICartAdapterPort {
  Cart saveCreateCart(Cart cart);

  Cart saveUpdateCart(Cart cart);

  Optional<Cart> findCartById(UUID id);

  List<Cart> findAllCarts();

  List<Cart> findAllCartsByStatus(CartStatus status);

  List<Cart> findAllCartsByCustomerId(UUID customerId);

  List<Cart> findAllCartsByItemsProductId(UUID productId);

  void deleteCartById(UUID id);
}
