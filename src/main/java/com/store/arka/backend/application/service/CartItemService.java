package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.ICartItemUseCase;
import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.ICartItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public CartItem addCartItem(UUID cartId, CartItem cartItem) {
    ValidateAttributesUtils.validateId(cartId, "Cart ID in CartItem");
    ValidateAttributesUtils.validateModel(cartItem, "CartItem");
    productUseCase.validateAvailability(cartItem.getProductId(), cartItem.getQuantity());
    CartItem saved = cartItemAdapterPort.saveAddCartItem(cartId, cartItem);
    log.info("[CART_ITEM_SERVICE][CREATED] User(id={}) has created new CartItem(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public CartItem getCartItemById(UUID id) {
    ValidateAttributesUtils.validateId(id, "CartItem ID");
    return cartItemAdapterPort.findCartItemById(id)
        .orElseThrow(() -> {
          log.warn("[CART_ITEM_SERVICE][GET_BY_ID] CartItem(id={}) not found", id);
          return new ModelNotFoundException("CartItem ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<CartItem> getAllCartItems() {
    log.info("[CART_ITEM_SERVICE][GET_ALL] Fetching all CartItems");
    return cartItemAdapterPort.findAllCartItems();
  }

  @Override
  @Transactional(readOnly = true)
  public List<CartItem> getAllCartItemsByProductId(UUID productId) {
    ValidateAttributesUtils.validateId(productId, "Product ID in CartItem");
    log.info("[CART_ITEM_SERVICE][GET_ALL_BY_PRODUCT] Fetching all CartItems with Product(id={})", productId);
    return cartItemAdapterPort.findAllCartItemsByProductId(productId);
  }

  @Override
  @Transactional
  public CartItem addQuantityById(UUID id, Integer quantity) {
    CartItem found = getCartItemById(id);
    found.addQuantity(quantity);
    productUseCase.validateAvailability(found.getProductId(), found.getQuantity());
    CartItem saved = cartItemAdapterPort.saveUpdateCartItem(found);
    log.info("[CART_ITEM_SERVICE][ADDED_QUANTITY] User(id={}) has added quantity=({}) in CartItem(id={})",
        securityUtils.getCurrentUserId(), quantity, id);
    return saved;
  }

  @Override
  @Transactional
  public CartItem updateQuantity(UUID id, Integer quantity) {
    CartItem found = getCartItemById(id);
    found.updateQuantity(quantity);
    productUseCase.validateAvailability(found.getProductId(), found.getQuantity());
    CartItem saved = cartItemAdapterPort.saveUpdateCartItem(found);
    log.info("[CART_ITEM_SERVICE][ADDED_QUANTITY] User(id={}) has updated quantity=({}) in CartItem(id={})",
        securityUtils.getCurrentUserId(), quantity, id);
    return saved;
  }
}
