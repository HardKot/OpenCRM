export interface PageResponse<T> {
    totalElements: number;
    totalPages: number;
    models: T[];
}