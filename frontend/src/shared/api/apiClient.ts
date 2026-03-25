import { APP_CONSTANTS } from '../lib/appConstant'

export interface IRequestOptions {
    url: string,
    body?: unknown,
    params?: Record<string, string>,
    headers?: Record<string, string>,
}


export interface IResponse<T> {
    status: number,
    data: T | null,
    hasError: boolean,
    errorMessage?: string
}


async function request<T>(endpoint: string, method: string, headers: Record<string, string>, body: unknown, params: Record<string, string>): Promise<IResponse<T>> {
    try {
        endpoint = endpoint.startsWith('/') ? endpoint.substring(1) : endpoint
        endpoint = endpoint.includes('?') ? endpoint.split('?')[0] : endpoint

        if (headers['content-type'] === 'application/json' && body !== null && typeof body === 'object') {
            body = JSON.stringify(body);
        } else if (body !== null && typeof body !== 'string') {
            body = String(body);
        }

        if (endpoint.startsWith('/')) {
            endpoint = endpoint.substring(1);
        }

        const response = await fetch(`${APP_CONSTANTS.API_BASE_URL}/${endpoint}?${new URLSearchParams(params).toString()}`, {
            method,
            headers,
            body: body as BodyInit,
        });

        if (response.ok) {
            return ({
                status: response.status,
                data: await response.json(),
                hasError: false
            })
        }

        return ({
            status: response.status,
            data: null,
            hasError: true,
            errorMessage: await response.text()
        })
    } catch (error) {
        return ({
            status: -1,
            data: null,
            hasError: true,
            errorMessage: (error as Error).message
        })
    }
}


const api = Object.freeze({
    GET: <T>(options: IRequestOptions) => request<T>(options.url, 'GET', options.headers ?? {}, null, options.params ?? {}),
    POST: <T>(options: IRequestOptions) => request<T>(options.url, 'POST', options.headers ?? {}, options.body, options.params ?? {}),
    DELETE: <T>(options: IRequestOptions) => request<T>(options.url, 'DELETE', options.headers ?? {}, null, options.params ?? {}),
    PUT: <T>(options: IRequestOptions) => request<T>(options.url, 'PUT', options.headers ?? {}, options.body, options.params ?? {}),
    PATCH: <T>(options: IRequestOptions) => request<T>(options.url, 'PATCH', options.headers ?? {}, options.body, options.params ?? {})
})





export { api }