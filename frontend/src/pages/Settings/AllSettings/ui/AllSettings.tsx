import { useI18n } from "#shared/hooks";
import { Tabs, View } from "#shared/ui";
import { Outlet } from "react-router-dom";

const AllSettings = () => {
  const { t } = useI18n();
  return (
    <View>
      <Tabs.Navigation
        tabs={[
          { label: t("settings.account"), href: "/settings" },
          { label: t("settings.logs"), href: "/settings/logs" },
        ]}
      />
      <Outlet />
    </View>
  );
};

export { AllSettings };
