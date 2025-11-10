package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.IOrderItemUseCase;
import com.store.arka.backend.application.port.in.IProductUseCase;
import com.store.arka.backend.application.port.out.IOrderItemAdapterPort;
import com.store.arka.backend.domain.exception.ModelNotFoundException;
import com.store.arka.backend.domain.model.OrderItem;
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
public class OrderItemService implements IOrderItemUseCase {
  private final IOrderItemAdapterPort orderItemAdapterPort;
  private final IProductUseCase productUseCase;
  private final SecurityUtils securityUtils;

  @Override
  public OrderItem addOrderItem(UUID orderId, OrderItem orderItem) {
    ValidateAttributesUtils.throwIfIdNull(orderId, "Order ID in OrderItem");
    ValidateAttributesUtils.throwIfModelNull(orderItem, "OrderItem");
    productUseCase.validateAvailabilityOrThrow(orderItem.getProductId(), orderItem.getQuantity());
    OrderItem saved = orderItemAdapterPort.saveAddOrderItem(orderId, orderItem);
    log.info("[ORDER_ITEM_SERVICE][CREATED] User(id={}) has created new OrderItem(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public OrderItem getOrderItemById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "OrderItem ID");
    return orderItemAdapterPort.findOrderItemById(id)
        .orElseThrow(() -> {
          log.warn("[ORDER_ITEM_SERVICE][GET_BY_ID] OrderItem(id={}) not found", id);
          return new ModelNotFoundException("OrderItem ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderItem> getAllOrderItems() {
    log.info("[ORDER_ITEM_SERVICE][GET_ALL] Fetching all OrderItems");
    return orderItemAdapterPort.findAllOrderItems();
  }

  @Override
  @Transactional(readOnly = true)
  public List<OrderItem> getAllOrderItemsByProductId(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in OrderItem");
    log.info("[ORDER_ITEM_SERVICE][GET_ALL_BY_PRODUCT] Fetching all orderItems with product(id={})", productId);
    return orderItemAdapterPort.findAllOrderItemsByProductId(productId);
  }

  @Override
  public OrderItem addQuantityById(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    OrderItem found = getOrderItemById(id);
    found.addQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    OrderItem saved = orderItemAdapterPort.saveUpdateOrderItem(found);
    log.info("[ORDER_ITEM_SERVICE][ADDED_QUANTITY] User(id={}) has added quantity {} in OrderItem(id={})",
        securityUtils.getCurrentUserId(), quantity, id);
    return saved;
  }

  @Override
  public OrderItem updateQuantity(UUID id, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    OrderItem found = getOrderItemById(id);
    found.updateQuantity(quantity);
    productUseCase.validateAvailabilityOrThrow(found.getProductId(), found.getQuantity());
    OrderItem saved = orderItemAdapterPort.saveUpdateOrderItem(found);
    log.info("[ORDER_ITEM_SERVICE][UPDATED_QUANTITY] User(id={}) has updated quantity {} in OrderItem(id={})",
        securityUtils.getCurrentUserId(), quantity, id);
    return saved;
  }
}
