import { fetchBaseQuery } from "@reduxjs/toolkit/query";

const BASE_URL: string = (import.meta as any).env.VITE_API_URL || 'http://localhost:8080';

const BaseFetchQuery = (options: { url?: string }) => fetchBaseQuery({
    baseUrl: `${BASE_URL}/${options.url ?? ''}`.replace(/\/+$/, '/')
})

export { BaseFetchQuery, BASE_URL }