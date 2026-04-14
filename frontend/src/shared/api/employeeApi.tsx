import { createApi } from "@reduxjs/toolkit/query/react";
import { BaseFetchQuery } from "./api";
import { OptionalId, PageResponse, SuggestResponse } from "./types";

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

export interface EmployeeAccess {
  isAccessAllowed: boolean;
  permissions: string[];
  role?: string;
}

export interface EmployeeFormDto extends EmployeeDto, EmployeeAccess {}

export interface GetEmployeeListRequest {
  fullname?: string;
  position?: string;
  email?: string;
  phone?: string;
  page: number;
  size: number;
  isDeleted?: boolean;
  sortBy?: string;
  sortDirection?: "asc" | "desc";
}

export interface EmployeeFormServer {
  employee: EmployeeDto;
  permissions: string[];
  isAccessAllowed: boolean;
  role?: string;
}

const employeeApi = createApi({
  reducerPath: "api/employeeApi",
  baseQuery: BaseFetchQuery(),
  tagTypes: ["Employee", "Position", "EmployeeForm"],
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
      query: (args) => ({
        url: `/api/employee`,
        method: "GET",
        params: {
          page: args.page,
          size: args.size,
          fullname: args.fullname,
          position: args.position,
          email: args.email,
          phone: args.phone,
          isDeleted: args.isDeleted,
          sortBy: args.sortBy,
          sortDirection: args.sortDirection,
        },
      }),
      providesTags: () => [{ type: "Employee", id: "LIST" }],
    }),

    getSuggestPositions: build.query<SuggestResponse<string>, string>({
      query: (name) => ({
        url: `/api/employee/position`,
        method: "GET",
        params: {
          name,
        },
      }),
      providesTags: () => [{ type: "Position", id: "LIST" }],
    }),

    getEmployeeForm: build.query<EmployeeFormDto, number>({
      query: (id) => ({
        url: `/api/employee/${id}/form`,
        method: "GET",
      }),
      providesTags: (result) => [{ type: "EmployeeForm", id: result?.id }],
      transformResponse: (response: EmployeeFormServer) => ({
        ...response,
        ...response.employee,
      }),
    }),

    saveEmployeeForm: build.mutation<
      EmployeeFormDto,
      OptionalId<EmployeeFormDto>
    >({
      query: ({ permissions, isAccessAllowed, ...employee }) => ({
        url: employee.id
          ? `/api/employee/${employee.id}/form`
          : "/api/employee/form",
        method: employee.id ? "PUT" : "POST",
        body: {
          employee,
          permissions,
          isAccessAllowed,
        },
      }),
      invalidatesTags: (result) => [
        { type: "Employee", id: result?.id },
        { type: "Employee", id: "LIST" },
        { type: "Position", id: "LIST" },
        { type: "EmployeeForm", id: result?.id },
        { type: "Position", id: "LIST" },
      ],
      transformResponse: (response: EmployeeFormServer) => ({
        ...response,
        ...response.employee,
      }),
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
export const useGetSuggestPositions = employeeApi.useGetSuggestPositionsQuery;

export const useGetEmployeeForm = employeeApi.useLazyGetEmployeeFormQuery;
export const useSaveEmployeeForm = employeeApi.useSaveEmployeeFormMutation;
export const useDeleteEmployee = employeeApi.useDeleteEmployeeMutation;
export const useRestoreEmployee = employeeApi.useRestoreEmployeeMutation;

export { employeeApi };
