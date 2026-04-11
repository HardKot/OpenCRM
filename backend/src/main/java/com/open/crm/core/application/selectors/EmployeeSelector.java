package com.open.crm.core.application.selectors;

import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.results.ResultApp;
import com.open.crm.core.application.services.SelectorData;
import com.open.crm.core.application.specification.EmployeeSpecification;
import com.open.crm.core.entities.employee.Employee;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class EmployeeSelector {
  private static final String FIELD_ID = "id";
  private static final String FIELD_FIRSTNAME = "firstname";
  private static final String FIELD_LASTNAME = "lastname";
  private static final String FIELD_FULLNAME = "fullname";
  private static final String FIELD_PATRONYMIC = "patronymic";
  private static final String FIELD_POSITION = "position";
  private static final String FIELD_EMAIL = "email";
  private static final String FIELD_PHONE_NUMBER = "phoneNumber";

  private static final Set<String> ALLOWED_SORT_FIELDS =
      Set.of(FIELD_ID, FIELD_FULLNAME, FIELD_POSITION, FIELD_PHONE_NUMBER, FIELD_EMAIL);

  private final SelectorData<Employee> selector;
  @Setter private String fullname;
  @Setter private String email;
  @Setter private String phoneNumber;
  @Setter private String position;
  @Setter private boolean includeDeleted = false;
  @Setter private int page = 0;
  @Setter private int size = 100;
  @Setter private String sortBy = FIELD_ID;
  @Setter private SortDirection sortDirection = SortDirection.ASC;
  private Page<Employee> resultPage;

  public EmployeeSelector(IEmployeeRepository employeeRepository) {
    this.selector = new SelectorData<>(employeeRepository);
  }

  public List<Employee> getItems() {
    return resultPage.getContent();
  }

  public long getTotalItems() {
    return resultPage.getTotalElements();
  }

  public int getTotalPages() {
    return resultPage.getTotalPages();
  }

  public ResultApp<EmployeeSelector> search() {
    ResultApp<EmployeeSelector> validationResult = validateSort();
    if (!(validationResult instanceof ResultApp.Ok<EmployeeSelector>)) {
      return validationResult;
    }

    Specification<Employee> specification = buildSpecification();
    Sort sort = buildSort();

    resultPage = selector.getPage(page, size, includeDeleted, specification, sort);

    return new ResultApp.Ok<>(this);
  }

  private ResultApp<EmployeeSelector> validateSort() {
    if (Objects.isNull(sortBy) || sortBy.isBlank()) {
      sortBy = FIELD_ID;
    }

    if (!ALLOWED_SORT_FIELDS.contains(sortBy)) {
      return new ResultApp.InvalidData<>("Invalid sort field: " + sortBy);
    }

    return new ResultApp.Ok<>(this);
  }

  private Specification<Employee> buildSpecification() {
    Specification<Employee> specification = Specification.anyOf();

    if (Objects.nonNull(fullname) && !fullname.isBlank()) {
      specification = specification.and(EmployeeSpecification.findByFullname(fullname));
    }

    if (Objects.nonNull(email) && !email.isBlank()) {
      specification = specification.and(EmployeeSpecification.findByEmail(email));
    }

    if (Objects.nonNull(phoneNumber) && !phoneNumber.isBlank()) {
      specification = specification.and(EmployeeSpecification.findByPhoneNumber(phoneNumber));
    }

    if (Objects.nonNull(position) && !position.isBlank()) {
      specification = specification.and(EmployeeSpecification.findByPosition(position));
    }

    return specification;
  }

  private Sort buildSort() {
    Sort.Direction direction = Sort.Direction.ASC;
    if (sortDirection == SortDirection.DESC) direction = Sort.Direction.DESC;

    if (FIELD_FULLNAME.equals(sortBy)) {
      return Sort.by(
          new Order(direction, FIELD_LASTNAME),
          new Order(direction, FIELD_FIRSTNAME),
          new Order(direction, FIELD_PATRONYMIC),
          new Order(direction, FIELD_ID));
    }

    return Sort.by(new Order(direction, sortBy), new Order(direction, FIELD_ID));
  }
}
