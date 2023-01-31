package com.mate.repositories;

import com.mate.models.entities.AuthUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthUserRepository extends JpaRepository<AuthUser, Long> {

  AuthUser findByUsername(String username);
}
