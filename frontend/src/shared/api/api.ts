const BASE_URL: string = (import.meta as any).env.VITE_API_URL || 'http://localhost:8080/api';

class ApiClient {
    private baseUrl: string;

    constructor(baseUrl: string) {
        this.baseUrl = baseUrl;
    }

    private async request<T>(endpoint: string, method: string, body?: any): Promise<{ data: T }> {
        const headers: HeadersInit = {
            'Content-Type': 'application/json',
        };

        const user = localStorage.getItem('user');
        if (user) {
            try {
                const parsedUser = JSON.parse(user);
                if (parsedUser && parsedUser.token) {
                    headers['Authorization'] = `Bearer ${parsedUser.token}`;
                }
            } catch (e) {
                console.error('Error parsing user from localStorage', e);
            }
        }

        const config: RequestInit = {
            method,
            headers,
            body: body ? JSON.stringify(body) : undefined,
        };

        try {
            const response = await fetch(`${this.baseUrl}${endpoint}`, config);

            if (!response.ok) {
                const errorData = await response.json().catch(() => ({}));
                throw new Error(errorData.message || `HTTP error! status: ${response.status}`);
            }

            const data = await response.json();
            return { data };
        } catch (error) {
            console.error('API Request Failed:', error);
            throw error;
        }
    }

    get<T>(endpoint: string) {
        return this.request<T>(endpoint, 'GET');
    }

    post<T>(endpoint: string, body: any) {
        return this.request<T>(endpoint, 'POST', body);
    }

    put<T>(endpoint: string, body: any) {
        return this.request<T>(endpoint, 'PUT', body);
    }

    delete<T>(endpoint: string) {
        return this.request<T>(endpoint, 'DELETE');
    }
}

export const $api = new ApiClient(BASE_URL);
