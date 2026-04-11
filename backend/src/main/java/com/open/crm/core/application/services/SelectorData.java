package com.open.crm.core.application.services;

import com.open.crm.core.application.common.IRepository;
import com.open.crm.core.entities.common.BaseEntity;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;

@RequiredArgsConstructor
public class SelectorData<T extends BaseEntity> {
  private static final String DEFAULT_SORT_BY = "id";

  private final IRepository<T> repository;

  public Optional<T> getById(Long id) {
    return repository.findById(id);
  }

  // Get page
  public Page<T> getPage(
      int page, int size, boolean includeDeleted, Specification<T> specification, Sort sortBy) {
    Specification<T> finalSpecification = Specification.where(specification);
    if (!includeDeleted) {
      finalSpecification =
          finalSpecification.and((root, query, cb) -> cb.isFalse(root.get("isDeleted")));
    }

    Pageable pageable = PageRequest.of(page, size, sortBy);

    return repository.findAll(finalSpecification, pageable);
  }

  public Page<T> getPage(
      int page, int size, boolean includeDeleted, Specification<T> specification) {
    return getPage(page, size, includeDeleted, specification, Sort.by(Order.asc(DEFAULT_SORT_BY)));
  }

  public Page<T> getPage(int page, int size, boolean includeDeleted) {
    return getPage(
        page, size, includeDeleted, Specification.anyOf(), Sort.by(Order.asc(DEFAULT_SORT_BY)));
  }

  public Page<T> getPage(int page, int size) {
    return getPage(page, size, false, Specification.anyOf(), Sort.by(Order.asc(DEFAULT_SORT_BY)));
  }

  // Get count items
  public long countItems(Specification<T> specification, boolean includeDeleted) {
    return getPage(0, 0, includeDeleted, specification).getTotalElements();
  }

  public long countItems(boolean includeDeleted) {
    return getPage(0, 0, includeDeleted).getTotalElements();
  }

  public long countItems() {
    return getPage(0, 0).getTotalElements();
  }

  // Get items list
  public List<T> getItems(
      long offset, int size, boolean includeDeleted, Specification<T> specification, Sort sortBy) {
    int page = (int) (offset / size);
    return getPage(page, size, includeDeleted, specification, sortBy).getContent();
  }

  public List<T> getItems(
      long offset, int size, boolean includeDeleted, Specification<T> specification) {
    return getItems(
        offset, size, includeDeleted, specification, Sort.by(Order.asc(DEFAULT_SORT_BY)));
  }

  public List<T> getItems(long offset, int size, boolean includeDeleted) {
    return getItems(
        offset, size, includeDeleted, Specification.anyOf(), Sort.by(Order.asc(DEFAULT_SORT_BY)));
  }

  public List<T> getItems(long offset, int size) {
    return getItems(
        offset, size, false, Specification.anyOf(), Sort.by(Order.asc(DEFAULT_SORT_BY)));
  }

  public List<T> getItems(int size) {
    return getItems(0, size, false, Specification.anyOf(), Sort.by(Order.asc(DEFAULT_SORT_BY)));
  }
}
