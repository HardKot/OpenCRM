import { UserPermission } from "../model/UserPermission";
import { UserRole } from "../model/UserRole";

export function isUserRole(role: string): role is UserRole {
  return Object.values(UserRole).includes(role as UserRole);
}

export function isUserPermission(
  permission: string,
): permission is UserPermission {
  return Object.values(UserPermission).includes(permission as UserPermission);
}
