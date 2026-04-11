import { Tabs as MuiTabs, Tab as MuiTab, Box } from "@mui/material";
import { ReactElement, useState } from "react";
import { IconName } from "../Icon";
import { TabsNavigation } from "./TabsNavigation";

interface Tab {
  label: string;
  icon?: IconName;
  Component: ReactElement;
  disabled?: boolean;
}

interface TabsProps {
  tabs: Tab[];
}

const Tabs = ({ tabs }: TabsProps) => {
  const [tab, setTab] = useState(0);
  return (
    <>
      <Box
        sx={{
          width: "100%",
          borderBottom: 1,
          borderColor: "divider",
          bgcolor: "background.paper",
        }}
      >
        <MuiTabs
          value={tab}
          onChange={(e, newValue) => setTab(newValue)}
          textColor="inherit"
          indicatorColor="primary"
          sx={{ color: "text.secondary" }}
        >
          {tabs.map((t, index) => (
            <MuiTab
              key={index}
              label={t.label}
              disabled={t.disabled}
              sx={{ "&.Mui-selected": { color: "text.primary" } }}
            />
          ))}
        </MuiTabs>
      </Box>
      <Box sx={{ padding: 2 }}>{tabs[tab].Component}</Box>
    </>
  );
};

Tabs.Navigation = TabsNavigation;

export { Tabs };
