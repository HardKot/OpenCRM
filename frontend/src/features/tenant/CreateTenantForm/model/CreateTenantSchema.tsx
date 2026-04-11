import { ITranslation } from "#shared/index";
import { InferType, object, string } from "yup";

const createTenantSchema = (t: ITranslation) =>
  object({
    email: string()
      .email(t("createTenant.validation.email.email"))
      .required(t("createTenant.validation.email.required")),
  }).required();

export type CreateTenantSchema = InferType<
  ReturnType<typeof createTenantSchema>
>;

export { createTenantSchema };
