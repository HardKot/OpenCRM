import * as yup from "yup";

import { ITranslation } from "#shared/index";

const loginSchema = (t: ITranslation) =>
  yup
    .object({
      username: yup
        .string()
        .email(t("authByUsername.validation.username.email"))
        .required(t("authByUsername.validation.username.required")),
      password: yup
        .string()
        .required(t("authByUsername.validation.password.required")),
    })
    .required();

export type LoginSchema = yup.InferType<ReturnType<typeof loginSchema>>;

export { loginSchema };
