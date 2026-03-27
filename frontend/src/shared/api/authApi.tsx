import { createApi } from "@reduxjs/toolkit/query/react";
import { BaseFetchQuery } from "./api";
import { Utils } from "#shared/index";

interface LoginRequest {
    username: string;
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


const authApi = createApi({
    reducerPath: "api/authApi",
    baseQuery: BaseFetchQuery({ url: "auth" }),
    endpoints: (build) => ({
        loginByUsername: build.mutation<LoginResponse, LoginRequest>({
            query: (data) => ({
                url: "/login",
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
                url: "/logout",
                method: "POST",
            }),
        }),
    }),
});


export const useLoginByUsername = authApi.useLoginByUsernameMutation;
export { authApi }
