package com.open.crm.core.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.open.crm.security.User;

import java.util.UUID;

/**
 * Repository for User entity in public schema
 */
@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {
    boolean existsByEmail(String email);
}
