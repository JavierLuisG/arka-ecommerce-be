package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.Cart;
import com.store.arka.backend.infrastructure.web.dto.cart.request.CreateCartDto;
import com.store.arka.backend.infrastructure.web.dto.cart.response.CartResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CartDtoMapper {
  private final CartItemDtoMapper cartItemDtoMapper;
  private final CustomerDtoMapper customerDtoMapper;

  public Cart toDomain(CreateCartDto dto) {
    return new Cart(
        null,
        null,
        cartItemDtoMapper.toDomain(dto.cartItems()),
        null,
        null,
        null,
        null
    );
  }

  public CartResponseDto toDto(Cart domain) {
    return new CartResponseDto(
        domain.getId(),
        customerDtoMapper.toCartDto(domain.getCustomer()),
        cartItemDtoMapper.toDto(domain.getItems()),
        domain.getStatus(),
        domain.getCreatedAt(),
        domain.getUpdatedAt(),
        domain.getAbandonedAt()
    );
  }
}
