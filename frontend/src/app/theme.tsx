import { createTheme } from '@mui/material/styles';

const appTheme = createTheme({
  palette: {
    primary: {
      light: '#ffac33',
      main: '#ff9800',
      dark: '#b26a00',
    },
    secondary: {
      light: '#ffee33',
      main: '#ffea00',
      dark: '#b2a300',
    },
  },
});

export { appTheme };
