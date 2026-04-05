import { InferType, object, string } from "yup";

const EmployeeFilterSchema = object({
    fullnameLike: string().optional().default(""),
    position: string().optional().default(""),
})

type IEmployeeFilter = InferType<typeof EmployeeFilterSchema>;

export type { IEmployeeFilter };
export { EmployeeFilterSchema }