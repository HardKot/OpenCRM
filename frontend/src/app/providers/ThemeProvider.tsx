import { PropsWithChildren } from "react";
import { ThemeProvider as MuiThemeProvider } from '@mui/material/styles';
import { appTheme } from "#app/theme";

const ThemeProvider = ({ children }: PropsWithChildren) => {

    return (
        <MuiThemeProvider theme={appTheme} noSsr>
            {children}
        </MuiThemeProvider>
    )
}


export { ThemeProvider };
