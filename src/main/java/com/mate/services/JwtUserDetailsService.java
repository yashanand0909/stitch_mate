package com.mate.services;

import com.mate.models.entities.AuthUser;
import com.mate.repositories.AuthUserRepository;
import java.util.ArrayList;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class JwtUserDetailsService implements UserDetailsService {

  private AuthUserRepository authUserRepository;

  private PasswordEncoder bcryptEncoder;

  @Lazy
  public JwtUserDetailsService(
      AuthUserRepository authUserRepository, PasswordEncoder bcryptEncoder) {
    this.authUserRepository = authUserRepository;
    this.bcryptEncoder = bcryptEncoder;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    AuthUser authUser = authUserRepository.findByUsername(username);
    if (!authUser.getIsActive()) {
      log.error("User not active {}", username);
      throw new UsernameNotFoundException("User not Active: " + username);
    }
    if (authUser == null) {
      log.error("User not found {}", username);
      throw new UsernameNotFoundException("User not found with username: " + username);
    }
    log.info("user created {}", username);
    return new org.springframework.security.core.userdetails.User(
        authUser.getUsername(), authUser.getPassword(), new ArrayList<>());
  }

  public AuthUser save(AuthUser user) {
    AuthUser newUser = new AuthUser();
    newUser.setUsername(user.getUsername());
    newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
    newUser.setIsActive(true);
    log.info("user authorized {}", newUser);
    return authUserRepository.save(newUser);
  }
}
