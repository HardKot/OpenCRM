import React from "react";
import { Box, BoxProps } from "@mui/material";

export interface ViewProps extends BoxProps {
  component?: React.ElementType;
}

export const View: React.FC<ViewProps> = (props) => {
  return <Box {...props} />;
};
