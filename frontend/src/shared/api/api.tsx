import { fetchBaseQuery } from "@reduxjs/toolkit/query";

const BASE_URL: string = import.meta.env.VITE_API_URL ?? "";

const BaseFetchQuery = (): ReturnType<typeof fetchBaseQuery> => {
  return fetchBaseQuery({
    credentials: "include",
    timeout: 60_000,
  });
};

export { BaseFetchQuery, BASE_URL };
