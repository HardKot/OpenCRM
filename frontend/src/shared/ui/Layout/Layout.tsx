import { Box, Grid, Paper, SxProps } from "@mui/material";
import { PropsWithChildren } from "react";

interface LayoutProps extends PropsWithChildren {
  size?: "grow" | "auto" | number;
  minHeight?: number | string;
  padding?: number | string;
  paddingX?: number | string;
  paddingY?: number | string;
  sx?: SxProps;
}

interface LayoutGridProps extends LayoutProps {
  column?: number | string;
}

const LayoutPaper = ({
  children,
  minHeight,
  padding,
  paddingX = 2,
  paddingY = 1,
}: LayoutProps) => (
  <Paper
    square
    variant="elevation"
    elevation={2}
    sx={{ paddingX, paddingY, borderRadius: 2, minHeight, padding }}
  >
    {children}
  </Paper>
);

const LayoutGrid = ({ children, padding, paddingX, paddingY }: LayoutProps) => (
  <Grid container spacing={3} sx={{ padding, paddingX, paddingY }}>
    {children}
  </Grid>
);

const LayoutItem = ({
  children,
  size = "grow",
  minHeight,
  padding,
  paddingX = 2,
  paddingY = 1,
  column,
}: LayoutGridProps) => (
  <Grid
    size={size}
    sx={{ minHeight, padding, paddingX, paddingY, gridColumn: column }}
  >
    {children}
  </Grid>
);

const LayoutItemPaper = ({
  children,
  size = "grow",
  minHeight,
  padding,
  paddingX = 2,
  paddingY = 1,
  column,
}: LayoutGridProps) => (
  <Grid size={size} sx={{ minHeight, gridColumn: column }}>
    <Paper
      square
      variant="elevation"
      elevation={2}
      sx={{ paddingX, paddingY, borderRadius: 2, minHeight, padding }}
    >
      {children}
    </Paper>
  </Grid>
);

const Layout = ({
  children,
  minHeight,
  padding,
  paddingX,
  paddingY,
  size,
}: LayoutProps) => (
  <Box
    sx={{
      minHeight,
      padding,
      paddingX,
      paddingY,
      flexGrow: size === "grow" ? 1 : 0,
    }}
  >
    {children}
  </Box>
);

Layout.Paper = LayoutPaper;
Layout.Grid = LayoutGrid;
Layout.Item = LayoutItem;
Layout.ItemPaper = LayoutItemPaper;

export { Layout };
