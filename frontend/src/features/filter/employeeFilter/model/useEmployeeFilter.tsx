import { useAppSelector } from "#shared/hooks";

const useEmployeeFilter = () =>
  useAppSelector(({ filters: { employee } }) => ({
    fullname: employee.fullname,
    position: employee.position,
    email: employee.email,
    phone: employee.phone,
  }));

export { useEmployeeFilter };
