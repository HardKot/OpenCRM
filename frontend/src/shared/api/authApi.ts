import { createApi } from "./createApi";

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

const AuthApi = createApi({
    baseUrl: '/auth',
    endpoints: (builder) => ({
        login: builder.mutation<LoginResponse, LoginRequest>({
            query: (body) => ({
                method: 'POST',
                url: '/login',
                body
            })
        }),
        
        logout: builder.mutation<void, void>({
            query: () => ({
                method: 'POST',
                url: '/logout'
            })
        }),
    })
});

export { AuthApi }