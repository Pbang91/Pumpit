package com.example.pumpit.domain.user.repository;

import com.example.pumpit.global.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByRecoveryCode(String recoveryCode);
}
