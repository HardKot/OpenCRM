package com.open.crm.core.application.services;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.common.BaseEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

@RequiredArgsConstructor
public class SelectorData<T extends BaseEntity> {
  private final IRepository<T> repository;

  public Optional<T> getById(Long id) {
    return repository.findById(id);
  }

  public long countItems(boolean isDeleted) {
    if (isDeleted) {
      return repository.count();
    }
    return repository.countByIsDeleted(isDeleted);
  }

  public List<T> getItems(int page, int size, boolean isDeleted, String sortBy, String sortDirection) {

    Order order = Order.asc(sortBy);
    if ("desc".equalsIgnoreCase(sortDirection)) order = Order.desc(sortBy);
  
    Sort sort = Sort.by(order);
    
    PageRequest pageable = PageRequest.of(page, size, sort);
    if (isDeleted) {
      return repository.findAll(pageable).toList();
    }
    return repository.findAllByIsDeleted(pageable, true);
  }

  public List<T> getItems(int page, int size, boolean isDeleted) {
    return getItems(page, size, isDeleted, "id", "asc");
  }
}
