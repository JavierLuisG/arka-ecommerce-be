package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Order;
import com.store.arka.backend.infrastructure.web.dto.order.response.OrderResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDtoMapper {
  private final OrderItemDtoMapper orderItemDtoMapper;
  private final CustomerDtoMapper customerDtoMapper;

  public OrderResponseDto toDto(Order domain) {
    return new OrderResponseDto(
        domain.getId(),
        domain.getCartId(),
        customerDtoMapper.toOrderDto(domain.getCustomer()),
        orderItemDtoMapper.toDto(domain.getItems()),
        domain.getTotal(),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt()
    );
  }
}
