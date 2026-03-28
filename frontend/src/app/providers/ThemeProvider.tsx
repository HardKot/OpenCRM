import { PropsWithChildren, useEffect, useMemo, useState } from "react";
import { ThemeProvider as MuiThemeProvider } from '@mui/material/styles';
import { PaletteMode } from '@mui/material/styles';
import { createAppTheme } from "#app/theme";
import { useAppSelector } from "#shared/index";

const ThemeProvider = ({ children }: PropsWithChildren) => {
    const themeMode = useAppSelector(state => state.appConfig.theme);
    const [currentMode, setCurrentMode] = useState<PaletteMode>('light');

    useEffect(() => {
        if (themeMode === 'system') {
            const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)');
            const isDark = mediaQuery.matches;
            setCurrentMode(isDark ? 'dark' : 'light');

            const handler = (e: MediaQueryListEvent) => {
                setCurrentMode(e.matches ? 'dark' : 'light');
            };
            mediaQuery.addEventListener('change', handler);
            return () => mediaQuery.removeEventListener('change', handler);
        } else {
            setCurrentMode(themeMode);
        }
    }, [themeMode]);

    const theme = useMemo(() => createAppTheme(currentMode), [currentMode]);

    return (
        <MuiThemeProvider theme={theme}>
            {children}
        </MuiThemeProvider>
    )
}


export { ThemeProvider };
