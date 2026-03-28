import { createTheme, PaletteMode } from '@mui/material/styles';

const createAppTheme = (mode: PaletteMode) => {
  return createTheme({
    palette: {
      mode,
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
      text: {
        primary: 'rgb(26, 30, 35)'
      }
    },
  });
};

const appTheme = createAppTheme('light');

export { appTheme, createAppTheme };
