import { CreateTenantForm } from "#features/tenant/CreateTenantForm";
import { useI18n } from "#shared/hooks";

export const CreateTenant = () => {
  const { t } = useI18n();

  const onSuccess = () => {
    alert(t("createTenant.checkEmailAlert"));
  };

  return <CreateTenantForm onSuccess={onSuccess} />;
};
