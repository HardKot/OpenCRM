export interface PageResponse<T> {
  totalElements: number;
  totalPages: number;
  models: T[];
}

export type OptionalId<T extends { id: any }> = Omit<T, "id"> & {
  id?: T["id"];
};

export interface SuggestResponse<T> {
  items: T[];
}
