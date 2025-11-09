package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICartItemUseCase;
import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.ICartItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemService implements ICartItemUseCase {
  private final ICartItemAdapterPort cartItemAdapterPort;
  private final IProductUseCase productUseCase;

  @Override
  public CartItem addCartItem(UUID cartId, CartItem cartItem) {
    ValidateAttributesUtils.throwIfIdNull(cartId, "Cart ID in CartItem");
    ValidateAttributesUtils.throwIfModelNull(cartItem, "CartItem");
    productUseCase.validateAvailabilityOrThrow(cartItem.getProductId(), cartItem.getQuantity());
    CartItem saved = cartItemAdapterPort.saveAddCartItem(cartId, cartItem);
    log.info("[CART_ITEM_SERVICE][CREATED] Created new cartItem ID: {}", saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public CartItem getCartItemById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "CartItem ID");
    return cartItemAdapterPort.findCartItemById(id)
        .orElseThrow(() -> {
          log.warn("[CART_ITEM_SERVICE][GET_BY_ID] CartItem ID {} not found", id);
          return new ModelNotFoundException("CartItem ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<CartItem> getAllCartItems() {
    log.info("[CART_ITEM_SERVICE][GET_ALL] Fetching all cartItems");
    return cartItemAdapterPort.findAllCartItems();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CartItem> getAllCartItemsByProductId(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in CartItem");
    log.info("[CART_ITEM_SERVICE][GET_ALL_BY_PRODUCT] Fetching all cartItems with product {}", productId);
    return cartItemAdapterPort.findAllCartItemsByProductId(productId);
  }

  @Override
  public CartItem addQuantityById(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    CartItem found = getCartItemById(id);
    found.addQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    CartItem saved = cartItemAdapterPort.saveUpdateCartItem(found);
    log.info("[CART_ITEM_SERVICE][ADDED_QUANTITY] Add quantity {} in CARTItem ID {}", quantity, id);
    return saved;
  }

  @Override
  public CartItem updateQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    CartItem found = getCartItemById(id);
    found.updateQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    CartItem saved = cartItemAdapterPort.saveUpdateCartItem(found);
    log.info("[CART_ITEM_SERVICE][ADDED_QUANTITY] Update quantity {} in CARTItem ID {}", quantity, id);
    return saved;
  }
}
