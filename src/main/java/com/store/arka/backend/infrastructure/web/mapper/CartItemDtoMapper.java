package com.store.arka.backend.infrastructure.web.mapper;

import com.store.arka.backend.domain.model.CartItem;
import com.store.arka.backend.infrastructure.web.dto.cartitem.request.CreateCartItemDto;
import com.store.arka.backend.infrastructure.web.dto.cartitem.response.CartItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CartItemDtoMapper {
  private final ProductDtoMapper productDtoMapper;

  public CartItemResponseDto toDto(CartItem domain) {
    return new CartItemResponseDto(
        domain.getId(),
        productDtoMapper.toCartDto(domain.getProduct()),
        domain.getQuantity(),
        domain.getAddedAt()
    );
  }

  public List<CartItem> toDomain(List<CreateCartItemDto> listDto) {
    List<CartItem> response = new ArrayList<>();
    listDto.forEach(dto -> {
      response.add(new CartItem(
          null,
          dto.productId(),
          null,
          dto.quantity(),
          null
      ));
    });
    return response;
  }

  public List<CartItemResponseDto> toDto(List<CartItem> listDomain) {
    List<CartItemResponseDto> response = new ArrayList<>();
    listDomain.forEach(domain -> {
      response.add(new CartItemResponseDto(
          domain.getId(),
          productDtoMapper.toCartDto(domain.getProduct()),
          domain.getQuantity(),
          domain.getAddedAt()
      ));
    });
    return response;
  }
}
