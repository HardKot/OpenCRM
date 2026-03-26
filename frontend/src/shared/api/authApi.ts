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
    endpoints: (create) => ({
        login: create<LoginResponse, LoginRequest>({
            query: (body) => ({
                method: 'POST',
                url: '/login',
                body
            }),
            errorTransform: (error) => error.message || 'Login failed',
        }),
        
        logout: create<void, void>({
            query: () => ({
                method: 'POST',
                url: '/logout'
            })
        }),
    })
});

export { AuthApi }