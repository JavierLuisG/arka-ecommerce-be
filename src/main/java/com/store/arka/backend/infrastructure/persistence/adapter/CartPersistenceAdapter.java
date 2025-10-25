package com.store.arka.backend.infrastructure.persistence.adapter;

import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.domain.enums.CartStatus;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.Cart;
import com.store.arka.backend.infrastructure.persistence.entity.CartEntity;
import com.store.arka.backend.infrastructure.persistence.mapper.CartMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaCartRepository;
import com.store.arka.backend.infrastructure.persistence.updater.CartUpdater;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartPersistenceAdapter implements ICartAdapterPort {
  private final IJpaCartRepository jpaCartRepository;
  private final CartMapper mapper;
  private final CartUpdater updater;
  @PersistenceContext
  private EntityManager entityManager;

  @Override
  @Transactional
  public Cart saveCreateCart(Cart cart) {
    CartEntity cartEntity = mapper.toEntity(cart);
    if (cartEntity.getCustomer() != null && cartEntity.getCustomer().getId() != null) {
      cartEntity.setCustomer(entityManager.getReference(cartEntity.getCustomer().getClass(), cartEntity.getCustomer().getId()));
    }
    if (cartEntity.getItems() != null) {
      cartEntity.getItems().forEach(item -> {
        if (item.getProduct() != null && item.getProduct().getId() != null) {
          item.setProduct(entityManager.getReference(item.getProduct().getClass(), item.getProduct().getId()));
        }
        item.setCart(cartEntity);
      });
    }
    CartEntity saved = jpaCartRepository.save(cartEntity);
    entityManager.flush();
    entityManager.refresh(saved);
    return mapper.toDomain(saved);
  }

  @Override
  public Cart saveUpdateCart(Cart cart) {
    return jpaCartRepository.findById(cart.getId())
        .map(exists -> {
          CartEntity saved = jpaCartRepository.save(updater.merge(exists, cart));
          entityManager.flush();
          entityManager.refresh(saved);
          return saved;
        })
        .map(mapper::toDomain)
        .orElseThrow(() -> new ModelNotFoundException("Cart with id " + cart.getId() + " not found"));
  }

  @Override
  public Optional<Cart> findCartById(UUID id) {
    return jpaCartRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Cart> findCartByIdAndStatus(UUID id, CartStatus status) {
    return jpaCartRepository.findByIdAndStatus(id, status).map(mapper::toDomain);
  }

  @Override
  public Optional<Cart> findCartByIdAndCustomerId(UUID id, UUID customerId) {
    return jpaCartRepository.findByIdAndCustomerId(id, customerId).map(mapper::toDomain);
  }

  @Override
  public Optional<Cart> findCartByIdAndCustomerIdAndStatus(UUID id, UUID customerId, CartStatus status) {
    return jpaCartRepository.findByIdAndCustomerIdAndStatus(id, customerId, status).map(mapper::toDomain);
  }

  @Override
  public List<Cart> findAllCarts() {
    return jpaCartRepository.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Cart> findAllCartsByStatus(CartStatus status) {
    return jpaCartRepository.findAllByStatus(status).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Cart> findAllCartsByCustomerId(UUID customerId) {
    return jpaCartRepository.findAllByCustomerId(customerId).stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Cart> findAllCartsByCustomerIdAndStatus(UUID customerId, CartStatus status) {
    return jpaCartRepository.findAllByCustomerIdAndStatus(customerId, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Cart> findAllCartsByItemsProductId(UUID productId) {
    return jpaCartRepository.findAllByItemsProductId(productId)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Cart> findAllCartsByItemsProductIdAndStatus(UUID productId, CartStatus status) {
    return jpaCartRepository.findAllByItemsProductIdAndStatus(productId, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public List<Cart> findAllCartsByCustomerIdAndItemsProductIdAndStatus(UUID customerId, UUID productId, CartStatus status) {
    return jpaCartRepository.findAllByCustomerIdAndItemsProductIdAndStatus(customerId, productId, status)
        .stream().map(mapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public void deleteById(UUID id) {
    jpaCartRepository.deleteById(id);
  }
}
