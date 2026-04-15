import { UserPermission } from "#entities/User";
import { useHasPermission } from "#features/auth/AccessPermission";
import { useI18n } from "#shared/hooks";
import { Tabs } from "#shared/ui";
import { useMemo } from "react";
import { NavigationTo } from "../libs/types";

interface NavigationMenuProps {
  hrefMap: (key: NavigationTo) => string;
}

const NavigationMenu = ({ hrefMap }: NavigationMenuProps) => {
  const { t } = useI18n();
  const showEmployeeTab = useHasPermission(UserPermission.EmployeeRead);

  const tabs = useMemo(() => {
    const tabs = [];
    if (showEmployeeTab) {
      tabs.push({
        label: "navigation.employee",
        href: NavigationTo.Employee,
        disabled: false,
      });
    }
    return tabs;
  }, [showEmployeeTab]);

  return (
    <Tabs.Navigation
      tabs={tabs.map((it) => ({
        label: t(it.label),
        href: hrefMap(it.href),
        disabled: it.disabled,
      }))}
    />
  );
};

export { NavigationMenu };
