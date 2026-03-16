package com.open.crm.core.application.services;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.common.BaseEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;

@RequiredArgsConstructor
public class SelectorData<T extends BaseEntity> {
  private final IRepository<T> repository;

  public Optional<T> getById(Long id) {
    return repository.findById(id);
  }

  public long countItems(boolean isDeleted) {
    return repository.countByIsDeleted(isDeleted);
  }

  public List<T> getItems(int page, int size, boolean isDeleted) {
    PageRequest pageable = PageRequest.of(page, size);
    return repository.findAllByIsDeleted(pageable, isDeleted);
  }
}
