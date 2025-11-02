package com.store.arka.backend.shared.util;

import com.store.arka.backend.domain.enums.NotificationType;

import java.util.UUID;

public final class MessageNotificationType {
  public static String defaultMessageFor(NotificationType type, String customerName, UUID orderId) {
    String name = capitalizeWords(customerName);
    return switch (type) {
      case ORDER_CONFIRMED ->
          name + ", nos complace informarte que tu pedido #" + orderId + " ha sido confirmado exitosamente.";
      case ORDER_PAID ->
          name + ", hemos recibido correctamente el pago de tu pedido #" + orderId + ".";
      case ORDER_SHIPPED ->
          name + ", ¡buenas noticias! Tu pedido #" + orderId +
              " ha sido enviado y está en camino hacia tu dirección de entrega.";
      case ORDER_DELIVERED ->
          name + ", confirmamos que tu pedido #" + orderId + " ha sido entregado.";
      case ORDER_CANCELED ->
          name + ", lamentamos informarte que tu pedido #" + orderId + " ha sido cancelado.";
    };
  }

  private static String capitalizeWords(String text) {
    if (text == null || text.isBlank()) return text;
    String[] words = text.trim().toLowerCase().split("\\s+");
    StringBuilder sb = new StringBuilder();
    for (String word : words) {
      if (!word.isEmpty()) {
        sb.append(Character.toUpperCase(word.charAt(0)))
            .append(word.substring(1))
            .append(" ");
      }
    }
    return sb.toString().trim();
  }
}
