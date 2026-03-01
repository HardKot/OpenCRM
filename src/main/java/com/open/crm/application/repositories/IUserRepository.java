package com.open.crm.application.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.open.crm.domain.common.User;

import java.util.UUID;

public interface IUserRepository extends JpaRepository<User, UUID> {
	public boolean existsByEmail(String email);
}
