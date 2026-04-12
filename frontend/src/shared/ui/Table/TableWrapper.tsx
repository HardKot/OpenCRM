import { Paper, SxProps } from "@mui/material";
import { PropsWithChildren } from "react";

interface TableWrapperProps extends PropsWithChildren {
  sx?: SxProps;
}

const TableWrapper = ({ children, sx = {} }: TableWrapperProps) => (
  <Paper
    elevation={2}
    sx={{
      borderRadius: 2,
      gridColumn: { xs: "span 1", md: "1 / -1" },
      ...sx,
    }}
  >
    {children}
  </Paper>
);

export { TableWrapper };
