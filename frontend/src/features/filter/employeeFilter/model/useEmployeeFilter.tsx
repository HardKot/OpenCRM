import { useAppSelector } from "#shared/hooks";

const useEmployeeFilter = () => useAppSelector(({
    filters: {
        employee
    }
}) => ({
    fullname: employee.fullnameLike,
    position: employee.position
}))

export { useEmployeeFilter }