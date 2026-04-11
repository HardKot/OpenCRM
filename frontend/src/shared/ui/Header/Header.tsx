import React from "react";
import { Typography, TypographyProps } from "@mui/material";

export interface HeaderProps extends TypographyProps {
  level?: 1 | 2 | 3 | 4 | 5 | 6;
}

export const Header: React.FC<HeaderProps> = ({
  level = 1,
  variant,
  ...props
}) => {
  const mapLevel = {
    1: "h1",
    2: "h2",
    3: "h3",
    4: "h4",
    5: "h5",
    6: "h6",
  } as const;

  const variantMap = {
    1: "h4",
    2: "h5",
    3: "h6",
    4: "subtitle1",
    5: "subtitle2",
    6: "body1",
  } as const;

  return (
    <Typography
      variant={variant || variantMap[level]}
      component={mapLevel[level]}
      fontWeight="bold"
      {...props}
    />
  );
};
