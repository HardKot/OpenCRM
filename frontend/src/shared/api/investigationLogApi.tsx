import { createApi } from "@reduxjs/toolkit/query/react";
import { PageResponse } from "./types";
import { BaseFetchQuery } from "./api";

export interface InvestigationLogDto {
  id: number;
  createdAt: number;
  author: {
    entityId: number;
    entityName: string;
    label: string;
  };
  details: {
    description: string;
    action: string;
    entityName: string;
    entityId: number;
  };
}

export interface GetInvestigationLogListRequest {
  page: number;
  size: number;
}

const investigationLogApi = createApi({
  reducerPath: "api/investigationLogApi",
  baseQuery: BaseFetchQuery(),
  tagTypes: ["InvestigationLog"],
  endpoints: (build) => ({
    getPageInvestigationLogs: build.query<
      PageResponse<InvestigationLogDto>,
      GetInvestigationLogListRequest
    >({
      query: (args) => ({
        url: "/api/log",
        method: "GET",
        params: {
          page: args.page,
          size: args.size,
        },
      }),
      transformResponse: (data) => ({ models: data }),
    }),
  }),
});

export const useGetPageInvestigationLogs =
  investigationLogApi.useGetPageInvestigationLogsQuery;

export { investigationLogApi };
