package com.store.arka.backend.shared.security;

import com.store.arka.backend.infrastructure.security.UserDetailsImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.UUID;

@Component
public class SecurityUtils {
  public UUID getAuthenticatedUserId() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return null;
    Object principal = auth.getPrincipal();
    if (principal instanceof UserDetailsImpl ud) return ud.getId();
    return null;
  }

  public String getAuthenticatedUsername() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return null;
    Object principal = auth.getPrincipal();
    if (principal instanceof UserDetailsImpl ud) return ud.getUsername();
    return null;
  }

  public boolean hasRole(String role) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return false;
    return auth.getAuthorities().stream()
        .anyMatch(g -> g.getAuthority().equals("ROLE_" + role));
  }

  public boolean hasAnyRole(String... roles) {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null || !auth.isAuthenticated()) return false;
    return Arrays.stream(roles)
        .anyMatch(r -> auth.getAuthorities().stream().anyMatch(g -> g.getAuthority().equals("ROLE_" + r)));
  }

  public boolean isOwner(UUID ownerId) {
    UUID authId = getAuthenticatedUserId();
    return authId != null && authId.equals(ownerId);
  }

  /**
   * Check owner OR any of provided roles.
   * Returns true if authenticated user equals ownerId OR has any of the roles.
   */
  public boolean isOwnerOrHasAnyRole(UUID ownerId, String... roles) {
    if (isOwner(ownerId)) return true;
    return hasAnyRole(roles);
  }

  /**
   * Throw AccessDeniedException if not owner nor roles.
   */
  public void requireOwnerOrRoles(UUID ownerId, String... roles) {
    if (!isOwnerOrHasAnyRole(ownerId, roles)) {
      throw new AccessDeniedException("Access denied");
    }
  }
}
