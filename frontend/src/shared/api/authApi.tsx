import { createApi } from "@reduxjs/toolkit/query/react";
import { BaseFetchQuery } from "./api";
import { Utils } from "#shared/index";
import { EmployeeDto } from "./employeeApi";

interface LoginRequest {
    email: string;
    password: string;
}

interface LoginResponse {
    success: boolean;
    userId: string;
    tenantId: string;
    permissions: string[];
    entityId: number;
    entityName: string;
    role: string;
}

interface RegisterTenantRequest {
    email: string;
}

interface RegisterTenantResponse {
    success: boolean;
    message: string;
}

interface ForgoutPasswordRequest {
    email: string;
}

interface HoldSession {
    userId: string;
    tenantId: string;
    permissions: string[];
    role: string;
    entity: EmployeeDto
}

interface ChangePasswordRequest {
    password: string;
    newPassword: string;
    confirmPassword: string;
}

interface PasswordGenerationResponse {
    password: string;
}

type PasswordLevel = "WEAK" | "SIMPLE" | "MEDIUM" | "HARD";

const authApi = createApi({
    reducerPath: "api/authApi",
    baseQuery: BaseFetchQuery({  }),
    endpoints: (build) => ({
        loginByUsername: build.mutation<LoginResponse, LoginRequest>({
            query: (data) => ({
                url: "/auth/login",
                method: "POST",
                body: data,
            }),

            transformErrorResponse: (response) => {
                const { data } = response
                if (!Utils.isIMessage(data)) return { error: "Unknown error" }
                return { error: data.message }
            }
        }),
        logout: build.mutation<void, void>({
            query: () => ({
                url: "/auth/logout",
                method: "POST",
            }),
        }),
        registerTenant: build.mutation<RegisterTenantResponse, RegisterTenantRequest>({
            query: (data) => ({
                url: "/auth/tenant/register",
                method: "POST",
                body: data,
            }),
            transformErrorResponse: (response) => {
                const { data } = response
                if (!Utils.isIMessage(data)) return { error: "Unknown error" }
                return { error: data.message }
            }
        }),
        forgoutPassword: build.mutation<{}, ForgoutPasswordRequest>({
            query: (data) => ({
                url: "/auth/forgoutPassword",
                method: "POST",
                body: data,
            }),
            transformErrorResponse: (response) => {
                const { data } = response
                if (!Utils.isIMessage(data)) return { error: "Unknown error" }
                return { error: data.message }
            }
        }),
        holdSession: build.query<HoldSession, void>({
            query: () => ({
                url: "/session/hold",
                method: "GET",
            }),
        }),

        changePassword: build.mutation<LoginResponse, ChangePasswordRequest>({
            query: (data) => ({
                url: "/auth/password",
                method: "POST",
                body: data,
            }),
            transformErrorResponse: (response) => {
                const { data } = response
                if (!Utils.isIMessage(data)) return { error: "Unknown error" }
                return { error: data.message }
            }
        }),
        getPasswordLevel: build.query<PasswordLevel, string>({
            query: (data) => ({
                url: "/auth/password/level",
                method: "POST",
                body: data,
            }),
            transformResponse: (response: { level: PasswordLevel }) => response.level,
        }),
        generatePassword: build.query<string, void>({
            query: () => ({
                url: "/auth/password/generate",
                method: "GET",
            }),
            transformResponse: (response: PasswordGenerationResponse) => response.password,
        })
    }),
});


export const useLoginByUsername = authApi.useLoginByUsernameMutation;
export const useLogout = authApi.useLogoutMutation;
export const useRegisterTenant = authApi.useRegisterTenantMutation;
export const useForgoutPassword = authApi.useForgoutPasswordMutation;
export const useHoldSession = authApi.useLazyHoldSessionQuery;
export const useChangePassword = authApi.useChangePasswordMutation;
export const useGetPasswordLevel = authApi.useLazyGetPasswordLevelQuery;
export const useGeneratePassword = authApi.useGeneratePasswordQuery;

export { authApi }
