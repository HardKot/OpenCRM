package com.open.crm.core.application.services;

import com.open.crm.core.entities.employee.AccessPermission;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class ConflictAccessSetUseCase {
  public record Result(boolean isValid, String message) {}

  public Result execute(Set<AccessPermission> permissions) {
    Result employeeResult = conflictEmployee(permissions);
    if (!employeeResult.isValid) return employeeResult;

    Result clientResult = conflictClient(permissions);
    if (!clientResult.isValid) return clientResult;

    Result commodityResult = conflictCommodity(permissions);
    if (!commodityResult.isValid) return commodityResult;

    return new Result(true, "All permissions are valid.");
  }

  private Result conflictEmployee(Set<AccessPermission> permissions) {
    boolean hasRead = permissions.contains(AccessPermission.EMPLOYEE_READ);
    boolean hasUpdate = permissions.contains(AccessPermission.EMPLOYEE_UPDATE);
    boolean hasAccess = permissions.contains(AccessPermission.EMPLOYEE_ACCESS);

    if (hasUpdate && !hasRead) {
      return new Result(false, "EMPLOYEE_UPDATE permission requires EMPLOYEE_READ permission.");
    }

    if (hasAccess && !hasUpdate) {
      return new Result(
          false,
          "EMPLOYEE_ACCESS permission requires both EMPLOYEE_READ and EMPLOYEE_UPDATE permissions.");
    }

    return new Result(true, "Permissions are valid.");
  }

  private Result conflictClient(Set<AccessPermission> permissions) {
    boolean hasRead = permissions.contains(AccessPermission.CLIENT_READ);
    boolean hasUpdate = permissions.contains(AccessPermission.CLIENT_UPDATE);
    boolean hasNameShow = permissions.contains(AccessPermission.CLIENT_NAME_SHOW);
    boolean hasContactShow = permissions.contains(AccessPermission.CLIENT_CONTACT_SHOW);

    if ((hasNameShow || hasContactShow) && !hasRead) {
      return new Result(
          false,
          "CLIENT_NAME_SHOW and CLIENT_CONTACT_SHOW permissions require CLIENT_READ permission.");
    }

    if (hasUpdate && (!hasRead || !hasNameShow || !hasContactShow)) {
      return new Result(
          false,
          "CLIENT_UPDATE permission requires at least one of CLIENT_READ, CLIENT_NAME_SHOW, or CLIENT_CONTACT_SHOW permissions.");
    }

    return new Result(true, "Permissions are valid.");
  }

  private Result conflictCommodity(Set<AccessPermission> permissions) {
    boolean hasRead = permissions.contains(AccessPermission.COMMODITY_READ);
    boolean hasUpdate = permissions.contains(AccessPermission.COMMODITY_UPDATE);

    if (hasUpdate && !hasRead) {
      return new Result(false, "COMMODITY_UPDATE permission requires COMMODITY_READ permission.");
    }

    return new Result(true, "Permissions are valid.");
  }
}
