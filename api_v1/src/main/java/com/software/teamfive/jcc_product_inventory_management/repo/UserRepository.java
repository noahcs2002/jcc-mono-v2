package com.software.teamfive.jcc_product_inventory_management.repo;

import com.software.teamfive.jcc_product_inventory_management.model.biz.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    UUID id(UUID id);

    Optional<User> findByEmail(String email);
}
