import { ITranslation } from "#shared/index";
import { InferType, object, string } from "yup";

const EmployeeFormSchema = (t: ITranslation) =>
  object({
    firstname: string().max(255).default(""),
    lastname: string().max(255).default(""),
    patronymic: string().max(255).default(""),
    position: string().max(255).default(""),
    email: string()
      .email(t("forms.employee.email.invalid"))
      .max(255)
      .default("")
      .optional(),
    phone: string()
      .matches(/^\+?[1-9]\d{1,14}$/, t("forms.employee.phone.invalid"))
      .max(20)
      .default("")
      .optional(),
  }).required();

type IEmployeeForm = InferType<ReturnType<typeof EmployeeFormSchema>>;

export { EmployeeFormSchema };
export type { IEmployeeForm };
