package com.store.arka.backend.infrastructure.security;

import com.store.arka.backend.domain.exception.UserNotFoundException;
import com.store.arka.backend.infrastructure.persistence.mapper.UserMapper;
import com.store.arka.backend.infrastructure.persistence.repository.IJpaUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
  private final IJpaUserRepository jpaUserRepository;
  private final UserMapper mapper;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    return jpaUserRepository.findByEmail(email)
        .map(mapper::toDomain)
        .map(UserDetailsImpl::new)
        .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
  }
}
