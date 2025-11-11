package com.store.arka.backend.application.service;

import com.store.arka.backend.application.port.in.*;
import com.store.arka.backend.application.port.out.ICartAdapterPort;
import com.store.arka.backend.application.port.out.IOrderAdapterPort;
import com.store.arka.backend.domain.enums.*;
import com.store.arka.backend.domain.exception.*;
import com.store.arka.backend.domain.model.*;
import com.store.arka.backend.shared.security.SecurityUtils;
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
  private final SecurityUtils securityUtils;

  @Override
  @Transactional
  public Order createOrder(UUID cartId) {
    Cart cartFound = findCartOrThrow(cartId);
    validateCartIdExistence(cartId);
    validateItemsContent(cartFound);
    Customer customerFound = findCustomerOrThrow(cartFound.getCustomer().getId());
    List<OrderItem> orderItems = new ArrayList<>();
    cartFound.getItems().forEach(cartItem -> {
      productUseCase.validateAvailability(cartItem.getProductId(), cartItem.getQuantity());
      orderItems.add(OrderItem.create(cartItem.getProduct(), cartItem.getQuantity()));
    });
    Order created = Order.create(cartId, customerFound, orderItems);
    Order saved = orderAdapterPort.saveCreateOrder(created);
    log.info("[ORDER_SERVICE][CREATED] User(id={}) has created new Order(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderById(UUID id) {
    ValidateAttributesUtils.throwIfIdNull(id, "Order ID");
    return orderAdapterPort.findOrderById(id)
        .orElseThrow(() -> {
          log.warn("[ORDER_SERVICE][GET_BY_ID] Order(id={}) not found", id);
          return new ModelNotFoundException("Order ID " + id + " not found");
        });
  }

  @Override
  @Transactional(readOnly = true)
  public Order getOrderByIdSecure(UUID id) {
    Order found = getOrderById(id);
    securityUtils.requireOwnerOrRoles(found.getCustomer().getUserId(), "ADMIN", "MANAGER");
    return found;
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrders() {
    log.info("[ORDER_SERVICE][GET_ALL] Fetching all Orders");
    return orderAdapterPort.findAllOrders();
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByStatus(OrderStatus status) {
    log.info("[ORDER_SERVICE][GET_ALL_BY_STATUS] Fetching all Orders with status=({})", status);
    return orderAdapterPort.findAllOrdersByStatus(status);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByCustomerId(UUID customerId) {
    Customer found = findCustomerOrThrow(customerId);
    securityUtils.requireOwnerOrRoles(found.getUserId(), "ADMIN", "MANAGER");
    log.info("[ORDER_SERVICE][GET_ALL_BY_CUSTOMER] Fetching all Orders with Customer(id={})", customerId);
    return orderAdapterPort.findAllOrdersByCustomerId(customerId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<Order> getAllOrdersByItemsProductId(UUID productId) {
    findProductOrThrow(productId);
    log.info("[ORDER_SERVICE][GET_ALL_BY_PRODUCT] Fetching all Orders with Product(id={})", productId);
    return orderAdapterPort.findAllOrdersByItemsProductId(productId);
  }

  @Override
  @Transactional
  public Order addOrderItemById(UUID id, UUID productId, Integer quantity) {
    Order orderFound = getOrderById(id);
    securityUtils.requireOwnerOrRoles(orderFound.getCustomer().getUserId(), "ADMIN");
    ValidateAttributesUtils.validateQuantity(quantity);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (orderFound.containsProduct(productId)) {
      OrderItem orderItem = findOrderItemOrThrow(productId, orderFound);
      orderItemUseCase.addQuantityById(orderItem.getId(), quantity);
      orderFound = getOrderById(id);
      log.info("[ORDER_SERVICE][ADDED_ITEM] User(id={}) has added quantity {} in OrderItem(id={})",
          securityUtils.getCurrentUserId(), quantity, orderItem.getId());
    } else {
      OrderItem newItem = OrderItem.create(productFound, quantity);
      orderFound.getItems().add(newItem);
      OrderItem saved = orderItemUseCase.addOrderItem(orderFound.getId(), newItem);
      log.info("[ORDER_SERVICE][ADDED_ITEM] User(id={}) has created OrderItem(id={}) whit Product(id={}) in Order(id={})",
          securityUtils.getCurrentUserId(), saved.getId(), productId, id);
    }
    orderFound.recalculateTotal();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    log.info("[ORDER_SERVICE][ADDED_ITEM] User(id={}) has updated Order(id={})",
        securityUtils.getCurrentUserId(), saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Order updateOrderItemQuantityById(UUID id, UUID productId, Integer quantity) {
    Order orderFound = getOrderById(id);
    securityUtils.requireOwnerOrRoles(orderFound.getCustomer().getUserId(), "ADMIN");
    productUseCase.validateAvailability(productId, quantity);
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (!orderFound.containsProduct(productFound.getId())) {
      log.warn("[ORDER_SERVICE][UPDATED_ITEM_QUANTITY] Product(id={}) not found in Order(id={})", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Order ID " + orderFound.getId());
    }
    OrderItem orderItem = findOrderItemOrThrow(productFound.getId(), orderFound);
    orderItemUseCase.updateQuantity(orderItem.getId(), quantity);
    Order orderUpdated = getOrderById(id);
    orderUpdated.recalculateTotal();
    Order saved = orderAdapterPort.saveUpdateOrder(orderUpdated);
    log.info("[ORDER_SERVICE][UPDATED_ITEM_QUANTITY] User(id={}) has updated quantity {} in OrderItem(id={})",
        securityUtils.getCurrentUserId(), quantity, saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public Order removeOrderItemById(UUID id, UUID productId) {
    Order orderFound = getOrderById(id);
    securityUtils.requireOwnerOrRoles(orderFound.getCustomer().getUserId(), "ADMIN");
    Product productFound = findProductOrThrow(productId);
    orderFound.ensureOrderIsModifiable();
    if (!orderFound.containsProduct(productFound.getId())) {
      log.warn("[ORDER_SERVICE][REMOVED_ITEM] Product(id={}) not found in Order(id={})", productId, id);
      throw new ProductNotFoundInOperationException("Product not found in Order ID " + orderFound.getId());
    }
    orderFound.removeOrderItem(productFound);
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    log.info("[ORDER_SERVICE][REMOVED_ITEM] User(id={}) has removed Product(id={}) of Order(id={})",
        securityUtils.getCurrentUserId(), productId, saved.getId());
    return saved;
  }

  @Override
  @Transactional
  public void confirmOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    securityUtils.requireOwnerOrRoles(orderFound.getCustomer().getUserId(), "ADMIN");
    orderFound.getItems()
        .forEach(orderItem -> productUseCase.decreaseStock(orderItem.getProductId(), orderItem.getQuantity()));
    orderFound.confirm();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_CONFIRMED));
    log.info("[ORDER_SERVICE][CONFIRMED] User(id={}) has marked the Order(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), id, saved.getStatus());
  }

  @Override
  @Transactional
  public void payOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    securityUtils.requireOwnerOrRoles(orderFound.getCustomer().getUserId(), "ADMIN");
    orderFound.pay();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_PAID));
    log.info("[ORDER_SERVICE][PAID] User(id={}) has marked the Order(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), id, saved.getStatus());
  }

  @Override
  @Transactional
  public void shippedOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.shipped();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_SHIPPED));
    log.info("[ORDER_SERVICE][SHIPPED] User(id={}) has marked the Order(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), id, saved.getStatus());
  }

  @Override
  @Transactional
  public void deliverOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    orderFound.deliver();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_DELIVERED));
    log.info("[ORDER_SERVICE][DELIVERED] User(id={}) has marked the Order(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), id, saved.getStatus());
  }

  @Override
  @Transactional
  public void cancelOrderById(UUID id) {
    Order orderFound = getOrderById(id);
    securityUtils.requireOwnerOrRoles(orderFound.getCustomer().getUserId(), "ADMIN");
    orderFound.getItems()
        .forEach(orderItem -> productUseCase.increaseStock(orderItem.getProductId(), orderItem.getQuantity()));
    orderFound.cancel();
    Order saved = orderAdapterPort.saveUpdateOrder(orderFound);
    notificationUseCase.createNotification(Notification.create(saved.getCustomer(), saved, NotificationType.ORDER_CANCELED));
    log.info("[ORDER_SERVICE][CANCEL] User(id={}) has marked the Order(id={}) whit status=({})",
        securityUtils.getCurrentUserId(), id, saved.getStatus());
  }

  private Cart findCartOrThrow(UUID cartId) {
    ValidateAttributesUtils.throwIfIdNull(cartId, "Cart ID in Order");
    Cart found = cartAdapterPort.findCartById(cartId)
        .orElseThrow(() -> {
          log.warn("[ORDER_SERVICE][FIND_CARD] Cart(id={}) in order not found", cartId);
          return new ModelNotFoundException("Cart Id " + cartId + " in Order not found");
        });
    if (!found.isCheckout()) {
      log.warn("[ORDER_SERVICE][FIND_CARD] Cart(id={}) is not checkout state to create an Order", cartId);
      throw new InvalidStateException("Cart must be CHECKED_OUT to create an Order");
    }
    return found;
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

  private OrderItem findOrderItemOrThrow(UUID productId, Order orderFound) {
    ValidateAttributesUtils.throwIfIdNull(productId, "Product ID in Order");
    return orderFound.getItems()
        .stream()
        .filter(item -> item.getProductId().equals(productId))
        .findFirst()
        .orElseThrow(() -> {
          log.warn("[ORDER_SERVICE][FIND_ORDER_ITEM] Product(id={}) not found in Order(id={})",
              productId, orderFound.getId());
          return new ProductNotFoundInOperationException("Product ID " + productId + " not found in Order ID " + orderFound.getId());
        });
  }

  private void validateItemsContent(Cart cartFound) {
    if (cartFound.getItems().isEmpty()) {
      log.warn("[ORDER_SERVICE][CREATED] CartItems in Cart(id={}) are empty", cartFound.getId());
      throw new ItemsEmptyException("Cart items in Order cannot be empty");
    }
  }

  private void validateCartIdExistence(UUID cartId) {
    if (orderAdapterPort.existsByCartId(cartId)) {
      log.warn("[ORDER_SERVICE][CREATED] Order already exists whit this Cart(id={})", cartId);
      throw new BusinessException("An order already exists for this Cart");
    }
  }
}
