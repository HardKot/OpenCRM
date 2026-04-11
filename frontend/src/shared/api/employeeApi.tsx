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
  isDeleted?: boolean;
  sortBy?: string;
  sortDirection?: "asc" | "desc";
}

const employeeApi = createApi({
  reducerPath: "api/employeeApi",
  baseQuery: BaseFetchQuery(),
  tagTypes: ["Employee"],
  endpoints: (build) => ({
    getEmployeeById: build.query<EmployeeDto, number>({
      query: (id) => ({
        url: `/api/employee/${id}`,
        method: "GET",
      }),
      providesTags: (result) => [{ type: "Employee", id: result?.id }],
    }),

    getPageEmployees: build.query<
      PageResponse<EmployeeDto>,
      GetEmployeeListRequest
    >({
      query: ({
        page,
        size,
        fullname,
        position,
        isDeleted,
        sortBy,
        sortDirection,
      }) => ({
        url: `/api/employee`,
        method: "GET",
        params: {
          page,
          size,
          fullname,
          position,
          isDeleted,
          sortBy,
          sortDirection,
        },
      }),
      providesTags: () => [{ type: "Employee", id: "LIST" }],
    }),

    saveEmployee: build.mutation<EmployeeDto, EmployeeDto>({
      query: (body) => ({
        url: body.id ? `/api/employee/${body.id}` : "/api/employee",
        method: body.id ? "PUT" : "POST",
        body,
      }),
      invalidatesTags: (result) => [
        { type: "Employee", id: result?.id },
        { type: "Employee", id: "LIST" },
      ],
    }),

    deleteEmployee: build.mutation<void, number>({
      query: (id) => ({
        url: `/api/employee/${id}`,
        method: "DELETE",
      }),
      invalidatesTags: (result, error, id) => [
        { type: "Employee", id },
        { type: "Employee", id: "LIST" },
      ],
    }),
    restoreEmployee: build.mutation<void, number>({
      query: (id) => ({
        url: `/api/employee/${id}`,
        method: "POST",
      }),
      invalidatesTags: (result, error, id) => [
        { type: "Employee", id },
        { type: "Employee", id: "LIST" },
      ],
    }),
  }),
});

export const useEmployeeById = employeeApi.useLazyGetEmployeeByIdQuery;
export const useGetEmployeeById = employeeApi.useGetEmployeeByIdQuery;
export const useGetPageEmployees = employeeApi.useGetPageEmployeesQuery;
export const useSaveEmployee = employeeApi.useSaveEmployeeMutation;
export const useDeleteEmployee = employeeApi.useDeleteEmployeeMutation;
export const useRestoreEmployee = employeeApi.useRestoreEmployeeMutation;

export { employeeApi };
