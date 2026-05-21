package com.store.arka.backend.shared.util;

public final class NormalizationUtils {
  public static String normalizeShortText(String text) {
    if (text == null) return null;
    String normalized = text.trim().replaceAll("\\s+", " ");
    return normalized.isEmpty() ? null : normalized.toLowerCase();
  }

  public static String normalizeLongText(String text) {
    if (text == null) return null;
    String normalized = text.trim().replaceAll(" +", " ");
    return normalized.isEmpty() ? null : normalized;
  }

  public static String normalizeEmail(String email) {
    if (email == null) return null;
    return email.trim().toLowerCase();
  }

  public static String normalizePhone(String phone) {
    if (phone == null) return null;
    return phone.replaceAll("\\D", "");
  }

  public static String normalizeIdentifier(String text) {
    if (text == null) return null;
    String normalized = text.trim().replaceAll("\\s+", "");
    return normalized.isEmpty() ? null : normalized.toUpperCase();
  }
}
