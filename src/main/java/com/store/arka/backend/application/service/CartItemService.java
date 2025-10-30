package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICartItemUseCase;
import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.ICartItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemUseCase {
  private final ICartItemAdapterPort cartItemAdapterPort;
  private final IProductUseCase productUseCase;

  @Override
  public CartItem addCartItem(UUID cartId, CartItem cartItem) {
    if (cartItem == null) throw new ModelNullException("CartItem cannot be null");
    productUseCase.validateAvailabilityOrThrow(cartItem.getProductId(), cartItem.getQuantity());
    return cartItemAdapterPort.saveAddCartItem(cartId, cartItem);
  }

  @Override
  @Transactional
  public CartItem getCartItemById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return cartItemAdapterPort.findCartItemById(id)
        .orElseThrow(() -> new ModelNotFoundException("CartItem with id " + id + " not found"));
  }

  @Override
  @Transactional
  public List<CartItem> getAllCartItems() {
    return cartItemAdapterPort.findAllCartItems();
  }

  @Override
  @Transactional
  public List<CartItem> getAllCartItemsByProductId(UUID productId) {
    return cartItemAdapterPort.findAllCartItemsByProductId(productId);
  }

  @Override
  public CartItem addQuantityById(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    CartItem found = getCartItemById(id);
    found.addQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    return cartItemAdapterPort.saveUpdateCartItem(found);
  }

  @Override
  public CartItem updateQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    CartItem found = getCartItemById(id);
    found.updateQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    return cartItemAdapterPort.saveUpdateCartItem(found);
  }
}
