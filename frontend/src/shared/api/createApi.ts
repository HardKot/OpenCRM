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

interface Definition<Result, Arg> {
    type: 'query' | 'mutation';
    query: (arg: Arg) => RequestOptions;
}

export class EndpointBuilder {
    query<Result, Arg>(definition: { query: (arg: Arg) => RequestOptions }): Definition<Result, Arg> {
        return {
            type: 'query',
            query: definition.query
        }
    }
    
    mutation<Result, Arg>(definition: { query: (arg: Arg) => RequestOptions }): Definition<Result, Arg> {
        return {
            type: 'mutation',
            query: definition.query
        }
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
    endpoints: (builder: EndpointBuilder) => Definitions;
}

export function createApi<Definitions extends EndpointsMap>(options: CreateApiOptions<Definitions>): Api<Definitions> {
    const builder = new EndpointBuilder();
    const endpointDefinitions = options.endpoints(builder);
    
    const { baseUrl = "", contentType: defaultContentType, headers: defaultHeaders } = options;
    const apiObj: any = {};

    for (const key in endpointDefinitions) {
        if (Object.prototype.hasOwnProperty.call(endpointDefinitions, key)) {
            const def = endpointDefinitions[key];
            
            apiObj[key] = async (arg: any) => {
                const config = def.query(arg);
                const method = (config.method || ((def.type === 'query') ? 'GET' : 'POST')).toUpperCase() as HttpMethod;
                
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
                   headers: requestHeaders
                });
            }
        }
    }

    return apiObj;
}
