package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IOrderItemUseCase;
import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.IOrderItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.exception.ModelNullException;
import com.store.arka.backend.domain.model.OrderItem;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderItemService implements IOrderItemUseCase {
  private final IOrderItemAdapterPort orderItemAdapterPort;
  private final IProductUseCase productUseCase;

  @Override
  public OrderItem addOrderItem(UUID orderId, OrderItem orderItem) {
    if (orderItem == null) throw new ModelNullException("CartItem cannot be null");
    productUseCase.validateAvailabilityOrThrow(orderItem.getProductId(), orderItem.getQuantity());
    return orderItemAdapterPort.saveAddOrderItem(orderId, orderItem);
  }

  @Override
  @Transactional
  public OrderItem getOrderItemById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id);
    return orderItemAdapterPort.findOrderItemById(id)
        .orElseThrow(() -> new ModelNotFoundException("OrderItem with id " + id + " not found"));
  }

  @Override
  @Transactional
  public List<OrderItem> getAllOrderItems() {
    return orderItemAdapterPort.findAllOrderItems();
  }

  @Override
  @Transactional
  public List<OrderItem> getAllOrderItemsByProductId(UUID productId) {
    return orderItemAdapterPort.findAllOrderItemsByProductId(productId);
  }

  @Override
  public OrderItem addQuantityById(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    OrderItem found = getOrderItemById(id);
    found.addQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    return orderItemAdapterPort.saveUpdateOrderItem(found);
  }

  @Override
  public OrderItem updateQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    OrderItem found = getOrderItemById(id);
    found.updateQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    return orderItemAdapterPort.saveUpdateOrderItem(found);
  }
}
