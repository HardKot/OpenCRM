package com.open.crm.components.services;

import com.open.crm.admin.entities.tenant.Tenant;
import com.open.crm.admin.entities.user.User;
import com.open.crm.components.errors.SessionException;
import com.open.crm.core.application.repositories.IEmployeeRepository;
import com.open.crm.core.application.services.ClientInfoCleaner;
import com.open.crm.core.entities.employee.AccessPermission;
import com.open.crm.core.entities.employee.Employee;
import com.open.crm.core.entities.investigationLog.Author;
import com.open.crm.core.entities.investigationLog.AuthorEntityName;
import com.open.crm.tenancy.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SessionService {

  private final IEmployeeRepository employeeRepository;

  public User getUser() throws SessionException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    Object principal = authentication.getPrincipal();

    if (principal instanceof User) {
      return (User) principal;
    }

    throw new SessionException("Unauthorized");
  }

  public Employee getEmployee() throws SessionException {
    User user = getUser();
    if (!user.isEmployeeUser()) {
      throw new SessionException("Current user is not an employee");
    }

    return employeeRepository
        .findById(user.getEntityId())
        .orElseThrow(() -> new SessionException("Employee not found for current user"));
  }

  public boolean isShowDeleted() throws SessionException {
    return true;
  }

  public Author getAuthor() throws SessionException {
    User user = getUser();

    Author author = new Author();
    author.setEntityName(AuthorEntityName.SYSTEM);

    if (user.isEmployeeUser()) {
      Employee employee = getEmployee();
      author.setEntityId(employee.getId());
      author.setEntityName(AuthorEntityName.EMPLOYEE);
    }

    return author;
  }

  public Tenant getTenant() throws SessionException {
    return TenantContext.getCurrentTenant();
  }

  public ClientInfoCleaner getClientInfoCleaner() throws SessionException {
    User user = getUser();

    boolean showName = user.hasPermission(AccessPermission.CLIENT_NAME_SHOW);
    boolean showContact = user.hasPermission(AccessPermission.CLIENT_CONTACT_SHOW);
    return new ClientInfoCleaner(!showName, !showContact);
  }

  public boolean hasPermission(AccessPermission permission) throws SessionException {
    User user = getUser();
    return user.hasPermission(permission);
  }
}
