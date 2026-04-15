import { UserPermission } from "#entities/User";
import { PropsWithChildren } from "react";
import { useHasPermission } from "../model/useHasPermission";

interface HasPermissionProps extends PropsWithChildren {
  permission: UserPermission;
}

const HasPermission = ({ permission, children }: HasPermissionProps) => {
  const hasPermission = useHasPermission(permission);

  if (hasPermission) return children;
  return null;
};

export { HasPermission };
