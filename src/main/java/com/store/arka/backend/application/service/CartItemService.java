package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICartItemUseCase;
import com.store.arka.backend.application.port.out.ICartItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemUseCase {
  private final ICartItemAdapterPort cartItemAdapterPort;

  @Override
  public CartItem addCartItem(UUID cartId, CartItem cartItem) {
    if (cartItem == null) throw new ModelNullException("CartItem cannot be null");
    return cartItemAdapterPort.saveAddCartItem(cartId, cartItem);
  }

  @Override
  public CartItem getCartItemById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return cartItemAdapterPort.findCartItemById(id)
        .orElseThrow(() -> new ModelNotFoundException("CartItem with id " + id + " not found"));
  }

  @Override
  public List<CartItem> getAllCartItems() {
    return cartItemAdapterPort.findAllCartItems();
  }

  @Override
  public List<CartItem> getAllCartItemsByProductId(UUID productId) {
    return cartItemAdapterPort.findAllCartItemsByProductId(productId);
  }

  @Override
  public CartItem addQuantityById(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    CartItem found = getCartItemById(id);
    found.addQuantity(quantity);
    return cartItemAdapterPort.saveUpdateCartItem(found);
  }

  @Override
  public CartItem updateQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    CartItem found = getCartItemById(id);
    found.updateQuantity(quantity);
    return cartItemAdapterPort.saveUpdateCartItem(found);
  }

  @Override
  public void deleteCartItemById(UUID id) {
    CartItem found = getCartItemById(id);
    cartItemAdapterPort.deleteCartItemById(found.getId());
  }
}
