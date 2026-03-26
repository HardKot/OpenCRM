import { APP_CONSTANTS } from '../lib/appConstant'

export interface IRequestOptions {
    url: string,
    body?: unknown,
    params?: Record<string, string>,
    headers?: Record<string, string>,
    errorTransform?: (error: any) => any,
    dataTransform?: (data: any) => any
}


export interface IResponse<T> {
    status: number,
    data: T | null,
    hasError: boolean,
    errorMessage?: any
}


async function request<T>(endpoint: string, method: string, headers: Record<string, string>, body: unknown, params: Record<string, string>, errorTransform?: (error: any) => any, dataTransform?: (data: any) => any): Promise<IResponse<T>> {
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
            let data = await response.json();
            if (dataTransform) {
                data = dataTransform(data);
            }
            return ({
                status: response.status,
                data: data,
                hasError: false
            })
        }

        let errorMessage = await response.json();
        if (errorTransform) {
             errorMessage = errorTransform(errorMessage);
        }

        return ({
            status: response.status,
            data: null,
            hasError: true,
            errorMessage: errorMessage
        })
    } catch (error) {
        let errorMessage: any = (error as Error).message;
        if (errorTransform) {
            errorMessage = errorTransform(error);
        }
        return ({
            status: -1,
            data: null,
            hasError: true,
            errorMessage: errorMessage
        })
    }
}


const api = Object.freeze({
    GET: <T>(options: IRequestOptions) => request<T>(options.url, 'GET', options.headers ?? {}, null, options.params ?? {}, options.errorTransform, options.dataTransform),
    POST: <T>(options: IRequestOptions) => request<T>(options.url, 'POST', options.headers ?? {}, options.body, options.params ?? {}, options.errorTransform, options.dataTransform),
    DELETE: <T>(options: IRequestOptions) => request<T>(options.url, 'DELETE', options.headers ?? {}, null, options.params ?? {}, options.errorTransform, options.dataTransform),
    PUT: <T>(options: IRequestOptions) => request<T>(options.url, 'PUT', options.headers ?? {}, options.body, options.params ?? {}, options.errorTransform, options.dataTransform),
    PATCH: <T>(options: IRequestOptions) => request<T>(options.url, 'PATCH', options.headers ?? {}, options.body, options.params ?? {}, options.errorTransform, options.dataTransform)
})





export { api }