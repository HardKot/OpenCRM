import { PropsWithChildren, useMemo } from "react";
import { ThemeProvider as MuiThemeProvider } from "@mui/material/styles";
import { CssBaseline } from "@mui/material";
import { createAppTheme } from "#app/theme";
import { useAppSelector } from "#shared/index";
import { useSystemTheme } from "#app/libs/useSystemTheme";

const ThemeProvider = ({ children }: PropsWithChildren) => {
  const themeMode = useAppSelector((state) => state.appConfig.theme);
  const systemTheme = useSystemTheme();

  const currentMode = useMemo(() => {
    if (themeMode !== "system") return themeMode;

    const isDark = systemTheme === "dark";
    if (isDark) return "dark";
    return "light";
  }, [themeMode, systemTheme]);

  const theme = useMemo(() => createAppTheme(currentMode), [currentMode]);

  return (
    <MuiThemeProvider theme={theme}>
      <CssBaseline />
      {children}
    </MuiThemeProvider>
  );
};

export { ThemeProvider };
