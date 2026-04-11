import { Tabs as MuiTabs, Tab as MuiTab } from "@mui/material";
import { IconName } from "../Icon";
import { Link, useLocation } from "react-router-dom";
import { useMemo } from "react";

interface TabTo {
  label: string;
  icon?: IconName;
  href: string;
  disabled?: boolean;
}

interface RootTabTo extends TabTo {
  list?: TabTo[];
}

interface TabsProps {
  tabs: RootTabTo[];
}

const TabsNavigation = ({ tabs }: TabsProps) => {
  const { pathname } = useLocation();

  const tabsHref = useMemo(
    () =>
      tabs
        .map((it) => [[it], it.list ?? []])
        .flat(2)
        .map((it) => it.href),
    [tabs],
  );

  let currentPathName: string | boolean = pathname;
  if (!tabsHref.includes(currentPathName)) currentPathName = false;

  return (
    <MuiTabs
      textColor="inherit"
      indicatorColor="primary"
      role="navigation"
      value={currentPathName}
    >
      {tabs.map((t) => (
        <MuiTab
          component={Link}
          key={`tab-${t.href}`}
          label={t.label}
          disabled={t.disabled}
          to={t.href}
          value={t.href}
        />
      ))}
      <MuiTab
        component={Link}
        key={`tab-unknown`}
        label={"unknown"}
        disabled
        to={"/"}
        value={"/"}
        style={{ display: "none" }}
      />
    </MuiTabs>
  );
};

export { TabsNavigation };
