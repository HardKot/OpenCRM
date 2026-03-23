package com.open.crm.admin.application.interfaces;

import com.open.crm.admin.entities.tenant.Tenant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ITenantRepository extends JpaRepository<Tenant, UUID> {

  List<Tenant> findByReady(boolean ready);

  @Query("SELECT u.tenant FROM User u WHERE u.email = :email")
  Optional<Tenant> findByUserEmail(@Param("email") String email);
}
