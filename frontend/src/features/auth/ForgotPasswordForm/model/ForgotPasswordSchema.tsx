import * as yup from "yup";

import { ITranslation } from "#shared/index";

const forgotPasswordSchema = (t: ITranslation) =>
  yup
    .object({
      username: yup
        .string()
        .email(t("forgotPassword.validation.username.email"))
        .required(t("forgotPassword.validation.username.required")),
    })
    .required();

export type ForgotPasswordSchema = yup.InferType<
  ReturnType<typeof forgotPasswordSchema>
>;

export { forgotPasswordSchema };
