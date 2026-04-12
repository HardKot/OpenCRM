import { createApi } from "@reduxjs/toolkit/query/react";
import { BaseFetchQuery } from "./api";
import { PageResponse, SuggestResponse } from "./types";

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
  email?: string;
  phone?: string;
  page: number;
  size: number;
  isDeleted?: boolean;
  sortBy?: string;
  sortDirection?: "asc" | "desc";
}

const employeeApi = createApi({
  reducerPath: "api/employeeApi",
  baseQuery: BaseFetchQuery(),
  tagTypes: ["Employee", "Position"],
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

    saveEmployee: build.mutation<EmployeeDto, EmployeeDto>({
      query: (body) => ({
        url: body.id ? `/api/employee/${body.id}` : "/api/employee",
        method: body.id ? "PUT" : "POST",
        body,
      }),
      invalidatesTags: (result) => [
        { type: "Employee", id: result?.id },
        { type: "Employee", id: "LIST" },
        { type: "Position", id: "LIST" },
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
export const useGetSuggestPositions = employeeApi.useGetSuggestPositionsQuery;
export const useSaveEmployee = employeeApi.useSaveEmployeeMutation;
export const useDeleteEmployee = employeeApi.useDeleteEmployeeMutation;
export const useRestoreEmployee = employeeApi.useRestoreEmployeeMutation;

export { employeeApi };
