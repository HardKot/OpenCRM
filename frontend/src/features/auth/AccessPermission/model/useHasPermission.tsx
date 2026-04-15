import { UserPermission, UserRole } from "#entities/User";
import { useAppSelector } from "#shared/hooks";

const useHasPermission = (permission: UserPermission) => {
  const role = useAppSelector((state) => state.user.authData?.role);
  const permissions = useAppSelector(
    (state) => state.user.authData?.permissions ?? [],
  );

  if (role === UserRole.Owner) return true;

  return permissions.includes(permission);
};

export { useHasPermission };
