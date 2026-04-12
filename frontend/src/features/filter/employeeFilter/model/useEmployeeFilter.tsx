import { useAppSelector } from "#shared/hooks";
import { createSelector } from "@reduxjs/toolkit";
import { RootState } from "#app/store";

const selectEmployeeFilter = createSelector(
  (state: RootState) => state.filters.employee,
  (employee) => ({
    fullname: employee.fullname,
    position: employee.position,
    email: employee.email,
    phone: employee.phone,
  }),
);

const useEmployeeFilter = () => useAppSelector(selectEmployeeFilter);

export { useEmployeeFilter };
