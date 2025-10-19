package com.store.arka.backend.application.port.out;

import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.model.Cart;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ICartAdapterPort {
  Cart saveCreateCart(Cart cart);

  Cart saveUpdateCart(Cart cart);

  Optional<Cart> findCartById(UUID id);

  Optional<Cart> findCartByIdAndStatus(UUID id, CartStatus status);

  Optional<Cart> findCartByIdAndCustomerId(UUID id, UUID customerId);

  Optional<Cart> findCartByIdAndCustomerIdAndStatus(UUID id, UUID customerId, CartStatus status);

  List<Cart> findAllCarts();

  List<Cart> findAllCartsByStatus(CartStatus status);

  List<Cart> findAllCartsByCustomerId(UUID customerId);

  List<Cart> findAllCartsByCustomerIdAndStatus(UUID customerId, CartStatus status);

  List<Cart> findAllCartsByItemsProductId(UUID productId);

  List<Cart> findAllCartsByItemsProductIdAndStatus(UUID productId, CartStatus status);

  List<Cart> findAllCartsByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, CartStatus status);

  void deleteById(UUID id);
}
