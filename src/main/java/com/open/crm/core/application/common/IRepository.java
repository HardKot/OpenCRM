package com.open.crm.core.application.common;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import com.open.crm.core.entities.common.BaseEntity;

public interface IRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
    Optional<T> findById(Long id);

    long countByIsDeleted(boolean isDeleted);

    List<T> findAllByIsDeleted(PageRequest pageable, boolean isDeleted);
}
