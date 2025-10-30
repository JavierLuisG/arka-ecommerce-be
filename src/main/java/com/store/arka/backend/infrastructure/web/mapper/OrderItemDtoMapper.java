package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.OrderItem;
import com.store.arka.backend.infrastructure.web.dto.order.response.OrderItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class OrderItemDtoMapper {
  private final ProductDtoMapper productDtoMapper;

  public OrderItemResponseDto toDto(OrderItem domain) {
    return new OrderItemResponseDto(
        domain.getId(),
        productDtoMapper.toOrderDto(domain.getProduct()),
        domain.getQuantity(),
        domain.getProductPrice(),
        domain.getSubtotal(),
        domain.getCreatedAt()
    );
  }

  public List<OrderItemResponseDto> toDto(List<OrderItem> domainList) {
    List<OrderItemResponseDto> dtoList = new ArrayList<>();
    domainList.forEach(domain -> {
      dtoList.add(new OrderItemResponseDto(
          domain.getId(),
          productDtoMapper.toOrderDto(domain.getProduct()),
          domain.getQuantity(),
          domain.getProductPrice(),
          domain.getSubtotal(),
          domain.getCreatedAt()
      ));
    });
    return dtoList;
  }
}
