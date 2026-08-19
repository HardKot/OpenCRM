import { ForgotPasswordForm } from "#features/auth/ForgotPasswordForm";
import { useI18n } from "#shared/hooks/appHooks";

const ForgotPassword = () => {
  const { t } = useI18n();

  return (
    <ForgotPasswordForm
      onSuccess={() => alert(t("forgotPassword.checkEmailAlert"))}
    />
  );
};

export { ForgotPassword };
