import { ITranslation } from "#shared/index";
import { InferType, object, ref, string } from "yup";

const changePasswordSchema = (t: ITranslation) =>
  object({
    password: string().required(t("changePassword.currentPasswordRequired")),
    newPassword: string().required(t("changePassword.newPasswordRequired")),
    confirmPassword: string()
      .required(t("changePassword.confirmPasswordRequired"))
      .oneOf([ref("newPassword")], t("changePassword.passwordsDoNotMatch")),
  });

type ChangePasswordForm = InferType<ReturnType<typeof changePasswordSchema>>;

export { changePasswordSchema };
export type { ChangePasswordForm };
