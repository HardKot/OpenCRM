import { UserPermission } from "#entities/User";
import { FC } from "react";
import { useHasPermission } from "../model/useHasPermission";

const HOCHasPermission = <T extends object>(
  Component: FC<T>,
  permission: UserPermission,
) => {
  const WrappedComponent: FC<T> = (props) => {
    const hasAccess = useHasPermission(permission);

    if (!hasAccess) return null;
    return <Component {...props} />;
  };

  return WrappedComponent;
};

export { HOCHasPermission };
