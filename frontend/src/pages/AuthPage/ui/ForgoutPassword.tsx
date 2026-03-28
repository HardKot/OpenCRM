import { ForgoutPasswordForm } from "#features/auth/ForgoutPasswordForm";
import { useI18n } from "#shared/hooks/appHooks";

const ForgoutPassword = () => {
    const { t } = useI18n();

    return (
        <ForgoutPasswordForm onSuccess={() => alert(t('forgoutPassword.checkEmailAlert'))} />
    );
};

export { ForgoutPassword }