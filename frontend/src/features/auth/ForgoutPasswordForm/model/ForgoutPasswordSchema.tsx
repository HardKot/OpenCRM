import * as yup from "yup";

import { ITranslation } from "#shared/index";


const forgoutPasswordSchema = (t: ITranslation) => yup.object({
    username: yup.string().email(t("forgoutPassword.validation.username.email")).required(t("forgoutPassword.validation.username.required")),
}).required();

export type ForgoutPasswordSchema = yup.InferType<ReturnType<typeof forgoutPasswordSchema>>;

export { forgoutPasswordSchema }