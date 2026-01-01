package com.example.delivery.repository;

import com.example.delivery.domain.entity.User;
import com.example.delivery.domain.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for User entity operations.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their username.
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if a username already exists.
     */
    boolean existsByUsername(String username);

    /**
     * Find all users by role.
     */
    List<User> findByRole(Role role);
}
