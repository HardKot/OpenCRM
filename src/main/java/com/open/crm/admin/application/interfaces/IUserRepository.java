package com.open.crm.admin.application.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.open.crm.admin.entities.user.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.entityId = :employeeId AND u.tenant.id = :tenantId AND u.entityName = 'EMPLOYEE'")
    Optional<User> findByEmployeeId(Long employeeId, UUID tenantId);
}
