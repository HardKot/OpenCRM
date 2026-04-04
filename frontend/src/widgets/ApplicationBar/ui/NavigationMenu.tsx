import { useI18n } from "#shared/hooks";
import { Tabs } from "#shared/ui";
import { NavigationTo } from "../libs/types";


interface NavigationMenuProps {
  hrefMap: (key: NavigationTo) => string;
}

const NavigationMenu = ({ hrefMap }: NavigationMenuProps) => {
  const { t } = useI18n()
  return (
    <Tabs.Navigation 
      tabs={[
        {
          label: t("navigation.employee"),
          href: hrefMap(NavigationTo.Employee)
        }
      ]}
    />
)};


export { NavigationMenu }