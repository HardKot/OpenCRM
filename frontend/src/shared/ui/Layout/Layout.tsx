import { Grid, Paper } from "@mui/material";
import { PropsWithChildren } from "react";

interface LayoutProps extends PropsWithChildren {
    size?: "grow" | "auto" | number;
    minHeight?: number | string;
    padding?: number | string;
    paddingX?: number | string;
    paddingY?: number | string;
}


const LayoutPaper = ({ children, minHeight, padding, paddingX = 2, paddingY = 1 }: LayoutProps) => (
    <Paper square variant="elevation" elevation={2} sx={{ paddingX, paddingY, borderRadius: 2, minHeight, padding }}>
        {children}
    </Paper>
)

const LayoutGrid = ({ children, padding, paddingX, paddingY }: LayoutProps) => (
    <Grid container spacing={3} sx={{ padding, paddingX, paddingY }}>
        {children}
    </Grid>   
)

const LayoutItem = ({ children, size = "grow", minHeight, padding, paddingX = 2, paddingY = 1 }: LayoutProps) => (
    <Grid size={size} sx={{ minHeight, padding, paddingX, paddingY }}>
        {children}
    </Grid>
)

const LayoutItemPaper = ({ children, size = "grow", minHeight, padding, paddingX = 2, paddingY = 1 }: LayoutProps) => (
    <Grid size={size} sx={{ minHeight }}>
        <Paper square variant="elevation" elevation={2} sx={{ paddingX, paddingY, borderRadius: 2, minHeight, padding }}>
            {children}
        </Paper>
    </Grid>
)


const Layout = () => {
    return null;
}

Layout.Paper = LayoutPaper;
Layout.Grid = LayoutGrid;
Layout.Item = LayoutItem;
Layout.ItemPaper = LayoutItemPaper;

export { Layout }