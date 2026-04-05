import { createApi } from "@reduxjs/toolkit/query/react";
import { BaseFetchQuery } from "./api";
import { PageResponse } from "./types";

export interface EmployeeDto {
    id: number;
    isDeleted: boolean;
    firstname: string;
    lastname: string;
    patronymic: string;
    position: string;
    email: string;
    phone: string;
}

export interface GetEmployeeListRequest {
    fullname?: string;
    position?: string;
    page: number;
    size: number;
}

const employeeApi = createApi({
    reducerPath: "api/employeeApi",
    baseQuery: BaseFetchQuery({  }),
    endpoints: (build) => ({
        getEmployeeById: build.query<EmployeeDto, number>({
            query: (id) => ({
                url: `/api/employee/${id}`,
                method: "GET",
            }),
        }),

        getPageEmployees: build.query<PageResponse<EmployeeDto>, GetEmployeeListRequest>({
            query: ({ page, size, fullname, position }) => ({
                url: `/api/employee`,
                method: "GET",
                params: {
                    page,
                    size,
                    fullname,
                    position
                }
            }),
        }),
    }),

});

export const useGetEmployeeById = employeeApi.useGetEmployeeByIdQuery;
export const useGetPageEmployees = employeeApi.useGetPageEmployeesQuery;


export { employeeApi };