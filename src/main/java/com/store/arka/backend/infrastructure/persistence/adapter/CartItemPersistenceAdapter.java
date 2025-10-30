package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.ICartItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;
import com.store.arka.backend.infrastructure.persistence.entity.CartItemEntity;
import com.store.arka.backend.infrastructure.persistence.entity.ProductEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.CartItemMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaCartItemRepository;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaCartRepository;
import com.store.arka.backend.infrastructure.persistence.updater.CartItemUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartItemPersistenceAdapter implements ICartItemAdapterPort {
  private final IJpaCartItemRepository jpaCartItemRepository;
  private final IJpaCartRepository cartRepository;
  private final CartItemMapper mapper;
  private final CartItemUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public CartItem saveAddCartItem(UUID cartId, CartItem cartItem) {
    CartEntity cartEntity = cartRepository.findById(cartId)
        .orElseThrow(() -> new ModelNotFoundException("Cart with id " + cartId + " not found"));
    CartItemEntity cartItemEntity = mapper.toEntityWithCart(cartEntity, cartItem);
    cartItemEntity.setProduct(
        entityManager.getReference(ProductEntity.class, cartItemEntity.getProduct().getId()));
    CartItemEntity saved = jpaCartItemRepository.save(cartItemEntity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public CartItem saveUpdateCartItem(CartItem cartItem) {
    CartItemEntity cartItemEntity = jpaCartItemRepository.findById(cartItem.getId())
        .orElseThrow(() -> new ModelNotFoundException("CartItem with id " + cartItem.getId() + " not found"));
    CartItemEntity updated = updater.merge(cartItemEntity, cartItem);
    CartItemEntity saved = jpaCartItemRepository.save(updated);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<CartItem> findCartItemById(UUID id) {
    return jpaCartItemRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<CartItem> findAllCartItems() {
    return jpaCartItemRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<CartItem> findAllCartItemsByProductId(UUID productId) {
    return jpaCartItemRepository.findAllByProductId(productId)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public void deleteCartItemById(UUID id) {
    jpaCartItemRepository.deleteById(id);
  }
}
