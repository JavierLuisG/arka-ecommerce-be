package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.*;
import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.domain.enums.*;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.*;
import com.store.arka.backend.shared.util.ValidateAttributesUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService implements IOrderUseCase {
  private final IOrderAdapterPort orderAdapterPort;
  private final IOrderItemUseCase orderItemUseCase;
  private final ICartAdapterPort cartAdapterPort;
  private final IProductUseCase productUseCase;
  private final ICustomerUseCase customerUseCase;
  private final INotificationUseCase notificationUseCase;

  @Override
  @Transactional
  public Order createOrder(UUID cartId) {
    Cart cartFound = findCartOrThrow(cartId);
    if (orderAdapterPort.existsByCartId(cartFound.getId())) {
      log.warn("[ORDER_SERVICE][CREATED] Order already exists whit this cart ID {}", cartId);
      throw new BusinessException("An order already exists for this Cart");
    }
    if (cartFound.getItems().isEmpty()) {
      log.warn("[ORDER_SERVICE][CREATED] Cart ID {} is empty", cartId);
      throw new ItemsEmptyException("Cart items in Order cannot be empty");
    }
    Customer customerFound = findCustomerOrThrow(cartFound.getCustomer().getId());
    List<OrderItem> orderItems = new ArrayList<>();
    cartFound.getItems().forEach(cartItem -> {
      productUseCase.validateAvailabilityOrThrow(cartItem.getProductId(), cartItem.getQuantity());
      orderItems.add(OrderItem.create(cartItem.getProduct(), cartItem.getQuantity()));
    });
    Order created = Order.create(cartId, customerFound, orderItems);
    Order saved = orderAdapterPort.saveCreateOrder(created);
    log.info("[ORDER_SERVICE][CREATED] Created new order ID {}", saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Order ID");
    return orderAdapterPort.findOrderById(id)
        .orElseThrow(() -> {
          log.warn("[ORDER_SERVICE][GET_BY_ID] Order ID {} not found", id);
          return new ModelNotFoundException("Order ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    log.info("[ORDER_SERVICE][GET_ALL] Fetching all orders");
    return orderAdapterPort.findAllOrders();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByStatus(OrderStatus status) {
    log.info("[ORDER_SERVICE][GET_ALL_BY_STATUS] Fetching all orders with status {}", status);
    return orderAdapterPort.findAllOrdersByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByCustomerId(UUID customerId) {
    findCustomerOrThrow(customerId);
    log.info("[ORDER_SERVICE][GET_ALL_BY_STATUS] Fetching all orders with customer ID {}", customerId);
    return orderAdapterPort.findAllOrdersByCustomerId(customerId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    log.info("[ORDER_SERVICE][GET_ALL_BY_STATUS] Fetching all orders with product ID {}", productId);
    return orderAdapterPort.findAllOrdersByItemsProductId(productId);
  }

  @Override
  @Transactional
  public Order addOrderItemById(UUID id, UUID productId, Integer quantity) {
    ValidateAttributesUtils.validateQuantity(quantity);
    Order orderFound = getOrderById(id);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (orderFound.containsProduct(productId)) {
      OrderItem orderItem = findOrderItemOrThrow(productId, orderFound);
      orderItemUseCase.addQuantityById(orderItem.getId(), quantity);
      orderFound = getOrderById(id);
      log.info("[ORDER_SERVICE][ADDED_ITEM] Add quantity {} in item ID {}", quantity, orderItem.getId());
    } else {
      OrderItem newItem = OrderItem.create(productFound, quantity);
      orderFound.getItems().add(newItem);
      OrderItem saved = orderItemUseCase.addOrderItem(orderFound.getId(), newItem);
      log.info("[ORDER_SERVICE][ADDED_ITEM] Create item ID {} whit product ID {} in order ID {}",
          saved.getId(), productId, id);
    }
    orderFound.recalculateTotal();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    log.info("[ORDER_SERVICE][ADDED_ITEM] Updated order ID {} ", saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Order updateOrderItemQuantityById(UUID id, UUID productId, Integer quantity) {
    productUseCase.validateAvailabilityOrThrow(productId, quantity);
    Order orderFound = getOrderById(id);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (!orderFound.containsProduct(productFound.getId())) {
      log.warn("[ORDER_SERVICE][UPDATED_ITEM_QUANTITY] Product ID {} not found in order ID {}", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Order ID " + orderFound.getId());
    }
    OrderItem orderItem = findOrderItemOrThrow(productFound.getId(), orderFound);
    orderItemUseCase.updateQuantity(orderItem.getId(), quantity);
    Order orderUpdated = getOrderById(id);
    orderUpdated.recalculateTotal();
    Order saved = orderAdapterPort.saveUpdateOrder(orderUpdated);
    log.info("[ORDER_SERVICE][UPDATED_ITEM_QUANTITY] Updated quantity {} in item ID {} ", quantity, saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Order removeOrderItemById(UUID id, UUID productId) {
    Order orderFound = getOrderById(id);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (!orderFound.containsProduct(productFound.getId())) {
      log.warn("[ORDER_SERVICE][REMOVED_ITEM] Product ID {} not found in order ID {}", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Order ID " + orderFound.getId());
    }
    orderFound.removeOrderItem(productFound);
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    log.info("[ORDER_SERVICE][REMOVED_ITEM] Product ID {} has removed of order ID {}", productId, id);
    return saved;
  }

  @Override
  @Transactional
  public void confirmOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.getItems()
        .forEach(orderItem -> productUseCase.decreaseStock(orderItem.getProductId(), orderItem.getQuantity()));
    orderFound.confirm();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_CONFIRMED));
    log.info("[ORDER_SERVICE][CONFIRMED] Order ID {} was confirmed", id);
  }

  @Override
  @Transactional
  public void payOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.pay();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_PAID));
    log.info("[ORDER_SERVICE][PAID] Order ID {} was paid", id);
  }

  @Override
  @Transactional
  public void shippedOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.shipped();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_SHIPPED));
    log.info("[ORDER_SERVICE][SHIPPED] Order ID {} was shipped", id);
  }

  @Override
  @Transactional
  public void deliverOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.deliver();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_DELIVERED));
    log.info("[ORDER_SERVICE][DELIVERED] Order ID {} was delivered", id);
  }

  @Override
  @Transactional
  public void cancelOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.getItems()
        .forEach(orderItem -> productUseCase.increaseStock(orderItem.getProductId(), orderItem.getQuantity()));
    orderFound.cancel();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_CANCELED));
    log.info("[ORDER_SERVICE][CANCEL] Order ID {} was canceled", id);
  }

  private Cart findCartOrThrow(UUID cartId) {
    ValidateAttributesUtils.throwIfIdNull(cartId, "Cart ID in Order");
    return cartAdapterPort.findCartByIdAndStatus(cartId, CartStatus.CHECKED_OUT)
        .orElseThrow(() -> {
          log.info("[ORDER_SERVICE][FIND_CARD] Cart Id {} is not checkout state to create an order", cartId);
          return new ModelNotFoundException("Cart must be CHECKED_OUT to create an Order");
        });
  }

  private Customer findCustomerOrThrow(UUID customerId) {
    ValidateAttributesUtils.throwIfIdNull(customerId, "Customer ID in Order");
    Customer customer = customerUseCase.getCustomerById(customerId);
    customer.throwIfDeleted();
    return customer;
  }

  private Product findProductOrThrow(UUID productId) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Order");
    Product product = productUseCase.getProductById(productId);
    product.throwIfDeleted();
    return product;
  }

  private static OrderItem findOrderItemOrThrow(UUID productId, Order orderFound) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Order");
    return orderFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> {
          log.info("[ORDER_SERVICE][FIND_ORDER] Product ID {} not found in order", productId);
          return new ProductNotFoundInOperationException("Product ID " + productId + " not found in Order " + orderFound.getId());
        });
  }
}
