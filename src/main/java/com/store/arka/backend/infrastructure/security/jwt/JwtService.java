package com.store.arka.backend.infrastructure.security.jwt;

import com.store.arka.backend.infrastructure.security.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class JwtService {
  @Value("${jwt.secret.key}")
  private String SECRET_KEY;

  @PostConstruct
  private void validateSecretKey() {
    if (SECRET_KEY == null || SECRET_KEY.isBlank()) {
      log.error("SECRET_KEY is missing. Application cannot start.");
      throw new IllegalStateException("SECRET_KEY is missing. Define it as an environment variable.");
    }
    try {
      byte[] decoded = Decoders.BASE64URL.decode(SECRET_KEY);
      if (decoded.length < 32) {
        log.error("SECRET_KEY is too short ({} bytes). Must be at least 32 bytes.", decoded.length);
        throw new IllegalStateException("SECRET_KEY is too short. Must be at least 32 bytes (256 bits).");
      }
      log.info("SECRET_KEY loaded successfully ({} bytes", decoded.length);
    } catch (Exception e) {
      log.error("Invalid SECRET_KEY format. It must be Base64URL encoded.", e);
      throw new IllegalStateException("Invalid SECRET_KEY format. Must be Base64URL encoded.", e);
    }
  }

  public String generateToken(UserDetails userDetails) {
    UserDetailsImpl userImpl = (UserDetailsImpl) userDetails;
    Map<String, Object> claims = Map.of(
        "id", userImpl.getId(),
        "email", userImpl.getUsername(),
        "roles", userImpl.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList())
    );
    return generateToken(claims, userDetails);
  }

  public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails) {
    return Jwts.builder()
        .claims(extractClaims)
        .subject(userDetails.getUsername())
        .issuedAt(new Date(System.currentTimeMillis()))
        .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256)
        .compact();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64URL.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parser()
        .verifyWith(getSignInKey())
        .build()
        .parseSignedClaims(token)
        .getPayload();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }
}
