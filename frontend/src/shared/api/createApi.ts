import { api } from "./apiClient"
import type { IResponse } from "./apiClient"

type HttpMethod = keyof typeof api;

export interface RequestOptions {
    url: string;
    method?: HttpMethod | Lowercase<HttpMethod>;
    body?: unknown;
    params?: Record<string, string>;
    headers?: Record<string, string>;
    contentType?: string;
}


interface DefinitionOptions<Result, Arg> {
    errorTransform?: (error: any) => string;
    dataTransform?: (data: any) => Result;
    query: (arg: Arg) => RequestOptions;
}


interface Definition<Result, Arg> {
    query: (arg: Arg) => RequestOptions;
    errorTransform?: (error: any) => string;
    dataTransform?: (data: any) => Result;
}

export function createEndpoint<Result, Arg>(definition: DefinitionOptions<Result, Arg>): Definition<Result, Arg> {
    return {
        query: definition.query,
        errorTransform: definition.errorTransform,
        dataTransform: definition.dataTransform
    }
}


type EndpointsMap = Record<string, Definition<any, any>>;

type Api<Definitions extends EndpointsMap> = {
    [K in keyof Definitions]: Definitions[K] extends Definition<infer Result, infer Arg>
        ? (Arg extends void ? () => Promise<IResponse<Result>> : (arg: Arg) => Promise<IResponse<Result>>)
        : never
}

interface CreateApiOptions<Definitions extends EndpointsMap> {
    baseUrl?: string;
    contentType?: string;
    headers?: Record<string, string>;
    endpoints: (builder: typeof createEndpoint) => Definitions;
}

function isHttpMethod(method: string): method is HttpMethod {
    return ['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'HEAD', 'OPTIONS'].includes(method.toUpperCase());
}

export function createApi<Definitions extends EndpointsMap>(options: CreateApiOptions<Definitions>): Api<Definitions> {
    const endpointDefinitions = options.endpoints(createEndpoint);
    
    const { baseUrl = "", contentType: defaultContentType, headers: defaultHeaders } = options;
    const apiObj: any = {};

    for (const key in endpointDefinitions) {
        if (Object.prototype.hasOwnProperty.call(endpointDefinitions, key)) {
            const def = endpointDefinitions[key];
            
            apiObj[key] = async (arg: any) => {
                const config = def.query(arg);
                const { errorTransform, dataTransform } = def
                let method: HttpMethod | string = config.method?.toUpperCase() ?? 'GET';
                if (!isHttpMethod(method)) throw new Error(`Invalid HTTP method: ${method}`);
                
                
                let path = config.url;
                if (!path.startsWith('/')) path = '/' + path;
                
                let base = baseUrl;
                if (base.endsWith('/')) base = base.slice(0, -1);
                
                const finalUrl = `${base}${path}`;

                const requestHeaders = {
                    "content-type": config.contentType ?? defaultContentType ?? "application/json",
                    ...defaultHeaders,
                    ...config.headers
                };

                return await api[method]({
                   url: finalUrl,
                   body: config.body,
                   params: config.params,
                   headers: requestHeaders,
                   errorTransform,
                   dataTransform
                });
            }
        }
    }

    return apiObj;
}
