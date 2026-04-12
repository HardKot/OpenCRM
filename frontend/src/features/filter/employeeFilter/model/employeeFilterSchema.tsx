import { InferType, object, string } from "yup";

const EmployeeFilterSchema = object({
  fullnameLike: string().optional().default(""),
  positionSuggest: string().optional().default(""),
  email: string().optional().default(""),
  phoneLike: string().optional().default(""),
});

type IEmployeeFilter = InferType<typeof EmployeeFilterSchema>;

export type { IEmployeeFilter };
export { EmployeeFilterSchema };
