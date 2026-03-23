package com.open.crm.admin.application.interfaces;

import com.open.crm.admin.entities.user.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends JpaRepository<User, UUID> {

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  @Query(
      "SELECT u FROM User u WHERE u.entityId = :employeeId AND u.tenant.id = :tenantId AND"
          + " u.entityName = 'EMPLOYEE'")
  Optional<User> findByEmployeeId(
      @Param("employeeId") Long employeeId, @Param("tenantId") UUID tenantId);

  @Query(
      "SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email ="
          + " :email AND u.tenant.id = :tenantId AND u.entityName = 'EMPLOYEE'")
  boolean existsEmployeeByEmail(@Param("email") String email, @Param("tenantId") UUID tenantId);
}
